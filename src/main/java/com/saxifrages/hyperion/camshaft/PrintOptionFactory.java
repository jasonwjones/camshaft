package com.saxifrages.hyperion.camshaft;

import java.io.IOException;
import java.util.Properties;

public class PrintOptionFactory {

	public Properties defaultPrintOptions() throws IOException {
		Properties properties = new Properties();
		properties.load(getClass().getResourceAsStream("/test.properties"));
		return properties;
	}
	
}
