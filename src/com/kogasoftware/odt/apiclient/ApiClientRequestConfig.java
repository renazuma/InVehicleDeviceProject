package com.kogasoftware.odt.apiclient;

import java.io.Serializable;

public class ApiClientRequestConfig implements Serializable {
	private static final long serialVersionUID = -7664904125371847081L;
	private Boolean retry = true;
	private Boolean saveOnClose = false;

	public boolean getRetry() {
		return retry;
	}
	public void setRetry(Boolean retry) {
		this.retry = retry;
	}
	public boolean getSaveOnClose() {
		return saveOnClose;
	}
	public void setSaveOnClose(Boolean saveOnClose) {
		this.saveOnClose = saveOnClose;
	}
}
