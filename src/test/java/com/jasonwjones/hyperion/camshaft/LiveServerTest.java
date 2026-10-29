package com.jasonwjones.hyperion.camshaft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import picocli.CommandLine;

/**
 * Runs the commands against a real Essbase server with Sample.Basic, when
 * given one:
 *
 * <pre>
 * CAMSHAFT_URL=http://localhost:9000/essbase CAMSHAFT_USER=admin CAMSHAFT_PASSWORD=... mvn test
 * </pre>
 *
 * Skipped otherwise. Everything here only reads.
 */
public class LiveServerTest {

	private static final String SALES = "SELECT {[Jan], [Feb]} ON COLUMNS, CROSSJOIN({[East], [West]}, [Colas].Children) ON ROWS "
			+ "FROM Sample.Basic WHERE ([Sales], [Actual])";

	@TempDir
	Path temp;

	@BeforeAll
	public static void needsAServer() {
		assumeTrue(System.getenv("CAMSHAFT_URL") != null && System.getenv("CAMSHAFT_USER") != null
				&& System.getenv("CAMSHAFT_PASSWORD") != null, "CAMSHAFT_URL, CAMSHAFT_USER and CAMSHAFT_PASSWORD are not set");
	}

	private static int run(String... args) {
		CommandLine commandLine = Camshaft.commandLine();
		StringWriter err = new StringWriter();
		commandLine.setErr(new PrintWriter(err));
		int status = commandLine.execute(args);
		if (status == Camshaft.FAILED) {
			System.err.println(err);
		}
		return status;
	}

	private static String read(Path path) throws IOException {
		return Files.readString(path, StandardCharsets.UTF_8);
	}

	@Test
	public void writesAQueryToCsv() throws IOException {
		Path csv = temp.resolve("sales.csv");
		assertEquals(Camshaft.OK, run("run", SALES, "-o", csv.toString()));
		assertTrue(read(csv).startsWith("Market,Product,Jan,Feb\nEast,100-10,1812,1754\n"), read(csv));
	}

	@Test
	public void writesAFactTableByAlias() throws IOException {
		Path json = temp.resolve("sales.jsonl");
		assertEquals(Camshaft.OK, run("run", SALES, "-o", json.toString(), "--shape", "long", "--pov", "--members", "alias", "--names", "lower"));
		assertTrue(read(json).startsWith("{\"market\":\"East\",\"product\":\"Cola\",\"measures\":\"Sales\",\"scenario\":\"Actual\",\"year\":\"Jan\",\"value\":1812}\n"),
				read(json));
	}

	@Test
	public void writesEssbasesOwnExcel() throws IOException {
		Path xlsx = temp.resolve("sales.xlsx");
		assertEquals(Camshaft.OK, run("run", SALES, "-o", xlsx.toString()));
		byte[] bytes = Files.readAllBytes(xlsx);
		assertTrue(bytes.length > 4 && bytes[0] == 'P' && bytes[1] == 'K', "an xlsx is a zip");
	}

	@Test
	public void listsWhatsOnTheServer() throws IOException {
		Path apps = temp.resolve("apps.csv");
		Path cubes = temp.resolve("cubes.csv");
		Path dims = temp.resolve("dims.csv");
		Path members = temp.resolve("members.csv");
		assertEquals(Camshaft.OK, run("apps", "-o", apps.toString()));
		assertEquals(Camshaft.OK, run("cubes", "Sample", "-o", cubes.toString()));
		assertEquals(Camshaft.OK, run("dims", "Sample.Basic", "-o", dims.toString()));
		assertEquals(Camshaft.OK, run("members", "Sample.Basic", "Product", "--level", "0", "-o", members.toString()));

		assertTrue(read(apps).contains("\nSample\n"), read(apps));
		assertTrue(read(cubes).contains("\nSample,Basic\n"), read(cubes));
		assertTrue(read(dims).startsWith("DIMENSION,STORAGE,MEMBERS,STORED\nYear,dense,"), read(dims));
		assertTrue(read(members).startsWith("MEMBER,ALIAS,LEVEL,GENERATION\n100-10,Cola,0,3\n"), read(members));
	}

	@Test
	public void takesAliasesFromAnotherTableWithoutRenamingTheMembers() throws IOException {
		Path members = temp.resolve("months.csv");
		assertEquals(Camshaft.OK, run("members", "Sample.Basic", "Year", "--children", "Qtr1", "--alias-table", "Long Names", "-o", members.toString()));
		assertEquals("MEMBER,ALIAS,LEVEL,GENERATION\nJan,January,0,3\nFeb,February,0,3\nMar,March,0,3\n", read(members));
	}

	@Test
	public void runsAFolderOfQueries() throws IOException {
		Path queries = Files.createDirectories(temp.resolve("queries"));
		Files.writeString(queries.resolve("sales.mdx"), SALES);
		Files.writeString(queries.resolve("products.mdx"), "SELECT {} ON 0, [Colas].Children ON 1 FROM Sample.Basic");
		Path out = temp.resolve("out");
		assertEquals(Camshaft.OK, run("batch", queries.toString(), "-d", out.toString(), "-f", "tsv"));
		assertTrue(read(out.resolve("sales.tsv")).startsWith("Market\tProduct\tJan\tFeb\n"));
		assertEquals("Product\n100-10\n100-20\n100-30\n", read(out.resolve("products.tsv")));
	}

	@Test
	public void comparesAServerWithItself() {
		String url = System.getenv("CAMSHAFT_URL").replace("localhost", "127.0.0.1");
		assumeTrue(!url.equals(System.getenv("CAMSHAFT_URL")), "needs a second address for the same server");
		assertEquals(Camshaft.OK, run("diff", SALES, "--right-url", url, "-o", temp.resolve("diff.csv").toString()));
	}

	@Test
	public void refusesToCompareCubesForAQueryThatNamesOne() {
		assertEquals(Camshaft.FAILED, run("diff", SALES, "--right-cube", "Sample.Basic"));
	}

}
