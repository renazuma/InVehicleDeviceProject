package com.kogasoftware.odt.apiclient.serializablerequestloader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.StringEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Objects;
import com.kogasoftware.odt.apiclient.ApiClientException;
import com.kogasoftware.odt.invehicledevice.apiclient.model.base.Model;

public abstract class SerializableEntityEnclosingRequestLoader extends
		SerializableRequestLoader {
	private static final long serialVersionUID = -3807558102171626080L;
	protected String entityString = "";

	public SerializableEntityEnclosingRequestLoader(String host, String path,
			JsonNode param, String authenticationToken) {
		super(host, path, new TreeMap<String, String>(), authenticationToken);
		Iterator<JsonNode> iterator = param.iterator();
		while (iterator.hasNext()) {
			JsonNode jsonNode = iterator.next();
			if (jsonNode instanceof ObjectNode) {
				((ObjectNode) jsonNode)
						.remove(Model.JACKSON_IDENTITY_INFO_PROPERTY);
			}
		}
		this.entityString = param.toString();
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
				ObjectNode entity = Model.getObjectMapper().readValue(
						entityString, ObjectNode.class);
				entity.put(AUTHENTICATION_TOKEN_KEY, authenticationToken);
				entityString = entity.toString();
			} catch (IOException e) {
				throw new ApiClientException(e);
			}
		}
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("super", super.toString())
				.add("entityString", entityString).toString();
	}
}
