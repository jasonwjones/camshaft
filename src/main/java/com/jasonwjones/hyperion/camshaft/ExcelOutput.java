package com.jasonwjones.hyperion.camshaft;

import java.io.OutputStream;
import java.time.Duration;
import java.util.Locale;

import com.appliedolap.essbase.EssApiException;
import com.appliedolap.essbase.EssCube;
import com.appliedolap.essbase.EssMdxOptions;
import com.appliedolap.essbase.EssServer;
import com.jasonwjones.hyperion.thriller.parser.CubeName;
import com.jasonwjones.hyperion.thriller.parser.QueryParser;

/**
 * An Excel workbook of a query's result, as Essbase lays the grid out itself.
 * Essbase writes it, so Thriller's shaping doesn't apply, but member naming
 * does.
 */
final class ExcelOutput {

	private ExcelOutput() {}

	/**
	 * @param server the server
	 * @param shaping the shaping options; only member naming and the timeout
	 *            apply
	 * @param defaultCube the cube to read if the query has no FROM clause, or
	 *            null
	 * @param mdx the query
	 * @param out where to write the workbook; not closed
	 * @throws CamshaftException if the query can't be run
	 */
	static void write(EssServer server, ShapingOptions shaping, String defaultCube, String mdx, OutputStream out) throws CamshaftException {
		if (shaping.shape != null || shaping.pov || shaping.names != null || shaping.missing != null) {
			System.err.println("camshaft: Excel output is Essbase's own layout, so --shape, --pov, --names and --missing don't apply to it");
		}
		CubeName named = CubeName.fromQuery(mdx);
		CubeRef cube;
		if (named != null) {
			cube = new CubeRef(named.getApplication(), named.getCube());
		} else if (defaultCube != null) {
			cube = CubeRef.parse(defaultCube);
		} else {
			throw new CamshaftException("The query doesn't say which cube to read: give it a FROM clause, or use --cube");
		}
		EssMdxOptions options = EssMdxOptions.defaults();
		if (shaping.members != null) {
			options.memberIdentifierType(EssCube.MdxIdentifierType.valueOf(identifierType(shaping.members)));
		}
		if (shaping.aliasTable != null) {
			options.aliasTableName(shaping.aliasTable);
		}
		if (shaping.timeout != null) {
			options.timeout(Duration.ofSeconds(shaping.timeout));
		}
		try {
			// Comments go first, for the reason Thriller removes them: Essbase
			// stops reading at a semicolon, even inside one
			server.getApplication(cube.application()).getCube(cube.cube())
					.executeMdx(QueryParser.withoutComments(mdx), options, EssCube.MdxOutputType.XLSX, out);
		} catch (EssApiException e) {
			throw new CamshaftException("Essbase could not run the query on " + cube + ": " + Camshaft.message(e), e);
		}
	}

	private static String identifierType(String members) throws CamshaftException {
		switch (members.toLowerCase(Locale.ROOT)) {
		case "name":
		case "names":
			return "NAME";
		case "alias":
		case "aliases":
			return "ALIAS";
		case "unique":
		case "unique_name":
			return "UNIQUE_NAME";
		default:
			throw new CamshaftException("--members can be name, alias or unique, not '" + members + "'");
		}
	}

}
