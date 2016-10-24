package com.saxifrages.hyperion.camshaft.delegates;

import java.io.PrintStream;
import java.util.Properties;

import com.saxifrages.hyperion.camshaft.CamshaftOutputDelegate;

/**
 * Base class for output delegates, providing common functionality and
 * convenience methods
 * 
 * @author jasonwjones
 *
 */
public abstract class AbstractGridOutputDelegate implements CamshaftOutputDelegate {

	public static final String DEFAULT_SEPARATOR = ", ";

	public static final Properties NO_PROPS = new Properties();

	public static void outputArray(String[] items, PrintStream printStream) {
		outputArray(items, printStream, DEFAULT_SEPARATOR);
	}

	public static void outputArray(String[] items, PrintStream printStream, String separator) {
		if (items.length == 1) {
			printStream.print(items[0]);
		} else {
			for (int index = 0; index < items.length - 1; index++) {
				printStream.print(items[index]);
				printStream.print(separator);
			}
			// handles the edge case of items.length == 0; nothing happens
			if (items.length > 0) {
				printStream.print(items[items.length - 1]);
			}
		}
	}

}
