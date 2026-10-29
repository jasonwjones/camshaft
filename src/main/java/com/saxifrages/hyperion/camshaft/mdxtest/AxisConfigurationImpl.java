package com.saxifrages.hyperion.camshaft.mdxtest;

import java.util.List;

public class AxisConfigurationImpl implements AxisConfiguration {

	private Integer rowsAbove;
	
	private Integer rowsBelow;
	
	private Boolean renderPropertiesToColumns = false;
	
	private String formatTemplate;
	
	private String tupleJoinText = ", ";
	
	@Override
	public List<SyntheticMemberConfiguration> getSyntheticMemberConfigurations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getRowsAbove() {
		return rowsAbove;
	}

	@Override
	public Integer getRowsBelow() {
		return rowsBelow;
	}

	@Override
	public Boolean isRenderPropertiesToColumns() {
		return renderPropertiesToColumns;
	}

	public void setRenderPropertiesToColumns(Boolean renderPropertiesToColumns) {
		this.renderPropertiesToColumns = renderPropertiesToColumns;
	}

	@Override
	public String getFormatTemplate() {
		return formatTemplate;
	}

	public void setFormatTemplate(String formatTemplate) {
		this.formatTemplate = formatTemplate;
	}

	@Override
	public String getTupleJoinText() {
		return tupleJoinText;
	}

	public void setTupleJoinText(String tupleJoinText) {
		this.tupleJoinText = tupleJoinText;
	}

}
