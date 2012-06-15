package com.kogasoftware.odt.webapi.serializablerequestloader;

import java.util.Map;
import java.util.TreeMap;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import com.kogasoftware.odt.webapi.WebAPIException;

public class SerializableGetLoader extends SerializableRequestLoader {
	private static final long serialVersionUID = -3731243816173414925L;

	public SerializableGetLoader(String host, String path,
			String authenticationToken) {
		this(host, path, new TreeMap<String, String>(), authenticationToken);
	}

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
	public HttpRequestBase load() throws WebAPIException {
		HttpGet request = new HttpGet();
		build(request);
		return request;
	}
}
