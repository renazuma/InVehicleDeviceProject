package com.kogasoftware.odt.webapi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.client.methods.HttpRequestBase;

import android.util.Log;

import com.kogasoftware.odt.webapi.WebAPI.ResponseConverter;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.serializablerequestloader.SerializableRequestLoader;

public class WebAPIRequest<T> implements Serializable {
	private static final long serialVersionUID = -8451453777378477195L;
	private static final String TAG = WebAPIRequest.class.getSimpleName();
	protected static final AtomicInteger REQ_KEY_COUNTER = new AtomicInteger(0);
	protected final SerializableRequestLoader firstRequest;
	protected final SerializableRequestLoader retryRequest;
	protected final int reqkey = REQ_KEY_COUNTER.incrementAndGet();
	protected final Date createdDate = new Date();

	protected WebAPIRequestConfig config = new WebAPIRequestConfig();
	protected boolean retry;
	protected transient WebAPICallback<T> callback;
	protected transient ResponseConverter<? extends T> responseConverter;

	private void readObject(ObjectInputStream objectInputStream)
			throws IOException, ClassNotFoundException {
		objectInputStream.defaultReadObject();
		callback = null;
		responseConverter = null;
	}

	public WebAPIRequest(WebAPICallback<T> callback,
			ResponseConverter<? extends T> responseConverter,
			SerializableRequestLoader request) {
		this(callback, responseConverter, request, request);
	}

	protected WebAPIRequest(WebAPICallback<T> callback,
			ResponseConverter<? extends T> responseConverter,
			SerializableRequestLoader firstRequest,
			SerializableRequestLoader retryRequest) {
		this.callback = callback;
		this.responseConverter = responseConverter;
		this.firstRequest = firstRequest;
		this.retryRequest = retryRequest;
	}

	public Date getCreatedDate() {
		return new Date(createdDate.getTime());
	}

	public int getReqKey() {
		return reqkey;
	}

	public HttpRequestBase getRequest() throws WebAPIException {
		return retry ? retryRequest.load() : firstRequest.load();
	}
	
	public void onException(WebAPIException e) {
		if (callback != null) {
			callback.onException(reqkey, e);
		}
	}

	public void onFailed(int statusCode, String response) {
		if (callback != null) {
			callback.onFailed(reqkey, statusCode, response);
		}
	}

	public void onSucceed(int statusCode, byte[] rawResult) throws Exception {
		if (responseConverter != null && callback != null) {
			callback.onSucceed(reqkey, statusCode,
					responseConverter.convert(rawResult));
		}
	}

	public void setRetry(boolean retry) {
		this.retry = retry;
	}

	public void abort() {
		try {
			firstRequest.load().abort();
		} catch (WebAPIException e) {
			Log.w(TAG, e);
		}
		try {
			retryRequest.load().abort();
		} catch (WebAPIException e) {
			Log.w(TAG, e);
		}
		onException(new WebAPIException("Connection aborted by application"));
	}
	
	public void setConfig(WebAPIRequestConfig config) {
		this.config = config;
	}
	
	public WebAPIRequestConfig getConfig() {
		return config;
	}
}
