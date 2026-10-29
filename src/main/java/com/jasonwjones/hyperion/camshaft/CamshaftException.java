package com.jasonwjones.hyperion.camshaft;

/**
 * Something that stops a command, with a message meant for the person who ran
 * it.
 */
public class CamshaftException extends Exception {

	private static final long serialVersionUID = 1L;

	public CamshaftException(String message) {
		super(message);
	}

	public CamshaftException(String message, Throwable cause) {
		super(message, cause);
	}

}
