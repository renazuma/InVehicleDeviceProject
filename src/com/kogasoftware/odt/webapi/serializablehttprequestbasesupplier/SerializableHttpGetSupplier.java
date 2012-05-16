package com.kogasoftware.odt.webapi.serializablehttprequestbasesupplier;

import java.util.TreeMap;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import com.kogasoftware.odt.webapi.WebAPIException;

public class SerializableHttpGetSupplier extends
		SerializableHttpRequestBaseSupplier {
	private static final long serialVersionUID = -3731243816173414926L;

	public SerializableHttpGetSupplier(String host, String path,
			TreeMap<String, String> params, String authenticationToken) {
		super(host, path, params, authenticationToken);
	}

	@Override
	public HttpRequestBase get() throws WebAPIException {
		HttpGet request = new HttpGet();
		build(request);
		return request;
	}
}
