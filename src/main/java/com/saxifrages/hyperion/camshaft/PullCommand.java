package com.saxifrages.hyperion.camshaft;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(separators = "=")
public class PullCommand {

	@Parameter(names = "--server", description = "Essbase server", required = true)
	private String server;
	
	@Parameter(names = "--user", description = "User to connect to server as", required = true)
	private String username;
	
	@Parameter(names = "--password", description = "Password for user",  required = true)
	private String password;
	
	@Parameter(names = {"--app", "--application"}, description = "Essbase app", required = true)
	private String application;
	
	@Parameter(names = {"--database", "--db"}, description = "Essbase cube", required = true)
	private String database;
	
	@Parameter(names = {"--query"}, description = "Query to execute, or filename containing query", required = true)
	private String query;

	@Parameter(names = {"--outfile"}, description = "File to output results to")
	private String outFile;
	
	@Parameter(names = {"--no-header"}, description = "Suppress the header on the output")
	private boolean suppressHeader;
	
	@Parameter(names = {"--no-access-text"}, description = "Text for #NoAccess cells")
	private String noAccessText = "#NoAccess";
	
	@Parameter(names = {"--no-indent-header"}, description = "Indent header rows (to align with data)")
	private boolean noIndentHeader;
	
	@Parameter(names = {"--missing-text"}, description = "Text for #Missing cells")
	private String missingText = "#Missing";

	@Parameter(names = {"--tuple-separator"}, description = "Tuple separator text for header rows. Default is comma with a space")
	private String tupleSeparator = ", ";
	
	@Parameter(names = {"--output-engine"}, description = "Output engine (plain or html), default is plain")
	private String outputEngine = "plain";
	
	@Parameter(names = {"--line-per-header"}, description = "Print column headers with one tuple member per line instead of combined")
	private boolean linePerHeader = false;
	
	@DynamicParameter(names = "-D", description = "Dynamic parameters go here")
	private Map<String, String> params = new HashMap<String, String>();

	public String getServer() {
		return server;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getApplication() {
		return application;
	}

	public String getDatabase() {
		return database;
	}

	public String getQuery() {
		return query;
	}
	
	public String getOutFile() {
		return outFile;
	}
	
	public boolean getSuppressHeader() {
		return suppressHeader;
	}

	public String getOutputEngine() {
		return outputEngine;
	}
	
	public Map<String, String> getParams() {
		return params;
	}
	
	public Properties getParamsAsProperties() {
		return mapToProperties(params);
	}
	
	public static Properties mapToProperties(Map<String, String> map) {
		Properties properties = new Properties();
		for (String key : map.keySet()) {
			properties.put(key, map.get(key));
		}
		return properties;
	}

	public String getNoAccessText() {
		return noAccessText;
	}

	public String getMissingText() {
		return missingText;
	}

	public boolean isLinePerHeader() {
		return linePerHeader;
	}
	
	public boolean isSuppressHeader() {
		return suppressHeader;
	}
	
	public boolean isNoIdentHeader() {
		return noIndentHeader;
	}
	
	public String getTupleSeparator() {
		return tupleSeparator;
	}
	
}
