package com.kogasoftware.odt.webapi.serializablehttprequestbasesupplier;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Objects;
import com.kogasoftware.odt.webapi.WebAPIException;

abstract public class SerializableHttpEntityEnclosingRequestBase extends
		SerializableHttpRequestBaseSupplier {
	private static final long serialVersionUID = -3807598102171626080L;
	private static final String TAG = SerializableHttpEntityEnclosingRequestBase.class
			.getSimpleName();
	protected String entityString = "";

	public SerializableHttpEntityEnclosingRequestBase(String host, String path,
			JSONObject entityJSON, String authenticationToken) {
		super(host, path, null, authenticationToken);
		this.entityString = Objects.firstNonNull(entityJSON, new JSONObject())
				.toString();
	}

	protected void build(HttpEntityEnclosingRequestBase request)
			throws WebAPIException {
		super.build(request);
		try {
			StringEntity entity = new StringEntity(entityString, "UTF-8");
			entity.setContentType("application/json");
			request.setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			throw new WebAPIException(false, e);
		}
	}

	@Override
	protected void registerAuthenticationToken() throws WebAPIException {
		if (authenticationToken.length() > 0) {
			try {
				JSONObject entityJSON = new JSONObject(entityString);
				entityJSON.put(AUTHENTICATION_TOKEN_KEY, authenticationToken);
				entityString = entityJSON.toString();
			} catch (JSONException e) {
				throw new WebAPIException(false, e);
			}
		}
	}
}
