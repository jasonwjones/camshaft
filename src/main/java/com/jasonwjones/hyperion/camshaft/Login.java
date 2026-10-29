package com.jasonwjones.hyperion.camshaft;

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import com.appliedolap.essbase.EssAuthentication;
import com.appliedolap.essbase.EssServer;
import com.appliedolap.essbase.impl.EssServerImpl;

/**
 * Where to connect and how to log in: an Essbase server's address, a user, a
 * password, and how to present it. The password can be a bearer token or the
 * id of an existing session instead, for servers behind an identity provider.
 *
 * @param url the server's address, such as http://localhost:9000/essbase
 * @param user the user, which a token or a session doesn't need
 * @param password the password, token or session id
 * @param auth password, token or session
 */
public record Login(String url, String user, String password, String auth) {

	/** Where profiles are kept, unless CAMSHAFT_PROFILES says otherwise */
	static final String PROFILES_FILE = ".camshaft.properties";

	/**
	 * Works out a login from what was given, most specific first: the options,
	 * then a profile the options name, then the environment, then the profile
	 * named default. A password nothing gives is asked for.
	 *
	 * @param url the address from the options, or null
	 * @param user the user from the options, or null
	 * @param profile the profile the options name, or null
	 * @param auth how to log in, from the options, or null
	 * @param prefix the prefix of the environment variables to read, such as
	 *            CAMSHAFT_ for CAMSHAFT_URL
	 * @param env reads an environment variable
	 * @param console where to ask for a password, or null if there's nowhere
	 * @param sameUser a login whose password to use, if this one is for the same
	 *            user and nothing else gives a password; or null
	 * @return the login
	 * @throws CamshaftException if no address can be found, or no password and
	 *             nowhere to ask for one
	 */
	static Login resolve(String url, String user, String profile, String auth, String prefix, Function<String, String> env,
			Console console, Login sameUser) throws CamshaftException {
		Properties profiles = profiles(env);
		if (profile != null && !hasProfile(profiles, profile)) {
			throw new CamshaftException("There's no profile named " + profile + " in " + profilesPath(env));
		}
		String fallback = hasProfile(profiles, "default") ? "default" : null;
		url = first(url, setting(profiles, profile, "url"), env.apply(prefix + "URL"), setting(profiles, fallback, "url"));
		if (url == null) {
			throw new CamshaftException("Which Essbase server? Give --url, set " + prefix + "URL, or use a profile");
		}
		user = first(user, setting(profiles, profile, "user"), env.apply(prefix + "USER"), setting(profiles, fallback, "user"));
		auth = first(auth, setting(profiles, profile, "auth"), env.apply(prefix + "AUTH"), setting(profiles, fallback, "auth"), "password");
		auth = auth.toLowerCase(Locale.ROOT);
		String password = first(setting(profiles, profile, "password"), env.apply(prefix + "PASSWORD"), setting(profiles, fallback, "password"));
		if (password == null && sameUser != null && user != null && user.equals(sameUser.user()) && auth.equals(sameUser.auth())) {
			password = sameUser.password();
		}
		if (password == null) {
			if (console == null) {
				throw new CamshaftException("No password for " + url + ": set " + prefix + "PASSWORD or put one in a profile");
			}
			String what = auth.equals("token") ? "Token" : auth.equals("session") ? "Session id" : "Password";
			char[] typed = console.readPassword("%s for %s%s: ", what, user == null ? "" : user + " at ", url);
			password = typed == null ? "" : new String(typed);
		}
		if (auth.equals("password") && (user == null || user.isEmpty())) {
			throw new CamshaftException("Which user? Give --user, set " + prefix + "USER, or use a profile");
		}
		return new Login(url, user, password, auth);
	}

	@SafeVarargs
	private static <T> T first(T... values) {
		for (T value : values) {
			if (value != null && !(value instanceof String && ((String) value).isBlank())) {
				return value;
			}
		}
		return null;
	}

	static Path profilesPath(Function<String, String> env) {
		String path = env.apply("CAMSHAFT_PROFILES");
		return path != null ? Paths.get(path) : Paths.get(System.getProperty("user.home"), PROFILES_FILE);
	}

	private static Properties profiles(Function<String, String> env) throws CamshaftException {
		Properties profiles = new Properties();
		Path path = profilesPath(env);
		if (Files.isRegularFile(path)) {
			try (InputStream in = Files.newInputStream(path)) {
				profiles.load(in);
			} catch (IOException e) {
				throw new CamshaftException("Could not read the profiles in " + path + ": " + e.getMessage(), e);
			}
		}
		return profiles;
	}

	private static boolean hasProfile(Properties profiles, String profile) {
		return profiles.stringPropertyNames().stream().anyMatch(key -> key.startsWith(profile + "."));
	}

	private static String setting(Properties profiles, String profile, String key) {
		return profile == null ? null : profiles.getProperty(profile + "." + key);
	}

	/**
	 * A Thriller connection with this login, which then runs MDX as JDBC.
	 *
	 * @param settings Thriller's settings for the queries, such as members=alias
	 * @return the connection
	 * @throws SQLException if the server can't be reached or the login fails
	 */
	Connection connect(Map<String, String> settings) throws SQLException {
		Properties info = new Properties();
		info.putAll(settings);
		if (user != null) {
			info.setProperty("user", user);
		}
		info.setProperty("password", password);
		info.setProperty("auth", auth);
		return DriverManager.getConnection("jdbc:essbase:thriller:" + url, info);
	}

	/**
	 * The server itself, through essbase-rest-client, for what JDBC doesn't
	 * cover: listing what's on it, and Essbase's own Excel output.
	 *
	 * @return the server
	 * @throws CamshaftException if auth isn't one Camshaft knows
	 */
	EssServer server() throws CamshaftException {
		switch (auth) {
		case "password":
			return new EssServerImpl(url, user, password);
		case "token":
			return new EssServerImpl(url, EssAuthentication.bearerToken(password));
		case "session":
			return new EssServerImpl(url, EssAuthentication.sessionCookie(password, null));
		default:
			throw new CamshaftException("auth can be password, token or session, not '" + auth + "'");
		}
	}

	/**
	 * The login, without its password, for messages.
	 */
	@Override
	public String toString() {
		return (user == null ? "" : user + " at ") + url;
	}

}
