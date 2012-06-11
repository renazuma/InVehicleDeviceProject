package com.kogasoftware.odt.webapi.serializablehttprequestbasesupplier;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.http.client.methods.HttpRequestBase;

import android.net.Uri;
import android.util.Log;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.kogasoftware.odt.webapi.WebAPIException;

abstract public class SerializableHttpRequestBaseSupplier implements
		Serializable {
	private static final String TAG = SerializableHttpRequestBaseSupplier.class
			.getSimpleName();
	private static final long serialVersionUID = -7411973624771269698L;
	protected static final String AUTHENTICATION_TOKEN_KEY = "authentication_token";

	protected final String host;
	protected final String path;
	protected final String extension;
	protected final TreeMap<String, String> params; // Serializableを明示するため具体的なクラスを指定
	protected final String authenticationToken;

	public SerializableHttpRequestBaseSupplier(String host, String path,
			Map<String, String> params, String authenticationToken, String extension) {
		this.host = host;
		this.path = path;
		this.params = new TreeMap<String, String>(Objects.firstNonNull(params,
				new TreeMap<String, String>()));
		this.authenticationToken = Strings.nullToEmpty(authenticationToken);
		this.extension = extension;
	}
	
	public SerializableHttpRequestBaseSupplier(String host, String path,
			Map<String, String> params, String authenticationToken) {
		this(host, path, params, authenticationToken, ".json");
	}

	protected void build(HttpRequestBase request) throws WebAPIException {
		registerAuthenticationToken();

		Uri.Builder uriBuilder = Uri.parse(host).buildUpon();
		uriBuilder.path(path + extension);

		for (Entry<String, String> entry : (new TreeMap<String, String>(params))
				.entrySet()) {
			uriBuilder.appendQueryParameter(entry.getKey(), entry.getValue());
		}

		String uri = uriBuilder.toString();
		Log.d(TAG, uri);

		try {
			request.setURI(new URI(uri));
		} catch (URISyntaxException e) {
			throw new WebAPIException(e);
		}
	}

	abstract public HttpRequestBase get() throws WebAPIException;

	protected void registerAuthenticationToken() throws WebAPIException {
		if (authenticationToken.length() > 0) {
			params.put(AUTHENTICATION_TOKEN_KEY, authenticationToken);
		}
	}
}
