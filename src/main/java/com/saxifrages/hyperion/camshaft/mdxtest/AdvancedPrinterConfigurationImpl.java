package com.saxifrages.hyperion.camshaft.mdxtest;

import java.util.List;

public class AdvancedPrinterConfigurationImpl implements AdvancedPrinterConfiguration {

	private AxisConfiguration defaultConfiguration;
	
	private List<AxisConfiguration> axisConfigurations;
	
	private boolean printData;
	
	public AdvancedPrinterConfigurationImpl(AxisConfiguration defaultConfiguration) {
		this.defaultConfiguration = defaultConfiguration;
	}
	
	public AdvancedPrinterConfigurationImpl(List<AxisConfiguration> axisConfigurations) {
		this.axisConfigurations = axisConfigurations;
	}
	
	@Override
	public List<AxisConfiguration> getAxisConfigurations() {
		return axisConfigurations;
	}
	
	@Override
	public AxisConfiguration getConfiguration(int axisIndex) {
		return defaultConfiguration;
		//return axisConfigurations.get(axisIndex);
	}

	@Override
	public boolean isPrintData() {
		return printData;
	}

	public void setPrintData(boolean printData) {
		this.printData = printData;
	}	
	
	private static class DelegatingAxisConfiguration implements AxisConfiguration {

		private AxisConfiguration root;
		
		private AxisConfiguration axisConfiguration;
		
		@Override
		public List<SyntheticMemberConfiguration> getSyntheticMemberConfigurations() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Integer getRowsAbove() {
			Integer rowsAbove = axisConfiguration.getRowsAbove();
			if (rowsAbove == null) {
				return root.getRowsAbove();
			} else {
				return rowsAbove;
			}
		}

		@Override
		public Integer getRowsBelow() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Boolean isRenderPropertiesToColumns() {
			Boolean renderPropertiesToColumns = axisConfiguration.isRenderPropertiesToColumns();
			if (renderPropertiesToColumns == null) {
				return root.isRenderPropertiesToColumns();
			} else {
				return renderPropertiesToColumns;
			}
		}

		@Override
		public String getFormatTemplate() {
			return coalesce(axisConfiguration.getFormatTemplate(), root.getFormatTemplate());
		}
		
		private static String coalesce(String value, String defaultValue) {
			if (value == null) {
				return defaultValue;
			}
			return value;
		}

		@Override
		public String getTupleJoinText() {
			return coalesce(axisConfiguration.getTupleJoinText(), root.getTupleJoinText());
		}
		
	}
	
}
