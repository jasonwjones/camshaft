package com.jasonwjones.hyperion.camshaft;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An application and cube, written App.Cube or [App].[Cube] as a FROM clause
 * names them.
 *
 * @param application the application
 * @param cube the cube
 */
public record CubeRef(String application, String cube) {

	private static final Pattern NAME = Pattern.compile("\\s*\\[?([^\\[\\].]+)\\]?\\s*\\.\\s*\\[?([^\\[\\].]+)\\]?\\s*");

	/**
	 * @param text such as Sample.Basic
	 * @return the application and cube it names
	 * @throws CamshaftException if it doesn't name both
	 */
	public static CubeRef parse(String text) throws CamshaftException {
		Matcher matcher = NAME.matcher(text);
		if (!matcher.matches()) {
			throw new CamshaftException("Expected a cube such as Sample.Basic, not '" + text + "'");
		}
		return new CubeRef(matcher.group(1).trim(), matcher.group(2).trim());
	}

	/**
	 * @return the cube as a FROM clause names it, such as [Sample].[Basic]
	 */
	public String mdx() {
		return "[" + application + "].[" + cube + "]";
	}

	@Override
	public String toString() {
		return application + "." + cube;
	}

}
