package com.saxifrages.hyperion.camshaft.advanced.style;

public class BasicColor implements Color {

	private String hex;
	
	public BasicColor(String hex) {
		this.hex = hex;
	}
	
	@Override
	public String getHexTriplet() {
		return hex;
	}

	@Override
	public boolean isSet() {
		return true;
	}

}
