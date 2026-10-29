package com.jasonwjones.hyperion.camshaft;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import com.appliedolap.essbase.EssServer;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Runs many queries, each into a file of its own.
 */
@Command(name = "batch", mixinStandardHelpOptions = true,
		description = "Runs the queries in the given files and folders, writing each result to a file of the same name in an output folder.")
public class BatchCommand implements Callable<Integer> {

	@Parameters(arity = "1..*", paramLabel = "PATH", description = "Query files, and folders whose .mdx files to run")
	List<Path> inputs;

	@Option(names = { "-d", "--output-dir" }, required = true, paramLabel = "FOLDER", description = "Where to write the results")
	Path outputDir;

	@Option(names = { "-f", "--format" }, paramLabel = "FORMAT", description = "csv (the default), tsv, json, jsonl, markdown, table, html or xlsx")
	Format format = Format.CSV;

	@Option(names = "--keep-going", description = "Carry on past a query that fails, and report the failures at the end")
	boolean keepGoing;

	@Option(names = "--no-header", description = "Leave out the column names")
	boolean noHeader;

	@Option(names = { "--null", "--missing-text" }, paramLabel = "TEXT", description = "What to write for a null; nothing by default")
	String nullText = "";

	@Option(names = { "--decimals", "--maximum-fraction-digits" }, paramLabel = "N", description = "Round numbers to this many decimal places")
	Integer decimals;

	@Mixin
	ConnectionOptions connection;

	@Mixin
	ShapingOptions shaping;

	@Override
	public Integer call() throws Exception {
		List<Path> queries = queries(inputs);
		if (queries.isEmpty()) {
			throw new CamshaftException("No .mdx files in " + inputs);
		}
		Files.createDirectories(outputDir);
		Login login = connection.login();
		List<String> failures = new ArrayList<>();
		// One login for every query: a JDBC connection, or for Excel the server
		EssServer server = format == Format.XLSX ? login.server() : null;
		try (Connection jdbc = format == Format.XLSX ? null : login.connect(shaping.settings(connection.cube))) {
			for (Path query : queries) {
				Path target = outputDir.resolve(baseName(query) + "." + format.extension());
				try {
					String mdx = Files.readString(query, StandardCharsets.UTF_8);
					if (format == Format.XLSX) {
						try (OutputStream out = Files.newOutputStream(target)) {
							ExcelOutput.write(server, shaping, connection.cube, mdx, out);
						}
					} else {
						write(jdbc, mdx, target);
					}
					System.err.println("wrote " + target);
				} catch (CamshaftException | SQLException | IOException e) {
					failures.add(query + ": " + Camshaft.message(e));
					Files.deleteIfExists(target);
					if (!keepGoing) {
						throw new CamshaftException(query + ": " + Camshaft.message(e), e);
					}
					System.err.println("failed " + query + ": " + Camshaft.message(e));
				}
			}
		}
		if (!failures.isEmpty()) {
			System.err.println(failures.size() + " of " + queries.size() + " queries failed");
			return Camshaft.FAILED;
		}
		return Camshaft.OK;
	}

	private void write(Connection jdbc, String mdx, Path target) throws SQLException, IOException {
		try (Statement statement = jdbc.createStatement(); ResultSet resultSet = statement.executeQuery(mdx)) {
			Table table = Table.from(resultSet);
			try (Writer writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(target), StandardCharsets.UTF_8))) {
				new TableWriter(format).header(!noHeader).nullText(nullText).decimals(decimals).write(table, writer);
			}
		}
	}

	/**
	 * The query files: each file given, and each folder's .mdx files, in name
	 * order. Folders aren't searched below their top level.
	 */
	static List<Path> queries(List<Path> inputs) throws IOException, CamshaftException {
		List<Path> queries = new ArrayList<>();
		for (Path input : inputs) {
			if (Files.isDirectory(input)) {
				try (Stream<Path> files = Files.list(input)) {
					files.filter(path -> path.getFileName().toString().endsWith(".mdx") && Files.isRegularFile(path))
							.sorted().forEach(queries::add);
				}
			} else if (Files.isRegularFile(input)) {
				queries.add(input);
			} else {
				throw new CamshaftException("There's no file or folder at " + input);
			}
		}
		return queries;
	}

	static String baseName(Path query) {
		String name = query.getFileName().toString();
		int dot = name.lastIndexOf('.');
		return dot > 0 ? name.substring(0, dot) : name;
	}

}
