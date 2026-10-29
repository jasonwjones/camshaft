package com.jasonwjones.hyperion.camshaft;

import java.util.Locale;

/**
 * The formats Camshaft writes, with the file extension each is known by.
 */
public enum Format {

	/** Comma-separated values, quoted where needed */
	CSV("csv"),

	/** Tab-separated values, quoted as CSV is */
	TSV("tsv"),

	/** A JSON array with an object for each row */
	JSON("json"),

	/** One JSON object per line */
	JSONL("jsonl"),

	/** A Markdown table */
	MARKDOWN("md"),

	/** A text table with borders, for reading in a terminal */
	TABLE("txt"),

	/** An HTML table */
	HTML("html"),

	/** An Excel workbook, as Essbase lays the grid out itself */
	XLSX("xlsx");

	private final String extension;

	Format(String extension) {
		this.extension = extension;
	}

	/**
	 * @return the extension of a file in this format, without the dot
	 */
	public String extension() {
		return extension;
	}

	/**
	 * @return whether this is text, as all but Excel are
	 */
	public boolean isText() {
		return this != XLSX;
	}

	/**
	 * The format a file's name says it's in.
	 *
	 * @param fileName a file name, such as sales.csv
	 * @return its format, or null if the extension isn't one Camshaft knows
	 */
	public static Format fromFileName(String fileName) {
		int dot = fileName.lastIndexOf('.');
		if (dot < 0) {
			return null;
		}
		String extension = fileName.substring(dot + 1).toLowerCase(Locale.ROOT);
		switch (extension) {
		case "markdown":
			return MARKDOWN;
		case "htm":
			return HTML;
		case "ndjson":
			return JSONL;
		default:
			for (Format format : values()) {
				if (format.extension.equals(extension)) {
					return format;
				}
			}
			return null;
		}
	}

}
