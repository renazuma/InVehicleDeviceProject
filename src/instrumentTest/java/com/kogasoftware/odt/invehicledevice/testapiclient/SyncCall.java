package com.kogasoftware.odt.invehicledevice.testapiclient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.kogasoftware.odt.apiclient.ApiClientCallback;
import com.kogasoftware.odt.apiclient.ApiClientException;

public abstract class SyncCall<T> implements ApiClientCallback<T> {
	public final static int SUCCEED = 0;
	public final static int FAILED = 1;
	public final static int EXCEPTION = 2;

	protected int callback;
	protected int reqkey;
	protected int statusCode;
	protected T result;
	protected Exception exception;

	CountDownLatch latch;
	private String responseString;
	
	abstract public int run() throws Exception;

	public SyncCall() throws Exception {
		this(60, TimeUnit.SECONDS);
	}

	public SyncCall(int timeout, TimeUnit unit) throws Exception {
		latch = new CountDownLatch(1);
		reqkey = run();
		
		if (!latch.await(timeout, unit)) {
			throw new TimeoutException("timeout=" + timeout + " unit=" + unit);
		}
	}

	public int getCallback() {
		return callback;
	}
	
	public int getReqkey() {
		return reqkey;
	}
	
	public int getStatusCode() {
		return statusCode;
	}
	
	public T getResult() {
		return result;
	}
	
	public Exception getException() {
		return exception;
	}
	
	public String getResponseString() {
		return responseString;
	}

	@Override
	public void onSucceed(int reqkey, int statusCode, T result) {
		this.callback = SUCCEED;
		this.statusCode = statusCode;
		this.result = result;
		latch.countDown();
	}

	@Override
	public void onFailed(int reqkey, int statusCode, String response) {
		this.callback = FAILED;
		this.statusCode = statusCode;
		this.responseString = response;
		latch.countDown();
	}

	@Override
	public void onException(int reqkey, ApiClientException ex) {
		this.callback = EXCEPTION;
		this.exception = ex;
		latch.countDown();
	}

}
