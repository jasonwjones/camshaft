package com.saxifrages.hyperion.camshaft.advanced;

import java.util.List;

public interface AxisRenderingOptions {

	public interface AxisRenderingElement {
		
		public String getContents();
		
	}
	
	public List<AxisRenderingElement> getHeaders();
	
	public List<AxisRenderingElement> getFooters();
	
}
