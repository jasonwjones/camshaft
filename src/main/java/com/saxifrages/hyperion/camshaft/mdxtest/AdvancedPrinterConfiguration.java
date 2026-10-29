package com.saxifrages.hyperion.camshaft.mdxtest;

import java.util.List;

public interface AdvancedPrinterConfiguration {

	public List<AxisConfiguration> getAxisConfigurations();
	
	public AxisConfiguration getConfiguration(int axisIndex);

	public boolean isPrintData();
	
}
