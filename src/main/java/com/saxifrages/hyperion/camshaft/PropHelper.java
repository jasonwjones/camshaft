package com.saxifrages.hyperion.camshaft;

import java.util.Properties;

public class PropHelper {
	
	private Properties properties;
	
	public PropHelper(String filename) {
		
	}
	
	public boolean getBoolean(String key) {
		if (properties.getProperty(key) != null) {
			return Boolean.getBoolean(properties.getProperty(key));
		}
		return false;
	}

}
