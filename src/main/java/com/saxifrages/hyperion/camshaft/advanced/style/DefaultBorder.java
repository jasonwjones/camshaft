package com.saxifrages.hyperion.camshaft.advanced.style;

public class DefaultBorder implements Border {

	private Color color;
	
	private Integer width;
	
	public DefaultBorder(Color color, Integer width) {
		this.color = color;
		this.width = width;
	}
	
	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public Integer getWidth() {
		return width;
	}

	@Override
	public Border setWidth(Integer width) {
		this.width = width;
		return this;
	}

}
