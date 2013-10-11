package com.kogasoftware.odt.apiclient.serializablerequestloader;

import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import com.kogasoftware.odt.apiclient.ApiClientException;

public class SerializableGetLoader extends SerializableRequestLoader {
	private static final long serialVersionUID = -3731243816173414925L;

	public SerializableGetLoader(String host, String path,
			Map<String, String> params, String authenticationToken) {
		super(host, path, params, authenticationToken);
	}

	public SerializableGetLoader(String host, String path,
			Map<String, String> params, String authenticationToken,
			String extension) {
		super(host, path, params, authenticationToken, extension);
	}

	@Override
	public HttpRequestBase load() throws ApiClientException {
		HttpGet request = new HttpGet();
		build(request);
		return request;
	}
}
