package com.kogasoftware.odt.webapi;

public class WebAPIException extends Exception {
	private static final long serialVersionUID = -6992514460783010534L;

	public WebAPIException(Exception cause) {
		super(cause);
	}

	public WebAPIException(String message) {
		super(message);
	}
}
