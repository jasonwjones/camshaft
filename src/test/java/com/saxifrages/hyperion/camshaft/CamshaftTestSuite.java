package com.saxifrages.hyperion.camshaft;

import java.io.File;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamshaftTestSuite {

	private static final Logger logger = LoggerFactory.getLogger(CamshaftTestSuite.class);
	
	public static void main(String[] args) {
		new CamshaftTestSuite();
	}
	
	public CamshaftTestSuite() {
		File dir = Paths.get("src/test/resources").toFile();
		for (File  mdxScript : dir.listFiles((directory, name) -> name.endsWith("mdx"))) {
			System.err.println("script: " + mdxScript);
			
			String[] components = mdxScript.getName().split("-");
			String app = components[0];
			String db = components[1];
			
			logger.info("App {} db {}", app, db);
			
		}
		System.out.println("Is dir: " + dir.isDirectory());
		// TODO Auto-generated constructor stub
	}

	
	
}
