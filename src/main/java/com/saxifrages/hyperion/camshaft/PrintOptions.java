package com.saxifrages.hyperion.camshaft;

import java.util.Properties;

public class PrintOptions {

	private boolean suppressHeader = false;
	
	private String noAccessText = "#NoAccess";
	
	private String missingText = "#Missing";

	private String headerTupleSeparator = ",";
	
	private boolean indentHeader = true;
	
	private boolean linePerHeaderTupleMember = false;
	
	private int maximumFractionDigits = 6;

	private boolean printProperties = true;
	
	private boolean printPropertyNames = false;
	
	private boolean printPropertiesSeparately = false;
	
	public PrintOptions() {
	}
	
	public PrintOptions(Properties properties) {
		suppressHeader = Boolean.getBoolean(properties.getProperty("suppress-header", "false"));
		noAccessText = properties.getProperty("no-access-text", "#NoAccess");
		missingText = properties.getProperty("missing-text", "#Missing");
		headerTupleSeparator = properties.getProperty("header-tuple-separator", ",");
	}
	
	public boolean isSuppressHeader() {
		return suppressHeader;
	}

	public void setSuppressHeader(boolean suppressHeader) {
		this.suppressHeader = suppressHeader;
	}

	public String getNoAccessText() {
		return noAccessText;
	}

	public void setNoAccessText(String noAccessText) {
		this.noAccessText = noAccessText;
	}

	public String getMissingText() {
		return missingText;
	}

	public void setMissingText(String missingText) {
		this.missingText = missingText;
	}

	public String getHeaderTupleSeparator() {
		return headerTupleSeparator;
	}

	public void setHeaderTupleSeparator(String headerTupleSeparator) {
		this.headerTupleSeparator = headerTupleSeparator;
	}

	public boolean isIndentHeader() {
		return indentHeader;
	}

	public void setIndentHeader(boolean indentHeader) {
		this.indentHeader = indentHeader;
	}

	public boolean isLinePerHeaderTupleMember() {
		return linePerHeaderTupleMember;
	}

	public void setLinePerHeaderTupleMember(boolean linePerHeaderTupleMember) {
		this.linePerHeaderTupleMember = linePerHeaderTupleMember;
	}

	public int getMaximumFractionDigits() {
		return maximumFractionDigits;
	}

	public void setMaximumFractionDigits(int maximumFractionDigits) {
		this.maximumFractionDigits = maximumFractionDigits;
	}

	public boolean isPrintProperties() {
		return printProperties;
	}

	public void setPrintProperties(boolean printProperties) {
		this.printProperties = printProperties;
	}

	public boolean isPrintPropertyNames() {
		return printPropertyNames;
	}

	public void setPrintPropertyNames(boolean printPropertyNames) {
		this.printPropertyNames = printPropertyNames;
	}

	public boolean isPrintPropertiesSeparately() {
		return printPropertiesSeparately;
	}

	public void setPrintPropertiesSeparately(boolean printPropertiesSeparately) {
		this.printPropertiesSeparately = printPropertiesSeparately;
	}

}