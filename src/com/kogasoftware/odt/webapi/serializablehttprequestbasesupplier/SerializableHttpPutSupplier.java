package com.kogasoftware.odt.webapi.serializablehttprequestbasesupplier;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONObject;

import com.kogasoftware.odt.webapi.WebAPIException;

public class SerializableHttpPutSupplier extends
		SerializableHttpEntityEnclosingRequestBase {
	private static final long serialVersionUID = -5259397660430435317L;

	public SerializableHttpPutSupplier(String host, String path,
			JSONObject entityJSON, String authenticationToken) {
		super(host, path, entityJSON, authenticationToken);
	}

	@Override
	public HttpRequestBase get() throws WebAPIException {
		HttpPut request = new HttpPut();
		build(request);
		return request;
	}
}
