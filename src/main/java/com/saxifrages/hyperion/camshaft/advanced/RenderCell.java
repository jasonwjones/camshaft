package com.saxifrages.hyperion.camshaft.advanced;

public interface RenderCell {

	public enum AxisOrientation {
		ROW, 
		COL
	}
	
	public int getRow();
	
	public int getRenderRow();
	
	public String getValue();
	
	public AxisOrientation getAxisOrientation();
	
	public int getAxisNumber();
	
	public boolean isRendered();
	
	public int getSpan();
	
	// axis number
	// total axes
	// is first axis
	// is last axis
	// is middle axis
	
	// row
	// rowsSpanned -- for HTML rowspan
	// total rows
	
	// colsSpanned etc
	
	
	// is first row in axisgroup
	// is continuing row in axisgroup
	// is last row in axisgroup
	// is single axis group (first/middle/last)
	
	
	//
	
}
