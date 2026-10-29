package com.jasonwjones.hyperion.camshaft;

import picocli.CommandLine.Option;

/**
 * The options that say which server to query and how to log in to it. There's
 * deliberately no option for a password, which would end up in shell history
 * and process lists: it comes from CAMSHAFT_PASSWORD, a profile, or a prompt.
 */
public class ConnectionOptions {

	@Option(names = { "-s", "--url" }, paramLabel = "URL",
			description = "The Essbase server's address, such as http://localhost:9000/essbase (or CAMSHAFT_URL)")
	String url;

	@Option(names = { "-u", "--user" }, paramLabel = "USER", description = "The user to log in as (or CAMSHAFT_USER)")
	String user;

	@Option(names = { "-p", "--profile" }, paramLabel = "NAME",
			description = "A profile in ~/.camshaft.properties to take the address, user and password from")
	String profile;

	@Option(names = "--auth", paramLabel = "HOW",
			description = "How to log in: password (the default), token if the password is a bearer token, "
					+ "or session if it's the id of an existing session")
	String auth;

	@Option(names = "--cube", paramLabel = "APP.CUBE",
			description = "The cube to read when a query has no FROM clause, such as Sample.Basic")
	String cube;

	/**
	 * @return the login these options, the environment and the profiles give
	 * @throws CamshaftException if something needed is missing
	 */
	Login login() throws CamshaftException {
		return Login.resolve(url, user, profile, auth, "CAMSHAFT_", System::getenv, System.console(), null);
	}

}
