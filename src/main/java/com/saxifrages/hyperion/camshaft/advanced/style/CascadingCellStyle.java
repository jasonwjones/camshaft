package com.saxifrages.hyperion.camshaft.advanced.style;

public class CascadingCellStyle implements CellStyle {

	private CellStyle parentStyle;
	
	private Border leftBorder;
	
	private Color backgroundColor;
	
	public CascadingCellStyle(CellStyle parentStyle) {
		this.parentStyle = parentStyle;
	}
	
	@Override
	public Border getLeftBorder() {
		return leftBorder != null ? leftBorder : parentStyle.getLeftBorder();
	}

	@Override
	public Color getBackgroundColor() {
		return backgroundColor != null ? backgroundColor : parentStyle.getBackgroundColor();
	}

	public CascadingCellStyle setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}
	
}
