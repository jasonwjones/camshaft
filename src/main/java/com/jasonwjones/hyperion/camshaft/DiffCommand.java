package com.jasonwjones.hyperion.camshaft;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

import com.jasonwjones.hyperion.thriller.parser.CubeName;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Runs a query against two servers or cubes and shows where they differ.
 */
@Command(name = "diff", mixinStandardHelpOptions = true,
		description = { "Runs a query against two servers or cubes and shows the values that differ.",
				"The right side is the left with the --right options applied. Its password comes from CAMSHAFT_RIGHT_PASSWORD or a profile, "
						+ "or is the left's if it logs in as the same user. To compare two cubes, leave the FROM clause out of the query and give --cube and --right-cube." })
public class DiffCommand implements Callable<Integer> {

	@Parameters(index = "0", arity = "0..1", paramLabel = "QUERY",
			description = "The MDX, a file holding it, or - to read it from standard input, which is also what happens without one")
	String query;

	@Mixin
	ConnectionOptions connection;

	@Option(names = "--right-url", paramLabel = "URL", description = "The right server's address")
	String rightUrl;

	@Option(names = "--right-user", paramLabel = "USER", description = "The user to log in to the right server as")
	String rightUser;

	@Option(names = "--right-profile", paramLabel = "NAME", description = "A profile to take the right server's login from")
	String rightProfile;

	@Option(names = "--right-auth", paramLabel = "HOW", description = "How to log in to the right server: password, token or session")
	String rightAuth;

	@Option(names = "--right-cube", paramLabel = "APP.CUBE", description = "The cube to read on the right, for a query without a FROM clause")
	String rightCube;

	@Option(names = "--members", paramLabel = "NAMING", description = "How to name members: name (the default), alias or unique")
	String members;

	@Option(names = "--tolerance", paramLabel = "AMOUNT", description = "How far apart two numbers can be and still count as the same; 0 by default")
	BigDecimal tolerance = BigDecimal.ZERO;

	@Option(names = "--all", description = "Show the values that are the same too")
	boolean all;

	@Option(names = "--timeout", paramLabel = "SECONDS", description = "How long to wait for each query before giving up")
	Integer timeout;

	@Mixin
	OutputOptions output;

	@Override
	public Integer call() throws Exception {
		String mdx = QuerySource.read(query, System.in);
		Format format = output.format();
		if (format == Format.XLSX) {
			throw new CamshaftException("Excel is for query results; write the comparison in another format");
		}
		if (CubeName.fromQuery(mdx) != null && (connection.cube != null || rightCube != null)) {
			throw new CamshaftException("The query names its cube in a FROM clause, which wins over --cube and --right-cube; "
					+ "leave the FROM clause out to compare cubes");
		}
		Login left = connection.login();
		Login right = rightLogin(left);
		String leftCube = connection.cube;
		String rightCubeName = rightCube != null ? rightCube : leftCube;
		if (left.equals(right) && Objects.equals(leftCube, rightCubeName)) {
			throw new CamshaftException("Both sides are the same: give --right-url, --right-profile or --right-cube");
		}

		Table leftTable = run(left, leftCube, mdx, "left");
		Table rightTable = run(right, rightCubeName, mdx, "right");
		Diff.Result result = Diff.compare(leftTable, rightTable, tolerance, all);
		output.write(result.table(), format);
		System.err.println(result.summary());
		return result.differs() ? Camshaft.DIFFERENT : Camshaft.OK;
	}

	/**
	 * The right side's login: the left's with the --right options applied, so
	 * that comparing two cubes on one server needs no second login.
	 */
	private Login rightLogin(Login left) throws CamshaftException {
		boolean sameServer = rightProfile == null && (rightUrl == null || rightUrl.equals(left.url()))
				&& (rightUser == null || rightUser.equals(left.user()))
				&& (rightAuth == null || rightAuth.equalsIgnoreCase(left.auth()));
		if (sameServer) {
			return left;
		}
		return Login.resolve(rightUrl != null ? rightUrl : rightProfile == null ? left.url() : null,
				rightUser != null ? rightUser : rightProfile == null ? left.user() : null, rightProfile,
				rightAuth != null ? rightAuth : rightProfile == null ? left.auth() : null, "CAMSHAFT_RIGHT_", System::getenv,
				System.console(), left);
	}

	private Table run(Login login, String cube, String mdx, String side) throws CamshaftException {
		Map<String, String> settings = new LinkedHashMap<>();
		settings.put("shape", "long");
		settings.put("pov", "columns");
		if (members != null) {
			settings.put("members", members);
		}
		if (timeout != null) {
			settings.put("timeout", timeout.toString());
		}
		if (cube != null) {
			CubeRef ref = CubeRef.parse(cube);
			settings.put("app", ref.application());
			settings.put("cube", ref.cube());
		}
		try (Connection jdbc = login.connect(settings);
				Statement statement = jdbc.createStatement();
				ResultSet resultSet = statement.executeQuery(mdx)) {
			return Table.from(resultSet);
		} catch (SQLException e) {
			throw new CamshaftException("The " + side + " side (" + login + (cube == null ? "" : ", " + cube) + "): " + Camshaft.message(e), e);
		}
	}

}
