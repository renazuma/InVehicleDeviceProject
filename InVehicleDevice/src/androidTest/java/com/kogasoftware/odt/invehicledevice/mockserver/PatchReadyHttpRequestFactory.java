package com.kogasoftware.odt.invehicledevice.mockserver;

import org.apache.http.HttpRequest;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.RequestLine;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;

public class PatchReadyHttpRequestFactory extends DefaultHttpRequestFactory {
	@Override
	public HttpRequest newHttpRequest(final RequestLine requestline)
			throws MethodNotSupportedException {
		if (requestline.getMethod().equals("PATCH")) {
			return new BasicHttpEntityEnclosingRequest(requestline);
		} else {
			return super.newHttpRequest(requestline);
		}
	}

	@Override
	public HttpRequest newHttpRequest(final String method, final String uri)
			throws MethodNotSupportedException {
		if (method.equals("PATCH")) {
			return new BasicHttpEntityEnclosingRequest(method, uri);
		} else {
			return super.newHttpRequest(method, uri);
		}
	}
}
