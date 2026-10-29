package com.jasonwjones.hyperion.camshaft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.jasonwjones.hyperion.thriller.ObjectResultSet;
import com.jasonwjones.hyperion.thriller.ThrillerResultSetMetaData;
import com.jasonwjones.hyperion.thriller.table.ThrillerColumn;

/**
 * Formats by file name, cube names, where queries come from, the member
 * queries, and reading a result set.
 */
public class SmallPartsTest {

	@TempDir
	Path temp;

	@Test
	public void knowsAFormatByItsExtension() {
		assertEquals(Format.CSV, Format.fromFileName("sales.csv"));
		assertEquals(Format.MARKDOWN, Format.fromFileName("README.markdown"));
		assertEquals(Format.JSONL, Format.fromFileName("rows.NDJSON"));
		assertEquals(Format.XLSX, Format.fromFileName("report.xlsx"));
		assertNull(Format.fromFileName("notes.doc"));
		assertNull(Format.fromFileName("no-extension"));
	}

	@Test
	public void readsACubeNameEitherWay() throws CamshaftException {
		assertEquals(new CubeRef("Sample", "Basic"), CubeRef.parse("Sample.Basic"));
		assertEquals(new CubeRef("ASOsamp", "Basic"), CubeRef.parse("[ASOsamp].[Basic]"));
		assertEquals("[Sample].[Basic]", CubeRef.parse(" Sample . Basic ").mdx());
		assertThrows(CamshaftException.class, () -> CubeRef.parse("Sample"));
	}

	@Test
	public void takesAQueryFromTheCommandLineAFileOrStandardInput() throws Exception {
		Path file = temp.resolve("query.mdx");
		Files.writeString(file, "SELECT {[Jan]} ON 0 FROM Sample.Basic");
		ByteArrayInputStream stdin = new ByteArrayInputStream("SELECT {[Feb]} ON 0 FROM Sample.Basic".getBytes(StandardCharsets.UTF_8));
		ByteArrayInputStream empty = new ByteArrayInputStream(new byte[0]);

		assertEquals("SELECT {[Mar]} ON 0 FROM Sample.Basic", QuerySource.read("SELECT {[Mar]} ON 0 FROM Sample.Basic", empty));
		assertEquals("SELECT {[Jan]} ON 0 FROM Sample.Basic", QuerySource.read(file.toString(), empty));
		assertEquals("SELECT {[Feb]} ON 0 FROM Sample.Basic", QuerySource.read("-", stdin));
		assertThrows(CamshaftException.class, () -> QuerySource.read(null, new ByteArrayInputStream("  \n".getBytes(StandardCharsets.UTF_8))));
	}

	@Test
	public void buildsTheMembersQueryForEachWayOfChoosing() {
		CubeRef basic = new CubeRef("Sample", "Basic");
		MembersCommand.Which all = new MembersCommand.Which();
		assertTrue(MembersCommand.query(basic, "Product", all).contains("{[Product].Members} DIMENSION PROPERTIES"));
		assertTrue(MembersCommand.query(basic, "Product", all).endsWith("FROM [Sample].[Basic]"));

		MembersCommand.Which level = new MembersCommand.Which();
		level.level = 0;
		assertTrue(MembersCommand.query(basic, "Product", level).contains("{[Product].Levels(0).Members}"));

		MembersCommand.Which generation = new MembersCommand.Which();
		generation.generation = 2;
		assertTrue(MembersCommand.query(basic, "[Product]", generation).contains("{[Product].Generations(2).Members}"));

		MembersCommand.Which children = new MembersCommand.Which();
		children.children = "Qtr1";
		assertTrue(MembersCommand.query(basic, "Year", children).contains("{[Qtr1].Children}"));

		MembersCommand.Which descendants = new MembersCommand.Which();
		descendants.descendants = "Odd]Name";
		assertTrue(MembersCommand.query(basic, "Year", descendants).contains("{Descendants([Odd]]Name])}"));
	}

	@Test
	public void dropsTheAxisColumnFromTheMembers() {
		Table withAxis = new Table(List.of(new Table.Column("AXIS", false), new Table.Column("MEMBER", false)),
				List.<Object[]>of(new Object[] { "January", "Jan" }));
		Table members = MembersCommand.withoutAxis(withAxis);
		assertEquals("MEMBER", members.getColumns().get(0).name());
		assertEquals("Jan", members.getRows().get(0)[0]);
	}

	@Test
	public void findsTheQueriesToBatch() throws IOException, CamshaftException {
		Files.writeString(temp.resolve("b.mdx"), "x");
		Files.writeString(temp.resolve("a.mdx"), "x");
		Files.writeString(temp.resolve("notes.txt"), "x");
		Path single = Files.writeString(temp.resolve("single.query"), "x");

		List<Path> queries = BatchCommand.queries(List.of(temp));
		assertEquals(List.of("a.mdx", "b.mdx"), queries.stream().map(path -> path.getFileName().toString()).toList());
		assertEquals(List.of(single), BatchCommand.queries(List.of(single)));
		assertEquals("sales.2024", BatchCommand.baseName(Path.of("sales.2024.mdx")));
		assertThrows(CamshaftException.class, () -> BatchCommand.queries(List.of(temp.resolve("missing"))));
	}

	@Test
	public void readsAResultSetWithItsTypes() throws SQLException {
		ThrillerColumn market = new ThrillerColumn(Types.VARCHAR, 10);
		market.setColumnName("Market");
		ThrillerColumn jan = new ThrillerColumn(Types.DOUBLE, 17);
		jan.setColumnName("Jan");
		ObjectResultSet resultSet = new ObjectResultSet(List.<Object[]>of(new Object[] { "East", 1812.0 }),
				new ThrillerResultSetMetaData(new ThrillerColumn[] { market, jan }));

		Table table = Table.from(resultSet);
		assertEquals(List.of(new Table.Column("Market", false), new Table.Column("Jan", true)), table.getColumns());
		assertEquals(1812.0, table.getRows().get(0)[1]);
	}

}
