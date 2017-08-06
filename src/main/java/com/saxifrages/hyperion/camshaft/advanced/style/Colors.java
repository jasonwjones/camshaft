package com.saxifrages.hyperion.camshaft.advanced.style;

public class Colors {

	public static Color GREEN = new BasicColor("00FF00");
	
	public static Color RED = new BasicColor("FF0000");
	
	public static Color NONE = new ColorNone();
	
	private static class ColorNone implements Color {

		@Override
		public String getHexTriplet() {
			return null;
		}

		@Override
		public boolean isSet() {
			return false;
		}
		
	}
}
