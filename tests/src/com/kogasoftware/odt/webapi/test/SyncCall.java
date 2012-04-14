package com.kogasoftware.odt.webapi.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;

public abstract class SyncCall<T> implements WebAPICallback<T> {
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
		this(15, TimeUnit.SECONDS);
	}

	public SyncCall(int timeout, TimeUnit unit) throws Exception {
		latch = new CountDownLatch(1);
		reqkey = run();
		
		try {
			latch.await(timeout, unit);
		} catch (InterruptedException e) {
			e.printStackTrace();
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
	public void onException(int reqkey, WebAPIException ex) {
		this.callback = EXCEPTION;
		this.exception = ex;
		latch.countDown();
	}

}
