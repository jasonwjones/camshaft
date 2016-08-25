package com.saxifrages.hyperion.camshaft;

public class PrintOptions {

	//private String delimiter = "\t";
	
	private boolean suppressHeader = false;
	
	private String noAccessText = "#NoAccess";
	
	private String missingText = "#Missing";

	private String headerTupleSeparator = ", ";
	
	private boolean indentHeader = true;
	
	private boolean linePerHeaderTupleMember = false;
	
//	public String getDelimiter() {
//		return delimiter;
//	}
//
//	public void setDelimiter(String delimiter) {
//		this.delimiter = delimiter;
//	}

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
	
}
