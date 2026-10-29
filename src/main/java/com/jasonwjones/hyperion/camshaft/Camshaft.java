package com.jasonwjones.hyperion.camshaft;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.IVersionProvider;
import picocli.CommandLine.Option;
import picocli.CommandLine.ScopeType;

/**
 * Runs Essbase MDX queries and writes the results where you want them.
 *
 * Each command is its own class. They run MDX through Thriller, so a result
 * is shaped exactly as it would be in a SQL tool, and use essbase-rest-client
 * directly for what JDBC doesn't cover.
 */
@Command(name = "camshaft", mixinStandardHelpOptions = true, versionProvider = Camshaft.Version.class,
		description = "Runs Essbase MDX queries and writes the results where you want them.",
		subcommands = { RunCommand.class, BatchCommand.class, DiffCommand.class, AppsCommand.class, CubesCommand.class,
				DimensionsCommand.class, MembersCommand.class, CommandLine.HelpCommand.class },
		footer = { "", "Exit status: 0 when it worked, 1 when diff found differences, 2 when something went wrong." })
public class Camshaft implements Runnable {

	/** Every command worked, or diff found no differences */
	static final int OK = 0;

	/** diff found differences */
	static final int DIFFERENT = 1;

	/** Something went wrong: a bad option, a failed login, a failed query */
	static final int FAILED = 2;

	@Option(names = { "-v", "--verbose" }, scope = ScopeType.INHERIT, description = "Show what's happening, and the whole story when something fails")
	boolean verbose;

	@Override
	public void run() {
		CommandLine.usage(this, System.out);
	}

	public static void main(String[] args) {
		// Before anything logs, since the logger reads this once
		if (Arrays.asList(args).contains("-v") || Arrays.asList(args).contains("--verbose")) {
			System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
		}
		System.exit(commandLine().execute(args));
	}

	/**
	 * @return the command line, ready to run
	 */
	static CommandLine commandLine() {
		CommandLine commandLine = new CommandLine(new Camshaft());
		commandLine.setCaseInsensitiveEnumValuesAllowed(true);
		// Bad options and failures share one status, whichever command they're in
		for (CommandLine command : withSubcommands(commandLine)) {
			command.getCommandSpec().exitCodeOnInvalidInput(FAILED).exitCodeOnExecutionException(FAILED);
		}
		commandLine.setExecutionExceptionHandler((exception, line, parseResult) -> {
			PrintWriter err = line.getErr();
			err.println("camshaft: " + message(exception));
			if (parseResult.hasMatchedOption("--verbose")) {
				exception.printStackTrace(err);
			}
			err.flush();
			return FAILED;
		});
		return commandLine;
	}

	private static List<CommandLine> withSubcommands(CommandLine commandLine) {
		List<CommandLine> all = new ArrayList<>();
		all.add(commandLine);
		for (CommandLine subcommand : commandLine.getSubcommands().values()) {
			all.addAll(withSubcommands(subcommand));
		}
		return all;
	}

	/**
	 * A failure's message, with the cause's where the failure itself just wraps
	 * one.
	 */
	static String message(Throwable exception) {
		Throwable shown = exception;
		while (shown.getMessage() == null && shown.getCause() != null) {
			shown = shown.getCause();
		}
		return shown.getMessage() != null ? shown.getMessage() : shown.getClass().getSimpleName();
	}

	/**
	 * The version the build recorded.
	 */
	static final class Version implements IVersionProvider {

		@Override
		public String[] getVersion() throws IOException {
			Properties properties = new Properties();
			try (InputStream in = Camshaft.class.getResourceAsStream("camshaft.properties")) {
				if (in != null) {
					properties.load(in);
				}
			}
			return new String[] { "Camshaft " + properties.getProperty("version", "(unknown version)") };
		}

	}

}
