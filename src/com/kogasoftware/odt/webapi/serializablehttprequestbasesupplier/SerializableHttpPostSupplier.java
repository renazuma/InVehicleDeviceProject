package com.kogasoftware.odt.webapi.serializablehttprequestbasesupplier;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONObject;

import com.kogasoftware.odt.webapi.WebAPIException;

public class SerializableHttpPostSupplier extends
		SerializableHttpEntityEnclosingRequestBase {
	private static final long serialVersionUID = -5259397660430435317L;

	public SerializableHttpPostSupplier(String host, String path,
			JSONObject entityJSON, String authenticationToken) {
		super(host, path, entityJSON, authenticationToken);
	}

	@Override
	public HttpRequestBase get() throws WebAPIException {
		HttpPost request = new HttpPost();
		build(request);
		return request;
	}
}
