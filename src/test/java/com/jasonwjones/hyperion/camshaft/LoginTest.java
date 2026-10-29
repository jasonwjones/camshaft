package com.jasonwjones.hyperion.camshaft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Where a login's parts come from: options, then a named profile, then the
 * environment, then the default profile.
 */
public class LoginTest {

	@TempDir
	Path temp;

	private final Map<String, String> env = new HashMap<>();

	private final Function<String, String> getenv = env::get;

	@BeforeEach
	public void profiles() throws IOException {
		Path profiles = temp.resolve("profiles.properties");
		Files.writeString(profiles, String.join("\n",
				"dev.url = http://dev:9000/essbase",
				"dev.user = devuser",
				"dev.password = devpass",
				"default.url = http://default:9000/essbase",
				"default.user = defaultuser",
				"default.password = defaultpass",
				"cloud.url = https://cloud/essbase",
				"cloud.auth = token",
				""));
		env.put("CAMSHAFT_PROFILES", profiles.toString());
	}

	private Login resolve(String url, String user, String profile, String auth) throws CamshaftException {
		return Login.resolve(url, user, profile, auth, "CAMSHAFT_", getenv, null, null);
	}

	@Test
	public void takesOptionsFirst() throws CamshaftException {
		Login login = resolve("http://option:9000/essbase", "optionuser", "dev", null);
		assertEquals("http://option:9000/essbase", login.url());
		assertEquals("optionuser", login.user());
		assertEquals("devpass", login.password(), "nothing on the command line gives a password");
	}

	@Test
	public void takesANamedProfileOverTheEnvironment() throws CamshaftException {
		env.put("CAMSHAFT_URL", "http://env:9000/essbase");
		env.put("CAMSHAFT_PASSWORD", "envpass");
		Login login = resolve(null, null, "dev", null);
		assertEquals("http://dev:9000/essbase", login.url());
		assertEquals("devpass", login.password());
	}

	@Test
	public void takesTheEnvironmentOverTheDefaultProfile() throws CamshaftException {
		env.put("CAMSHAFT_URL", "http://env:9000/essbase");
		env.put("CAMSHAFT_USER", "envuser");
		env.put("CAMSHAFT_PASSWORD", "envpass");
		assertEquals(new Login("http://env:9000/essbase", "envuser", "envpass", "password"), resolve(null, null, null, null));
	}

	@Test
	public void fallsBackToTheDefaultProfile() throws CamshaftException {
		assertEquals(new Login("http://default:9000/essbase", "defaultuser", "defaultpass", "password"), resolve(null, null, null, null));
	}

	@Test
	public void needsNoUserForAToken() throws CamshaftException {
		env.put("CAMSHAFT_PASSWORD", "eyJ0b2tlbg");
		Login login = resolve(null, "", "cloud", null);
		assertEquals("token", login.auth());
		assertEquals("https://cloud/essbase", login.url());
		assertEquals("eyJ0b2tlbg", login.password());
	}

	@Test
	public void saysWhatIsMissing() throws IOException {
		Files.delete(Path.of(env.get("CAMSHAFT_PROFILES")));
		CamshaftException noUrl = assertThrows(CamshaftException.class, () -> resolve(null, "admin", null, null));
		assertTrue(noUrl.getMessage().startsWith("Which Essbase server?"), noUrl.getMessage());

		CamshaftException noPassword = assertThrows(CamshaftException.class, () -> resolve("http://x:9000/essbase", "admin", null, null));
		assertTrue(noPassword.getMessage().contains("set CAMSHAFT_PASSWORD"), noPassword.getMessage());

		env.put("CAMSHAFT_PASSWORD", "secret");
		CamshaftException noUser = assertThrows(CamshaftException.class, () -> resolve("http://x:9000/essbase", null, null, null));
		assertTrue(noUser.getMessage().startsWith("Which user?"), noUser.getMessage());
	}

	@Test
	public void refusesAProfileThatIsNotThere() {
		CamshaftException e = assertThrows(CamshaftException.class, () -> resolve(null, null, "prod", null));
		assertTrue(e.getMessage().startsWith("There's no profile named prod"), e.getMessage());
	}

	@Test
	public void borrowsAPasswordOnlyForTheSameUser() throws CamshaftException, IOException {
		Files.delete(Path.of(env.get("CAMSHAFT_PROFILES")));
		Login left = new Login("http://left:9000/essbase", "admin", "leftpass", "password");
		Login same = Login.resolve("http://right:9000/essbase", "admin", null, null, "CAMSHAFT_RIGHT_", getenv, null, left);
		assertEquals("leftpass", same.password());
		assertThrows(CamshaftException.class,
				() -> Login.resolve("http://right:9000/essbase", "other", null, null, "CAMSHAFT_RIGHT_", getenv, null, left));
	}

	@Test
	public void keepsThePasswordOutOfItsDescription() {
		Login login = new Login("http://x:9000/essbase", "admin", "secret", "password");
		assertEquals("admin at http://x:9000/essbase", login.toString());
		assertFalse(login.toString().contains("secret"));
		assertNull(new Login("http://x:9000/essbase", null, "t", "token").user());
	}

}
