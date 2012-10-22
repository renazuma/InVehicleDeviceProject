package com.kogasoftware.odt.apiclient.serializablerequestloader;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONObject;

import com.kogasoftware.odt.apiclient.ApiClientException;

public class SerializablePutLoader extends SerializableEntityEnclosingRequestLoader {
	private static final long serialVersionUID = -5259397660430434317L;

	public SerializablePutLoader(String host, String path,
			JSONObject entityJSON, String authenticationToken) {
		super(host, path, entityJSON, authenticationToken);
	}

	@Override
	public HttpRequestBase load() throws ApiClientException {
		HttpPut request = new HttpPut();
		build(request);
		return request;
	}
}
