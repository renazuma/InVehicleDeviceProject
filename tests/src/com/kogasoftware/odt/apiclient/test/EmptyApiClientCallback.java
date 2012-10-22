package com.kogasoftware.odt.webapi.test;

import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;

public class EmptyWebAPICallback<T> implements WebAPICallback<T> {
	@Override
	public void onException(int reqkey, WebAPIException ex) {
	}

	@Override
	public void onFailed(int reqkey, int statusCode, String response) {
	}

	@Override
	public void onSucceed(int reqkey, int statusCode, T result) {
	}
};
