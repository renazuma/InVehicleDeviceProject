package com.kogasoftware.odt.webapi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.client.methods.HttpRequestBase;

import com.kogasoftware.odt.webapi.WebAPI.ResponseConverter;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.serializablehttprequestbasesupplier.SerializableHttpRequestBaseSupplier;

public class WebAPIRequest<T> implements Serializable {
	private static final long serialVersionUID = -8451453777378477195L;
	private static final String TAG = WebAPIRequest.class.getSimpleName();
	protected static final AtomicInteger reqkeyCounter = new AtomicInteger(0);
	protected final SerializableHttpRequestBaseSupplier firstRequest;
	protected final SerializableHttpRequestBaseSupplier retryRequest;
	protected final int reqkey = reqkeyCounter.incrementAndGet();
	protected final Date createdDate = new Date();
	protected final boolean retryable;
	protected boolean retry = false;

	transient protected WebAPICallback<T> callback;
	transient protected ResponseConverter<T> responseConverter;
	
	private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
		objectInputStream.defaultReadObject();
		callback = null;
		responseConverter = null;
	}

	public WebAPIRequest(WebAPICallback<T> callback,
			ResponseConverter<T> responseConverter,
			SerializableHttpRequestBaseSupplier request) {
		this(callback, responseConverter, request, true);
	}

	public WebAPIRequest(WebAPICallback<T> callback,
			ResponseConverter<T> responseConverter,
			SerializableHttpRequestBaseSupplier request, Boolean retryable) {
		this(callback, responseConverter, request, request, retryable);
	}

	public WebAPIRequest(WebAPICallback<T> callback,
			ResponseConverter<T> responseConverter,
			SerializableHttpRequestBaseSupplier firstRequest,
			SerializableHttpRequestBaseSupplier retryRequest) {
		this(callback, responseConverter, firstRequest, retryRequest, true);
	}

	protected WebAPIRequest(WebAPICallback<T> callback,
			ResponseConverter<T> responseConverter,
			SerializableHttpRequestBaseSupplier firstRequest,
			SerializableHttpRequestBaseSupplier retryRequest, Boolean retryable) {
		this.callback = callback;
		this.responseConverter = responseConverter;
		this.firstRequest = firstRequest;
		this.retryRequest = retryRequest;
		this.retryable = retryable;
	}

	public Date getCreatedDate() {
		return new Date(createdDate.getTime());
	}

	public int getReqKey() {
		return reqkey;
	}

	public HttpRequestBase getRequest() throws WebAPIException {
		return retry ? retryRequest.get() : firstRequest.get();
	}

	public boolean isRetryable() {
		return retryable;
	}

	public void onException(WebAPIException e) {
		if (callback != null) {
			callback.onException(reqkey, e);
		}
		retry = true;
	}

	public void onFailed(int statusCode, String response) {
		if (callback != null) {
			callback.onFailed(reqkey, statusCode, response);
		}
		retry = true;
	}

	public void onSucceed(int statusCode, byte[] rawResult) throws Exception {
		if (responseConverter != null && callback != null) {
			callback.onSucceed(reqkey, statusCode,
					responseConverter.convert(rawResult));
		}
	}
}
