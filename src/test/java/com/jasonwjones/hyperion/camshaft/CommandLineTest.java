package com.jasonwjones.hyperion.camshaft;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

import picocli.CommandLine;

/**
 * How the command line answers without a server: help, versions and bad
 * options.
 */
public class CommandLineTest {

	private static int run(StringWriter out, String... args) {
		CommandLine commandLine = Camshaft.commandLine();
		commandLine.setOut(new PrintWriter(out));
		commandLine.setErr(new PrintWriter(out));
		return commandLine.execute(args);
	}

	@Test
	public void helpAndVersionSucceed() {
		StringWriter out = new StringWriter();
		assertEquals(Camshaft.OK, run(out, "--help"));
		assertEquals(Camshaft.OK, run(out, "run", "--help"));
		assertEquals(Camshaft.OK, run(out, "--version"));
	}

	@Test
	public void aBadOptionFailsWithStatusTwo() {
		StringWriter out = new StringWriter();
		assertEquals(Camshaft.FAILED, run(out, "run", "--no-such-option"));
		assertEquals(Camshaft.FAILED, run(out, "members", "Sample.Basic", "Product", "--level", "0", "--generation", "2"));
		assertEquals(Camshaft.FAILED, run(out, "run", "--format", "docx", "SELECT {} ON 0 FROM Sample.Basic"));
	}

}
