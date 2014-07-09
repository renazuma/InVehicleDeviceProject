package com.kogasoftware.odt.invehicledevice.mockserver;

import org.apache.http.HttpRequestFactory;
import org.apache.http.impl.DefaultHttpServerConnection;

public class PatchReadyHttpServerConnection extends DefaultHttpServerConnection {
	@Override
	protected HttpRequestFactory createHttpRequestFactory() {
		return new PatchReadyHttpRequestFactory();
	}
}
