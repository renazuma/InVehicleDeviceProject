package com.kogasoftware.odt.webapi;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.http.client.methods.HttpRequestBase;

import com.kogasoftware.odt.webapi.WebAPI.EmptyWebAPICallback;
import com.kogasoftware.odt.webapi.WebAPI.ResponseConverter;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;

class WebAPIRequest implements Serializable {
	private static final long serialVersionUID = -8451453777378477195L;
	protected static final AtomicInteger reqkeyCounter = new AtomicInteger(0);
	protected final HttpRequestBase firstRequest;
	protected final HttpRequestBase retryRequest;
	protected final int reqkey = reqkeyCounter.incrementAndGet();
	protected boolean retry = false;
	protected boolean succeed = false;

	transient protected WebAPICallback<ByteBuffer> callback = new EmptyWebAPICallback<ByteBuffer>();

	public boolean isSucceed() {
		return succeed;
	}

	public HttpRequestBase getRequest() {
		return retry ? retryRequest : firstRequest;
	}

	protected void readObject(ObjectInputStream stream) throws IOException,
			ClassNotFoundException {
		try {
			stream.defaultReadObject();
		} finally {
			callback = new EmptyWebAPICallback<ByteBuffer>();
		}
	}

	public void onSucceed(int statusCode, byte[] rawResult) {
		callback.onSucceed(reqkey, statusCode, ByteBuffer.wrap(rawResult));
	}

	public void onFailed(int statusCode, String response) {
		succeed = false;
		retry = true;
		callback.onFailed(reqkey, statusCode, response);
	}

	public void onException(WebAPIException e) {
		succeed = false;
		retry = true;
		callback.onException(reqkey, e);
	}

	public <T> WebAPIRequest(HttpRequestBase firstRequest,
			HttpRequestBase retryRequest,
			final WebAPICallback<T> defaultCallback,
			final ResponseConverter<T> responseConverter) {
		this.firstRequest = firstRequest;
		this.retryRequest = retryRequest;
		this.callback = new WebAPICallback<ByteBuffer>() {
			@Override
			public void onSucceed(int reqkey, int statusCode,
					ByteBuffer rawResult) {
				try {
					defaultCallback.onSucceed(reqkey, statusCode,
							responseConverter.convert(rawResult.array()));
				} catch (Exception e) {
					WebAPIRequest.this
							.onException(new WebAPIException(false, e));
				}
			}

			@Override
			public void onFailed(int reqkey, int statusCode, String response) {
				defaultCallback.onFailed(reqkey, statusCode, response);
			}

			@Override
			public void onException(int reqkey, WebAPIException ex) {
				defaultCallback.onException(reqkey, ex);
			}
		};
	}

	public int getReqKey() {
		return reqkey;
	}
}
