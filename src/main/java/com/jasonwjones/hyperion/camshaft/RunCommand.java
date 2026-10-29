package com.jasonwjones.hyperion.camshaft;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.Callable;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Parameters;

/**
 * Runs one query and writes its result.
 */
@Command(name = "run", description = "Runs a query and writes its result.", mixinStandardHelpOptions = true)
public class RunCommand implements Callable<Integer> {

	@Parameters(index = "0", arity = "0..1", paramLabel = "QUERY",
			description = "The MDX, a file holding it, or - to read it from standard input, which is also what happens without one")
	String query;

	@Mixin
	ConnectionOptions connection;

	@Mixin
	ShapingOptions shaping;

	@Mixin
	OutputOptions output;

	@Override
	public Integer call() throws Exception {
		String mdx = QuerySource.read(query, System.in);
		Format format = output.format();
		Login login = connection.login();
		if (format == Format.XLSX) {
			try (OutputStream out = output.open()) {
				ExcelOutput.write(login.server(), shaping, connection.cube, mdx, out);
			}
		} else {
			try (Connection jdbc = login.connect(shaping.settings(connection.cube));
					Statement statement = jdbc.createStatement();
					ResultSet resultSet = statement.executeQuery(mdx)) {
				output.write(Table.from(resultSet), format);
			}
		}
		return Camshaft.OK;
	}

}
