package com.kogasoftware.odt.invehicledevice.empty;

import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;

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
}
