package com.jasonwjones.hyperion.camshaft;

import java.util.List;
import java.util.concurrent.Callable;

import com.appliedolap.essbase.EssApiException;
import com.appliedolap.essbase.EssServer;

import picocli.CommandLine.Mixin;

/**
 * A command that lists something on the server, as a table in any of the
 * formats but Excel.
 */
abstract class ListingCommand implements Callable<Integer> {

	@Mixin
	ConnectionOptions connection;

	@Mixin
	OutputOptions output;

	@Override
	public Integer call() throws Exception {
		Format format = output.format();
		if (format == Format.XLSX) {
			throw new CamshaftException("Excel is for query results; list in another format");
		}
		EssServer server = connection.login().server();
		Table table;
		try {
			table = list(server);
		} catch (EssApiException e) {
			throw new CamshaftException(Camshaft.message(e), e);
		}
		output.write(table, format);
		return Camshaft.OK;
	}

	/**
	 * @param server the server
	 * @return what's on it
	 * @throws CamshaftException if what was asked for isn't there
	 */
	abstract Table list(EssServer server) throws CamshaftException;

	static Table.Column text(String name) {
		return new Table.Column(name, false);
	}

	static Table.Column number(String name) {
		return new Table.Column(name, true);
	}

	static Table table(List<Table.Column> columns, List<Object[]> rows) {
		return new Table(columns, rows);
	}

}
