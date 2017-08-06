package com.saxifrages.hyperion.camshaft.mdxtest;

import java.util.List;

public interface AxisConfiguration {

	public List<SyntheticMemberConfiguration> getSyntheticMemberConfigurations();
	
	public Integer getRowsAbove();
	
	public Integer getRowsBelow();
	
}
