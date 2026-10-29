package com.jasonwjones.hyperion.camshaft;

import java.util.LinkedHashMap;
import java.util.Map;

import picocli.CommandLine.Option;

/**
 * How Thriller should name the members and shape the result. Each is one of
 * Thriller's settings, which a query's own hints still override.
 */
public class ShapingOptions {

	@Option(names = "--members", paramLabel = "NAMING", description = "How to name members: name (the default), alias or unique")
	String members;

	@Option(names = "--alias-table", paramLabel = "TABLE", description = "The alias table to use with --members alias, such as \"Long Names\"")
	String aliasTable;

	@Option(names = "--shape", paramLabel = "SHAPE",
			description = "wide (the default) for the grid as Essbase lays it out, long for a row per value")
	String shape;

	@Option(names = "--pov", description = "Add the point of view's members as columns")
	boolean pov;

	@Option(names = "--names", paramLabel = "STYLE",
			description = "Column names as the members have them (as-is, the default), or as identifiers: upper or lower")
	String names;

	@Option(names = "--missing", paramLabel = "AS",
			description = "What #Missing becomes: null (the default), zero, or skip to leave rows of nothing else out")
	String missing;

	@Option(names = "--timeout", paramLabel = "SECONDS", description = "How long to wait for a query before giving up")
	Integer timeout;

	/**
	 * @param cube the cube for queries without a FROM clause, or null
	 * @return Thriller's settings for these options
	 */
	Map<String, String> settings(String cube) throws CamshaftException {
		Map<String, String> settings = new LinkedHashMap<>();
		put(settings, "members", members);
		put(settings, "alias.table", aliasTable);
		put(settings, "shape", shape);
		put(settings, "pov", pov ? "columns" : null);
		put(settings, "names", names);
		put(settings, "missing", missing);
		put(settings, "timeout", timeout == null ? null : timeout.toString());
		if (cube != null) {
			CubeRef ref = CubeRef.parse(cube);
			settings.put("app", ref.application());
			settings.put("cube", ref.cube());
		}
		return settings;
	}

	private static void put(Map<String, String> settings, String key, String value) {
		if (value != null) {
			settings.put(key, value);
		}
	}

}
