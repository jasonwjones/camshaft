package com.saxifrages.hyperion.camshaft.delegates;

import java.io.PrintStream;

public class IndentingPrinter {

	public static final String DEFAULT_INDENT = "\t";

	private PrintStream printStream;

	private String indentText;

	private String globalIndentText;
	
	public IndentingPrinter(PrintStream printStream) {
		this(printStream, DEFAULT_INDENT);
	}

	public IndentingPrinter(PrintStream printStream, String indentText) {
		this(printStream, indentText, 0);
	}

	public IndentingPrinter(PrintStream printStream, String indentText, int globalIndent) {
		this.printStream = printStream;
		this.indentText = indentText;
		this.globalIndentText = repeat(indentText, globalIndent);
	}
	
	public void print(String s) {
		printStream.print(globalIndentText);
		printStream.print(s);
	}

	public void print(int indentTimes, String s) {
		printStream.print(globalIndentText);
		indent(indentTimes);
		printStream.print(s);
	}

	public void println(String s) {
		printStream.print(globalIndentText);
		printStream.println(s);
	}

	public void println(int indentTimes, String s) {
		printStream.print(globalIndentText);
		indent(indentTimes);
		printStream.println(s);
	}

	private void indent(int indentTimes) {
		for (int i = 0; i < indentTimes; i++) {
			printStream.print(indentText);
		}
	}
	
	private static String repeat(String text, int times) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < times; i++) {
			sb.append(text);
		}
		return sb.toString();
	}
	
}
