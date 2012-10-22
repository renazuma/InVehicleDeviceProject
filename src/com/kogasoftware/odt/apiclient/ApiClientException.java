package com.kogasoftware.odt.apiclient;

public class ApiClientException extends Exception {
	private static final long serialVersionUID = -6992514460783010534L;

	public ApiClientException(Exception cause) {
		super(cause);
	}

	public ApiClientException(String message) {
		super(message);
	}
}
