package com.jasonwjones.hyperion.camshaft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TableWriterTest {

	private static final Table SALES = new Table(
			List.of(new Table.Column("Market", false), new Table.Column("Product", false), new Table.Column("Jan", true)),
			List.of(new Object[] { "East", "Cola", 1812.0 }, new Object[] { "West", "Diet, \"Lite\"", null }));

	private static String write(TableWriter writer, Table table) throws IOException {
		StringWriter out = new StringWriter();
		writer.write(table, out);
		return out.toString();
	}

	@Test
	public void writesCsvQuotingOnlyWhatNeedsIt() throws IOException {
		assertEquals("Market,Product,Jan\nEast,Cola,1812\nWest,\"Diet, \"\"Lite\"\"\",\n", write(new TableWriter(Format.CSV), SALES));
		assertEquals("\" padded\"", TableWriter.quoted(" padded", ','));
		assertEquals("\"two\nlines\"", TableWriter.quoted("two\nlines", ','));
	}

	@Test
	public void writesTabsWithoutAHeaderAndATextForNulls() throws IOException {
		assertEquals("East\tCola\t1812\nWest\tDiet, \"Lite\"\t#Missing\n".replace("Diet, \"Lite\"", "\"Diet, \"\"Lite\"\"\""),
				write(new TableWriter(Format.TSV).header(false).nullText("#Missing"), SALES));
	}

	@Test
	public void writesNumbersInFullAndRoundsThemOnlyWhenAsked() {
		TableWriter writer = new TableWriter(Format.CSV);
		assertEquals("12345678", writer.text(1.2345678E7));
		assertEquals("0.5", writer.text(0.5));
		assertEquals("1812", writer.text(1812.0));
		assertEquals("4860.00", writer.text(new BigDecimal("4860.00")), "a DECIMAL keeps the scale its hints gave it");
		assertEquals("56.6", new TableWriter(Format.CSV).decimals(1).text(56.60427012059772));
		assertEquals("57", new TableWriter(Format.CSV).decimals(0).text(56.6));
	}

	@Test
	public void writesJsonWithNumbersAndNulls() throws IOException {
		assertEquals("[ {\n  \"Market\" : \"East\",\n  \"Product\" : \"Cola\",\n  \"Jan\" : 1812\n}, {\n  \"Market\" : \"West\",\n"
				+ "  \"Product\" : \"Diet, \\\"Lite\\\"\",\n  \"Jan\" : null\n} ]\n", write(new TableWriter(Format.JSON), SALES));
	}

	@Test
	public void writesJsonLines() throws IOException {
		assertEquals("{\"Market\":\"East\",\"Product\":\"Cola\",\"Jan\":1812}\n{\"Market\":\"West\",\"Product\":\"Diet, \\\"Lite\\\"\",\"Jan\":null}\n",
				write(new TableWriter(Format.JSONL), SALES));
	}

	@Test
	public void writesMarkdownWithNumbersAlignedRight() throws IOException {
		Table pipes = new Table(List.of(new Table.Column("Name", false)), List.<Object[]>of(new Object[] { "a|b" }));
		assertEquals("| Market | Product | Jan |\n| --- | --- | ---: |\n| East | Cola | 1812 |\n| West | Diet, \"Lite\" |  |\n",
				write(new TableWriter(Format.MARKDOWN), SALES));
		assertEquals("| Name |\n| --- |\n| a\\|b |\n", write(new TableWriter(Format.MARKDOWN), pipes));
	}

	@Test
	public void writesATextTableWithNumbersAlignedRight() throws IOException {
		assertEquals("+--------+--------------+------+\n"
				+ "| Market | Product      | Jan  |\n"
				+ "+--------+--------------+------+\n"
				+ "| East   | Cola         | 1812 |\n"
				+ "| West   | Diet, \"Lite\" |      |\n"
				+ "+--------+--------------+------+\n", write(new TableWriter(Format.TABLE), SALES));
	}

	@Test
	public void writesAnEscapedHtmlTable() throws IOException {
		Table markup = new Table(List.of(new Table.Column("<b>", false), new Table.Column("N", true)), List.<Object[]>of(new Object[] { "a & b", 1 }));
		assertEquals("<table>\n  <thead>\n    <tr><th>&lt;b&gt;</th><th>N</th></tr>\n  </thead>\n  <tbody>\n"
				+ "    <tr><td>a &amp; b</td><td class=\"number\">1</td></tr>\n  </tbody>\n</table>\n", write(new TableWriter(Format.HTML), markup));
	}

	@Test
	public void excelIsNotAText() {
		assertThrows(IllegalArgumentException.class, () -> new TableWriter(Format.XLSX));
	}

}
