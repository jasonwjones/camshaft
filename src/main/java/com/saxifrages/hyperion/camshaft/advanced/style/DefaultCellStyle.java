package com.saxifrages.hyperion.camshaft.advanced.style;

public class DefaultCellStyle implements CellStyle {

	private static final Color DEFAULT_COLOR = new DefaultColor();
	
	private static final Border DEFAULT_LEFT_BORDER = new DefaultBorder(Colors.NONE, 0);
	
	@Override
	public Border getLeftBorder() {		
		return DEFAULT_LEFT_BORDER;
	}

	@Override
	public Color getBackgroundColor() {
		return DEFAULT_COLOR;
	}

}
