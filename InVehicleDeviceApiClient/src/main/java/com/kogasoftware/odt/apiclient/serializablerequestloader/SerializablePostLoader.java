package com.kogasoftware.odt.apiclient.serializablerequestloader;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;

import com.fasterxml.jackson.databind.JsonNode;
import com.kogasoftware.odt.apiclient.ApiClientException;

public class SerializablePostLoader extends
		SerializableEntityEnclosingRequestLoader {
	private static final long serialVersionUID = -525937660430435317L;

	public SerializablePostLoader(String host, String path, JsonNode param,
			String authenticationToken) {
		super(host, path, param, authenticationToken);
	}

	@Override
	public HttpRequestBase load() throws ApiClientException {
		HttpPost request = new HttpPost();
		build(request);
		return request;
	}
}