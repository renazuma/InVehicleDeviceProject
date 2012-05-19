package com.kogasoftware.odt.webapi.serializablehttprequestbasesupplier;

import java.util.TreeMap;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpRequestBase;

import com.kogasoftware.odt.webapi.WebAPIException;

public class SerializableHttpDeleteSupplier extends
		SerializableHttpRequestBaseSupplier {
	private static final long serialVersionUID = -2445701098599698093L;

	public SerializableHttpDeleteSupplier(String serverHost, String path,
			String authenticationToken) {
		this(serverHost, path, new TreeMap<String, String>(),
				authenticationToken);
	}

	public SerializableHttpDeleteSupplier(String host, String path,
			TreeMap<String, String> params, String authenticationToken) {
		super(host, path, params, authenticationToken);
	}

	@Override
	public HttpRequestBase get() throws WebAPIException {
		HttpDelete request = new HttpDelete();
		build(request);
		return request;
	}
}
