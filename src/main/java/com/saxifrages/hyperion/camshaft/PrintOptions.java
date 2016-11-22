package com.saxifrages.hyperion.camshaft;

public interface PrintOptions {

	boolean isSuppressHeader();

	String getNoAccessText();

	String getMissingText();

	String getHeaderTupleSeparator();

	boolean isIndentHeader();

	boolean isLinePerHeaderTupleMember();

	int getMaximumFractionDigits();

	boolean isPrintProperties();

	boolean isPrintPropertyNames();

	boolean isPrintPropertiesSeparately();

	boolean isPrintTupleMemberSeparately();

}