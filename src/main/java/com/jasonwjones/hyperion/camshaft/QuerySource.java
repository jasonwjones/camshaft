package com.jasonwjones.hyperion.camshaft;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Where a query comes from: written out on the command line, in a file, or on
 * standard input.
 */
final class QuerySource {

	private QuerySource() {}

	/**
	 * @param query the MDX itself, a file holding it, or - or nothing for
	 *            standard input
	 * @param stdin standard input
	 * @return the MDX
	 * @throws CamshaftException if there's no query
	 * @throws IOException if the file or input can't be read
	 */
	static String read(String query, InputStream stdin) throws CamshaftException, IOException {
		String mdx;
		if (query == null || query.equals("-")) {
			mdx = new String(stdin.readAllBytes(), StandardCharsets.UTF_8);
		} else if (looksLikeFile(query)) {
			mdx = Files.readString(Paths.get(query), StandardCharsets.UTF_8);
		} else {
			mdx = query;
		}
		if (mdx.isBlank()) {
			throw new CamshaftException(query == null || query.equals("-") ? "No query on standard input" : "The query is empty");
		}
		return mdx;
	}

	private static boolean looksLikeFile(String query) {
		if (query.contains("\n") || query.length() > 1024) {
			return false;
		}
		try {
			Path path = Paths.get(query);
			return Files.isRegularFile(path);
		} catch (RuntimeException e) {
			// Not something a path can even be made from, so it's MDX
			return false;
		}
	}

}
