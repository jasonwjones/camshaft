package com.saxifrages.hyperion.camshaft.advanced.style;

public class CascadingBorder implements Border {

	private Border parentBorder;
	
	private Color color;
	
	private Integer width;
	
	public CascadingBorder(Border parentBorder) {
		this.parentBorder = parentBorder;
	}

	@Override
	public Color getColor() {
		return color != null ? color : parentBorder.getColor();
	}

	@Override
	public Integer getWidth() {
		return width != null ? width : parentBorder.getWidth();
	}
		
	@Override
	public Border setWidth(Integer width) {
		this.width = width;
		return this;
	}
	
}
