package com.saxifrages.hyperion.camshaft;

import java.util.Properties;

public class BasicPrintOptions implements PrintOptions {

	private boolean suppressHeader = false;

	private String noAccessText = "#NoAccess";

	private String missingText = "#Missing";

	private String headerTupleSeparator = ",";

	private boolean indentHeader = true;

	private boolean linePerHeaderTupleMember = false;

	private int maximumFractionDigits = 6;

	private int minimumFractionDigits = 0;

	private boolean printProperties = true;

	private boolean printPropertyNames = false;

	private boolean printTupleMemberSeparately = false;

	private boolean printPropertiesSeparately = false;

	public BasicPrintOptions() {
	}

	public BasicPrintOptions(Properties properties) {
		suppressHeader = Boolean.getBoolean(properties.getProperty("suppress-header", "false"));
		noAccessText = properties.getProperty("no-access-text", "#NoAccess");
		missingText = properties.getProperty("missing-text", "#Missing");
		headerTupleSeparator = properties.getProperty("header-tuple-separator", ",");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.saxifrages.hyperion.camshaft.PrintOptions#isSuppressHeader()
	 */
	@Override
	public boolean isSuppressHeader() {
		return suppressHeader;
	}

	public void setSuppressHeader(boolean suppressHeader) {
		this.suppressHeader = suppressHeader;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.saxifrages.hyperion.camshaft.PrintOptions#getNoAccessText()
	 */
	@Override
	public String getNoAccessText() {
		return noAccessText;
	}

	public void setNoAccessText(String noAccessText) {
		this.noAccessText = noAccessText;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.saxifrages.hyperion.camshaft.PrintOptions#getMissingText()
	 */
	@Override
	public String getMissingText() {
		return missingText;
	}

	public void setMissingText(String missingText) {
		this.missingText = missingText;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.saxifrages.hyperion.camshaft.PrintOptions#getHeaderTupleSeparator()
	 */
	@Override
	public String getHeaderTupleSeparator() {
		return headerTupleSeparator;
	}

	public void setHeaderTupleSeparator(String headerTupleSeparator) {
		this.headerTupleSeparator = headerTupleSeparator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.saxifrages.hyperion.camshaft.PrintOptions#isIndentHeader()
	 */
	@Override
	public boolean isIndentHeader() {
		return indentHeader;
	}

	public void setIndentHeader(boolean indentHeader) {
		this.indentHeader = indentHeader;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.saxifrages.hyperion.camshaft.PrintOptions#isLinePerHeaderTupleMember(
	 * )
	 */
	@Override
	public boolean isLinePerHeaderTupleMember() {
		return linePerHeaderTupleMember;
	}

	public void setLinePerHeaderTupleMember(boolean linePerHeaderTupleMember) {
		this.linePerHeaderTupleMember = linePerHeaderTupleMember;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.saxifrages.hyperion.camshaft.PrintOptions#getMaximumFractionDigits()
	 */
	@Override
	public int getMaximumFractionDigits() {
		return maximumFractionDigits;
	}

	public void setMaximumFractionDigits(int maximumFractionDigits) {
		this.maximumFractionDigits = maximumFractionDigits;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.saxifrages.hyperion.camshaft.PrintOptions#isPrintProperties()
	 */
	@Override
	public boolean isPrintProperties() {
		return printProperties;
	}

	public void setPrintProperties(boolean printProperties) {
		this.printProperties = printProperties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.saxifrages.hyperion.camshaft.PrintOptions#isPrintPropertyNames()
	 */
	@Override
	public boolean isPrintPropertyNames() {
		return printPropertyNames;
	}

	public void setPrintPropertyNames(boolean printPropertyNames) {
		this.printPropertyNames = printPropertyNames;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.saxifrages.hyperion.camshaft.PrintOptions#isPrintPropertiesSeparately
	 * ()
	 */
	@Override
	public boolean isPrintPropertiesSeparately() {
		return printPropertiesSeparately;
	}

	public void setPrintPropertiesSeparately(boolean printPropertiesSeparately) {
		this.printPropertiesSeparately = printPropertiesSeparately;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.saxifrages.hyperion.camshaft.PrintOptions#
	 * isPrintTupleMemberSeparately()
	 */
	@Override
	public boolean isPrintTupleMemberSeparately() {
		return printTupleMemberSeparately;
	}

	public void setPrintTupleMemberSeparately(boolean printTupleMemberSeparately) {
		this.printTupleMemberSeparately = printTupleMemberSeparately;
	}

	@Override
	public int getMinimumFractionDigits() {
		return minimumFractionDigits;
	}

	public void setMinimumFractionDigits(int minimumFractionDigits) {
		this.minimumFractionDigits = minimumFractionDigits;
	}

}