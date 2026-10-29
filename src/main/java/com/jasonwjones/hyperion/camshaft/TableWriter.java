package com.jasonwjones.hyperion.camshaft;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.StreamWriteFeature;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

/**
 * Writes a table in one of the text formats. Numbers are written in full, never
 * in scientific notation, which a load rule can't read; a number of decimals
 * rounds them, and a text for nulls stands in for #Missing in the formats that
 * have no null of their own.
 */
public final class TableWriter {

	private final Format format;

	private boolean header = true;

	private String nullText = "";

	private Integer decimals;

	public TableWriter(Format format) {
		if (!format.isText()) {
			throw new IllegalArgumentException(format + " isn't a text format");
		}
		this.format = format;
	}

	/**
	 * @param header whether to write the column names first, where the format
	 *            has a place for them
	 * @return this writer
	 */
	public TableWriter header(boolean header) {
		this.header = header;
		return this;
	}

	/**
	 * @param nullText what to write for a null; JSON writes null regardless
	 * @return this writer
	 */
	public TableWriter nullText(String nullText) {
		this.nullText = nullText;
		return this;
	}

	/**
	 * @param decimals how many places to round numbers to, or null to leave
	 *            them as they are
	 * @return this writer
	 */
	public TableWriter decimals(Integer decimals) {
		this.decimals = decimals;
		return this;
	}

	/**
	 * @param table the table
	 * @param out where to write it; not closed
	 * @throws IOException if writing fails
	 */
	public void write(Table table, Writer out) throws IOException {
		switch (format) {
		case CSV:
			delimited(table, out, ',');
			break;
		case TSV:
			delimited(table, out, '\t');
			break;
		case JSON:
			json(table, out, false);
			break;
		case JSONL:
			json(table, out, true);
			break;
		case MARKDOWN:
			markdown(table, out);
			break;
		case TABLE:
			text(table, out);
			break;
		case HTML:
			html(table, out);
			break;
		default:
			throw new IllegalStateException(format.name());
		}
		out.flush();
	}

	/**
	 * A value as text: a number in full, rounded if asked, and a null as the
	 * text for nulls.
	 */
	String text(Object value) {
		if (value == null) {
			return nullText;
		}
		BigDecimal number = number(value);
		return number != null ? number.toPlainString() : value.toString();
	}

	private BigDecimal number(Object value) {
		if (!(value instanceof Number)) {
			return null;
		}
		BigDecimal number = value instanceof BigDecimal ? (BigDecimal) value : new BigDecimal(value.toString());
		if (decimals != null) {
			number = number.setScale(decimals, RoundingMode.HALF_UP);
		} else if (!(value instanceof BigDecimal)) {
			// 1812.0 reads as 1812, as a number from Essbase is shown
			number = number.stripTrailingZeros();
			if (number.scale() < 0) {
				number = number.setScale(0);
			}
		}
		return number;
	}

	private void delimited(Table table, Writer out, char delimiter) throws IOException {
		if (header) {
			List<String> names = new ArrayList<>();
			for (Table.Column column : table.getColumns()) {
				names.add(column.name());
			}
			delimitedLine(names, out, delimiter);
		}
		for (Object[] row : table.getRows()) {
			List<String> cells = new ArrayList<>();
			for (Object value : row) {
				cells.add(text(value));
			}
			delimitedLine(cells, out, delimiter);
		}
	}

	private static void delimitedLine(List<String> cells, Writer out, char delimiter) throws IOException {
		for (int index = 0; index < cells.size(); index++) {
			if (index > 0) {
				out.write(delimiter);
			}
			out.write(quoted(cells.get(index), delimiter));
		}
		out.write('\n');
	}

	/**
	 * A cell quoted the way CSV quotes one: only if it has to be, with any
	 * quote inside doubled.
	 */
	static String quoted(String cell, char delimiter) {
		boolean needsQuotes = cell.indexOf(delimiter) >= 0 || cell.indexOf('"') >= 0 || cell.indexOf('\n') >= 0
				|| cell.indexOf('\r') >= 0 || (!cell.isEmpty() && (Character.isWhitespace(cell.charAt(0))
						|| Character.isWhitespace(cell.charAt(cell.length() - 1))));
		return needsQuotes ? '"' + cell.replace("\"", "\"\"") + '"' : cell;
	}

	private void json(Table table, Writer out, boolean lines) throws IOException {
		// The writer belongs to the caller, so finishing the JSON mustn't close it
		JsonFactory factory = JsonFactory.builder().disable(StreamWriteFeature.AUTO_CLOSE_TARGET).build();
		try (JsonGenerator generator = factory.createGenerator(out)) {
			if (lines) {
				// Each row is on a line of its own, with nothing else between them
				generator.setRootValueSeparator(null);
			} else {
				generator.setPrettyPrinter(new DefaultPrettyPrinter());
				generator.writeStartArray();
			}
			for (Object[] row : table.getRows()) {
				generator.writeStartObject();
				for (int index = 0; index < row.length; index++) {
					generator.writeFieldName(table.getColumns().get(index).name());
					Object value = row[index];
					BigDecimal number = number(value);
					if (value == null) {
						generator.writeNull();
					} else if (number != null) {
						generator.writeNumber(number);
					} else {
						generator.writeString(value.toString());
					}
				}
				generator.writeEndObject();
				if (lines) {
					generator.flush();
					out.write('\n');
				}
			}
			if (!lines) {
				generator.writeEndArray();
			}
		}
		if (!lines) {
			out.write('\n');
		}
	}

	private void markdown(Table table, Writer out) throws IOException {
		List<String> names = new ArrayList<>();
		List<String> rule = new ArrayList<>();
		for (Table.Column column : table.getColumns()) {
			names.add(markdownCell(column.name()));
			rule.add(column.numeric() ? "---:" : "---");
		}
		markdownLine(names, out);
		markdownLine(rule, out);
		for (Object[] row : table.getRows()) {
			List<String> cells = new ArrayList<>();
			for (Object value : row) {
				cells.add(markdownCell(text(value)));
			}
			markdownLine(cells, out);
		}
	}

	private static String markdownCell(String text) {
		return text.replace("\\", "\\\\").replace("|", "\\|").replaceAll("[\\r\\n]+", " ");
	}

	private static void markdownLine(List<String> cells, Writer out) throws IOException {
		out.write("| " + String.join(" | ", cells) + " |\n");
	}

	/**
	 * A table with borders, numbers right-aligned, as a SQL tool shows one.
	 */
	private void text(Table table, Writer out) throws IOException {
		List<Table.Column> columns = table.getColumns();
		List<String[]> rows = new ArrayList<>();
		for (Object[] row : table.getRows()) {
			String[] cells = new String[row.length];
			for (int index = 0; index < row.length; index++) {
				cells[index] = text(row[index]).replaceAll("[\\r\\n]+", " ");
			}
			rows.add(cells);
		}
		int[] widths = new int[columns.size()];
		for (int index = 0; index < widths.length; index++) {
			widths[index] = header ? columns.get(index).name().length() : 0;
			for (String[] cells : rows) {
				widths[index] = Math.max(widths[index], cells[index].length());
			}
		}
		StringBuilder border = new StringBuilder("+");
		for (int width : widths) {
			border.append("-".repeat(width + 2)).append('+');
		}
		out.write(border + "\n");
		if (header) {
			String[] names = new String[columns.size()];
			for (int index = 0; index < names.length; index++) {
				names[index] = columns.get(index).name();
			}
			out.write(textLine(names, widths, Collections.nCopies(columns.size(), false)) + "\n");
			out.write(border + "\n");
		}
		List<Boolean> numeric = new ArrayList<>();
		for (Table.Column column : columns) {
			numeric.add(column.numeric());
		}
		for (String[] cells : rows) {
			out.write(textLine(cells, widths, numeric) + "\n");
		}
		out.write(border + "\n");
	}

	private static String textLine(String[] cells, int[] widths, List<Boolean> rightAligned) {
		StringBuilder line = new StringBuilder("|");
		for (int index = 0; index < cells.length; index++) {
			String padding = " ".repeat(widths[index] - cells[index].length());
			line.append(' ').append(rightAligned.get(index) ? padding + cells[index] : cells[index] + padding).append(" |");
		}
		return line.toString();
	}

	private void html(Table table, Writer out) throws IOException {
		out.write("<table>\n");
		if (header) {
			out.write("  <thead>\n    <tr>");
			for (Table.Column column : table.getColumns()) {
				out.write("<th>" + escape(column.name()) + "</th>");
			}
			out.write("</tr>\n  </thead>\n");
		}
		out.write("  <tbody>\n");
		for (Object[] row : table.getRows()) {
			out.write("    <tr>");
			for (int index = 0; index < row.length; index++) {
				boolean numeric = table.getColumns().get(index).numeric();
				out.write((numeric ? "<td class=\"number\">" : "<td>") + escape(text(row[index])) + "</td>");
			}
			out.write("</tr>\n");
		}
		out.write("  </tbody>\n</table>\n");
	}

	private static String escape(String text) {
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
	}

}
