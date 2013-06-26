package com.kogasoftware.odt.apiclient;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.base.Objects;

public class DefaultApiClientRequestConfig implements Serializable {
	private static final long serialVersionUID = -7664904125371847082L;
	protected static final AtomicInteger REQ_KEY_COUNTER = new AtomicInteger(0);
	private final int reqkey = REQ_KEY_COUNTER.incrementAndGet();
	private Boolean retry = true;
	private Boolean saveOnClose = false;
	
	public int getReqkey() {
		return reqkey;
	}
	
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

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("retry", retry)
				.add("saveOnClose", saveOnClose)
				.add("reqkey", reqkey).toString();
	}
}
