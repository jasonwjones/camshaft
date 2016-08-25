package com.saxifrages.hyperion.camshaft.delegates;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;

public class PrintStreamCamshaftOutputDelegate extends AbstractGridOutputDelegate {

	protected PrintStream printStream;
	
	// can also use '%s\t'
	private String formatString = "%-24s";
	
	private String delimiter = DEFAULT_DELIMITER;
	
	public final static String DEFAULT_FORMAT_STRING = "%s";
	
	public final static String DEFAULT_DELIMITER = "\t";
	
	public PrintStreamCamshaftOutputDelegate() {
		this.printStream = new PrintStream(System.out);
	}
	
	public PrintStreamCamshaftOutputDelegate(OutputStream outputStream) {
		this.printStream = new PrintStream(outputStream);
	}
	
	public PrintStreamCamshaftOutputDelegate(OutputStream outputStream, Properties properties) {
		this.printStream = new PrintStream(outputStream);
		this.formatString = properties.getProperty("formatString", DEFAULT_FORMAT_STRING);
		this.delimiter = properties.getProperty("delimiter", DEFAULT_DELIMITER);
	}
	
	public void startOutput() {
	}
	
	public void startHeader() {}
	
	public void endHeader() {}

	public void addRow() {
		//printStream.println();
	}

	public void endOutput() {
	}

	public void addTuple(String[] values) {
		for (String value : values) {
			printStream.printf(formatString, value);
		}
	}

	public void endRow() {
		printStream.println();
	}

	public void addValue(String value) {
		printStream.printf(formatString, value);
		printStream.print(delimiter);
	}
	
	public void addValue() {
		addValue("");
	}

	public void addHeaderTuple(String[] values) {
		StringBuilder sb = new StringBuilder();
		for (String value : values) {
			sb.append(value);
			sb.append(" ");
		}
		printStream.printf(formatString, sb.toString());
	}

	public void addHeaderSpace() {
		printStream.printf(formatString, "");
	}

}
