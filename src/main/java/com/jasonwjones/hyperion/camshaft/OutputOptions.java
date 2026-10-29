package com.jasonwjones.hyperion.camshaft;

import java.io.BufferedWriter;
import java.io.Console;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import picocli.CommandLine.Option;

/**
 * Where a result goes and in what format.
 */
public class OutputOptions {

	@Option(names = { "-o", "--output" }, paramLabel = "FILE",
			description = "The file to write, its format taken from its extension; standard output if not given")
	Path output;

	@Option(names = { "-f", "--format" }, paramLabel = "FORMAT",
			description = "csv, tsv, json, jsonl, markdown, table, html or xlsx. "
					+ "Without one, the output file's extension decides, and otherwise it's table in a terminal and csv when piped")
	Format format;

	@Option(names = "--no-header", negatable = false, description = "Leave out the column names")
	boolean noHeader;

	@Option(names = { "--null", "--missing-text" }, paramLabel = "TEXT",
			description = "What to write for a null, such as #Missing; nothing by default")
	String nullText = "";

	@Option(names = { "--decimals", "--maximum-fraction-digits" }, paramLabel = "N",
			description = "Round numbers to this many decimal places")
	Integer decimals;

	/**
	 * @return the format to write: the one asked for, or the output file's, or
	 *         a sensible one for where the output is going
	 * @throws CamshaftException if the output file's extension isn't a format
	 */
	Format format() throws CamshaftException {
		if (format != null) {
			return format;
		}
		if (output != null) {
			Format fromName = Format.fromFileName(output.getFileName().toString());
			if (fromName == null) {
				throw new CamshaftException("Can't tell the format from " + output + "'s extension; give --format");
			}
			return fromName;
		}
		return isTerminal() ? Format.TABLE : Format.CSV;
	}

	/**
	 * Whether output goes to a terminal rather than a file or a pipe. From Java
	 * 22, there's a console even when output is redirected, and only its
	 * isTerminal() says which; before that, there's a console only for a
	 * terminal.
	 */
	static boolean isTerminal() {
		Console console = System.console();
		if (console == null) {
			return false;
		}
		try {
			return (Boolean) Console.class.getMethod("isTerminal").invoke(console);
		} catch (ReflectiveOperationException e) {
			return true;
		}
	}

	/**
	 * @param format the format being written
	 * @return a writer of that format, with these options
	 */
	TableWriter writer(Format format) {
		return new TableWriter(format).header(!noHeader).nullText(nullText).decimals(decimals);
	}

	/**
	 * @return the stream to write to: the output file, or standard output,
	 *         which closing leaves open
	 * @throws IOException if the file can't be created
	 */
	OutputStream open() throws IOException {
		if (output == null) {
			return new FilterOutputStream(System.out) {
				@Override
				public void write(byte[] bytes, int offset, int length) throws IOException {
					out.write(bytes, offset, length);
				}

				@Override
				public void close() throws IOException {
					flush();
				}
			};
		}
		Path parent = output.toAbsolutePath().getParent();
		if (parent != null) {
			Files.createDirectories(parent);
		}
		return Files.newOutputStream(output);
	}

	/**
	 * Writes a table where these options say.
	 *
	 * @param table the table
	 * @param format its format, a text one
	 * @throws IOException if writing fails
	 */
	void write(Table table, Format format) throws IOException {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(open(), StandardCharsets.UTF_8))) {
			writer(format).write(table, writer);
		}
	}

}
