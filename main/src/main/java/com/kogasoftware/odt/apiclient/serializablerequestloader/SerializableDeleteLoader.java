package com.kogasoftware.odt.apiclient.serializablerequestloader;

import java.util.Map;
import java.util.TreeMap;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpRequestBase;

import com.kogasoftware.odt.apiclient.ApiClientException;

public class SerializableDeleteLoader extends SerializableRequestLoader {
	private static final long serialVersionUID = -2445501098599698093L;

	public SerializableDeleteLoader(String serverHost, String path,
			String authenticationToken) {
		this(serverHost, path, new TreeMap<String, String>(),
				authenticationToken);
	}

	public SerializableDeleteLoader(String host, String path,
			Map<String, String> params, String authenticationToken) {
		super(host, path, params, authenticationToken);
	}

	@Override
	public HttpRequestBase load() throws ApiClientException {
		HttpDelete request = new HttpDelete();
		build(request);
		return request;
	}
}
