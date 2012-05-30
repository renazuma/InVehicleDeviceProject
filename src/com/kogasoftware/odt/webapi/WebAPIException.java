package com.kogasoftware.odt.webapi;

public class WebAPIException extends Exception {
	private static final long serialVersionUID = -6992514460783010534L;

	public static class EmptyException extends Exception {
		private static final long serialVersionUID = 8479334770451198146L;
	}

	public WebAPIException(Exception cause) {
		super(cause);
	}

	public WebAPIException(String message) {
		super(message);
	}
}
