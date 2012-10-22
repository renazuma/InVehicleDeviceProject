package com.kogasoftware.odt.apiclient.test;

import com.kogasoftware.odt.apiclient.ApiClientCallback;
import com.kogasoftware.odt.apiclient.ApiClientException;

public class EmptyApiClientCallback<T> implements ApiClientCallback<T> {
	@Override
	public void onException(int reqkey, ApiClientException ex) {
	}

	@Override
	public void onFailed(int reqkey, int statusCode, String response) {
	}

	@Override
	public void onSucceed(int reqkey, int statusCode, T result) {
	}
};
