package com.saxifrages.hyperion.camshaft.advanced.style;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CascadingCellStyleTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetBackgroundColor() {
		
		DefaultCellStyle baseStyle = new DefaultCellStyle();
		
		CascadingCellStyle globalStyle = new CascadingCellStyle(baseStyle).setBackgroundColor(Colors.GREEN);
		CascadingCellStyle axisStyle = new CascadingCellStyle(globalStyle).setBackgroundColor(Colors.RED);
		axisStyle.getLeftBorder().setWidth(1);
		
		System.out.println("Axis style:");
		printCellStyle(axisStyle);

		System.out.println("Global style:");
		printCellStyle(globalStyle);

		System.out.println("Base style:");
		printCellStyle(baseStyle);

	}
	
	public static void printCellStyle(CellStyle cellStyle) {
		System.out.println("\tBackground Color: " + cellStyle.getBackgroundColor().getHexTriplet());
		System.out.println("\tLeft Border Width: " + cellStyle.getLeftBorder().getWidth());
		System.out.println("\tLeft Border Color: " + cellStyle.getLeftBorder().getColor().getHexTriplet());
		
	}
	

}
