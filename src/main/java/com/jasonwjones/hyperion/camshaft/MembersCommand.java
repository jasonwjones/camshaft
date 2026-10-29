package com.jasonwjones.hyperion.camshaft;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Lists a dimension's members, by way of an MDX query with Thriller's hints
 * naming the columns.
 */
@Command(name = "members", mixinStandardHelpOptions = true,
		description = "Lists a dimension's members, with each one's alias, level and generation.")
public class MembersCommand implements Callable<Integer> {

	@Parameters(index = "0", paramLabel = "APP.CUBE", description = "The cube, such as Sample.Basic")
	String cube;

	@Parameters(index = "1", paramLabel = "DIMENSION", description = "The dimension, such as Product")
	String dimension;

	@ArgGroup(exclusive = true)
	Which which = new Which();

	static class Which {

		@Option(names = "--level", paramLabel = "N", description = "Only the members at this level; 0 is the leaves")
		Integer level;

		@Option(names = "--generation", paramLabel = "N", description = "Only the members at this generation; 1 is the dimension itself")
		Integer generation;

		@Option(names = "--children", paramLabel = "MEMBER", description = "Only this member's children")
		String children;

		@Option(names = "--descendants", paramLabel = "MEMBER", description = "This member and everything below it")
		String descendants;

	}

	@Option(names = "--alias-table", paramLabel = "TABLE", description = "The alias table to show aliases from, such as \"Long Names\"; the default table otherwise")
	String aliasTable;

	@Mixin
	ConnectionOptions connection;

	@Mixin
	OutputOptions output;

	@Override
	public Integer call() throws Exception {
		Format format = output.format();
		if (format == Format.XLSX) {
			throw new CamshaftException("Excel is for query results; list members in another format");
		}
		String mdx = query(CubeRef.parse(cube), dimension, which);
		// Essbase only takes aliases from another table when members are named by
		// alias, which renames the member on the axis too. So the query reads the
		// name as a property as well, and the axis's own column is dropped.
		Map<String, String> settings = new LinkedHashMap<>();
		if (aliasTable != null) {
			settings.put("members", "alias");
			settings.put("alias.table", aliasTable);
		}
		try (Connection jdbc = connection.login().connect(settings);
				Statement statement = jdbc.createStatement();
				ResultSet resultSet = statement.executeQuery(mdx)) {
			output.write(withoutAxis(Table.from(resultSet)), format);
		}
		return Camshaft.OK;
	}

	/**
	 * The query that lists the members: the set asked for on the rows, and their
	 * name, alias, level and generation as properties.
	 */
	static String query(CubeRef cube, String dimension, Which which) {
		String dim = bracketed(dimension);
		String set;
		if (which.level != null) {
			set = dim + ".Levels(" + which.level + ").Members";
		} else if (which.generation != null) {
			set = dim + ".Generations(" + which.generation + ").Members";
		} else if (which.children != null) {
			set = bracketed(which.children) + ".Children";
		} else if (which.descendants != null) {
			set = "Descendants(" + bracketed(which.descendants) + ")";
		} else {
			set = dim + ".Members";
		}
		return "SELECT\n"
				+ "/*\n"
				+ " -- column.1.name = AXIS\n"
				+ " -- column.1.type = VARCHAR\n"
				+ " -- column.2.name = MEMBER\n"
				+ " -- column.2.type = VARCHAR\n"
				+ " -- column.3.name = ALIAS\n"
				+ " -- column.3.type = VARCHAR\n"
				+ " -- column.4.name = LEVEL\n"
				+ " -- column.4.type = INTEGER\n"
				+ " -- column.5.name = GENERATION\n"
				+ " -- column.5.type = INTEGER\n"
				+ " */\n"
				+ "{} ON COLUMNS,\n"
				+ "{" + set + "} DIMENSION PROPERTIES\n"
				+ "  PROPERTY_EXPR(" + dim + ", MEMBER_NAME, CurrentAxisMember(), \"Name\"),\n"
				+ "  PROPERTY_EXPR(" + dim + ", MEMBER_ALIAS, CurrentAxisMember(), \"Alias\"),\n"
				+ "  PROPERTY_EXPR(" + dim + ", LEVEL_NUMBER, CurrentAxisMember(), \"Level\"),\n"
				+ "  PROPERTY_EXPR(" + dim + ", GEN_NUMBER, CurrentAxisMember(), \"Generation\")\n"
				+ "ON ROWS\n"
				+ "FROM " + cube.mdx();
	}

	/**
	 * The table without its first column, the member as the axis names it,
	 * since MEMBER always has its name.
	 */
	static Table withoutAxis(Table table) {
		List<Object[]> rows = new ArrayList<>();
		for (Object[] row : table.getRows()) {
			rows.add(Arrays.copyOfRange(row, 1, row.length));
		}
		return new Table(table.getColumns().subList(1, table.getColumns().size()), rows);
	}

	/**
	 * A name in brackets, as MDX writes one, unless it's in them already.
	 */
	static String bracketed(String name) {
		String trimmed = name.trim();
		return trimmed.startsWith("[") ? trimmed : "[" + trimmed.replace("]", "]]") + "]";
	}

}
