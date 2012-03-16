package com.kogasoftware.odt.webapi;

public class WebAPIException extends Exception {
	private static final long serialVersionUID = -6992514460783010534L;
	protected final Boolean retryable;
	protected final String message;

	public static class EmptyException extends Exception {
		private static final long serialVersionUID = 8479334770451198146L;
	}

	public WebAPIException(Boolean retryable, String message, Throwable cause) {
		super(cause);
		this.retryable = retryable;
		this.message = message;
	}

	public WebAPIException(Boolean retryable, Exception cause) {
		this(retryable, cause.getClass().getSimpleName(), cause);
	}

	public WebAPIException(Boolean retryable, String message) {
		this(retryable, message, null);
	}

	public Boolean isRetryable() {
		return retryable;
	}
}
