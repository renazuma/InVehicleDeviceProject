package com.kogasoftware.odt.apiclient.serializablerequestloader;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Objects;
import com.kogasoftware.odt.apiclient.ApiClientException;

public abstract class SerializableEntityEnclosingRequestLoader extends
		SerializableRequestLoader {
	private static final long serialVersionUID = -3807558102171626080L;
	protected String entityString = "";

	public SerializableEntityEnclosingRequestLoader(String host, String path,
			JSONObject entityJSON, String authenticationToken) {
		super(host, path, null, authenticationToken);
		this.entityString = Objects.firstNonNull(entityJSON, new JSONObject())
				.toString();
	}

	protected void build(HttpEntityEnclosingRequestBase request)
			throws ApiClientException {
		super.build(request);
		try {
			StringEntity entity = new StringEntity(entityString, "UTF-8");
			entity.setContentType("application/json");
			request.setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			throw new ApiClientException(e);
		}
	}

	@Override
	protected void registerAuthenticationToken() throws ApiClientException {
		if (authenticationToken.length() > 0) {
			try {
				JSONObject entityJSON = new JSONObject(entityString);
				entityJSON.put(AUTHENTICATION_TOKEN_KEY, authenticationToken);
				entityString = entityJSON.toString();
			} catch (JSONException e) {
				throw new ApiClientException(e);
			}
		}
	}
}
