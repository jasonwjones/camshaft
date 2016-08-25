package com.saxifrages.hyperion.camshaft.delegates;

import java.io.OutputStream;
import java.util.Properties;

public class HtmlTableOutputDelegate extends PrintStreamCamshaftOutputDelegate {

	private String tupleJoinText = " / ";
	
	private boolean includeHtmlTag = false;
	
	public HtmlTableOutputDelegate() {
		this(System.out);
	}

	public HtmlTableOutputDelegate(OutputStream outputStream) {
		this(outputStream, NO_PROPS); 
	}

	public HtmlTableOutputDelegate(OutputStream outputStream, Properties properties) {
		super(outputStream, properties);
		this.includeHtmlTag = Boolean.valueOf(properties.getProperty("includeHtmlTag", "false"));
	}

	@Override
	public void startOutput() {
		if (includeHtmlTag) {
			printStream.println("<html><body>");
		}
		printStream.println("<table border='1'>");
	}

	public void startHeader() {
		printStream.println("<thead>");
	}
	
	public void endHeader() {
		printStream.println("</thead>");
	}
	
	@Override
	public void addRow() {
		printStream.println("\t<tr>");
	}

	@Override
	public void addHeaderSpace() {
		printStream.println("\t\t<td></td>");
	}

	@Override
	public void addHeaderTuple(String[] values) {
		printStream.print("\t\t<td>");
		printStream.print(join(values, tupleJoinText));
		printStream.println("</td>");
	}

	@Override
	public void addTuple(String[] values) {
		printStream.print("\t\t<td>");
		for (String value : values) {
			printStream.print(value);
			printStream.print(" ");
		}
		printStream.println("</td>");
	}

	@Override
	public void addValue(String value) {
		printStream.print("\t\t<td>");
		printStream.print(value);
		printStream.println("</td>");
	}

	@Override
	public void endRow() {
		printStream.println("\t</tr>");
	}

	@Override
	public void endOutput() {
		printStream.println("</table>");
		if (includeHtmlTag) {
			printStream.println("</body></html>");
		}
	}

	private static String join(String[] values, String delimiter) {
		if (values.length == 1) {
			return values[0];
		} else {
			StringBuilder sb = new StringBuilder(values[0]);
			for (int index = 1; index < values.length; index++) {
				sb.append(delimiter);
				sb.append(values[index]);
			}
			return sb.toString();
		}
	}
	
}
