package com.kogasoftware.odt.webapi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.client.methods.HttpRequestBase;

import android.util.Log;

import com.kogasoftware.odt.webapi.WebAPI.ResponseConverter;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;

class WebAPIRequest<T> implements Serializable {
	private static final long serialVersionUID = -8451453777378477195L;
	private static final String TAG = WebAPIRequest.class.getSimpleName();
	protected static final AtomicInteger reqkeyCounter = new AtomicInteger(0);
	protected final HttpRequestBase firstRequest;
	protected final HttpRequestBase retryRequest;
	protected final int reqkey = reqkeyCounter.incrementAndGet();
	protected boolean retry = false;
	protected boolean callbackAndResponseConverterSerialized = false;

	transient protected WebAPICallback<T> callback;
	transient protected ResponseConverter<T> responseConverter;

	public WebAPIRequest(HttpRequestBase firstRequest,
			HttpRequestBase retryRequest, WebAPICallback<T> callback,
			ResponseConverter<T> responseConverter) {
		this.firstRequest = firstRequest;
		this.retryRequest = retryRequest;
		this.callback = callback;
		this.responseConverter = responseConverter;
	}

	public int getReqKey() {
		return reqkey;
	}

	public HttpRequestBase getRequest() {
		return retry ? retryRequest : firstRequest;
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

	protected void readObject(ObjectInputStream stream) throws IOException,
			ClassNotFoundException {

		stream.defaultReadObject();
		if (!callbackAndResponseConverterSerialized) {
			return;
		}
		try {
			Object callbackObject = stream.readObject();
			if (!(callbackObject instanceof WebAPICallback<?>)) {
				Log.w(TAG, "!(callbackObject instanceof WebAPICallback<?>)");
				return;
			}
			callback = (WebAPICallback<T>) callbackObject; // TODO:warning
			Object responseConverterObject = stream.readObject();
			if (!(responseConverterObject instanceof ResponseConverter<?>)) {
				Log.w(TAG,
						"!(responseConverterObject instanceof ResponseConverter<?>)");
				return;
			}
			responseConverter = (ResponseConverter<T>) responseConverterObject; // TODO:warning
		} catch (ClassNotFoundException e) {
			Log.w(TAG, e);
			throw e;
		} catch (IOException e) {
			Log.w(TAG, e);
			throw e;
		}
	}

	protected void writeObject(ObjectOutputStream stream) throws IOException {
		stream.defaultWriteObject();
		if (!(callback instanceof Serializable)) {
			return;
		}
		if (!(responseConverter instanceof Serializable)) {
			return;
		}

		try {
			stream.writeObject(callback);
			stream.writeObject(responseConverter);
		} catch (IOException e) {
			Log.w(TAG, e);
			throw e;
		}
		callbackAndResponseConverterSerialized = true;
	}
}
