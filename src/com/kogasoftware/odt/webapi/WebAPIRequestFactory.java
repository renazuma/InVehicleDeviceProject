package com.kogasoftware.odt.webapi;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.util.Log;

import com.kogasoftware.odt.webapi.WebAPI.ResponseConverter;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;

public class WebAPIRequestFactory {
	private static final String TAG = WebAPIRequestFactory.class
			.getSimpleName();

	private static Uri.Builder buildUri(String host, String path) {
		Uri.Builder uriBuilder = Uri.parse(host).buildUpon();
		uriBuilder.path(path + ".json");
		return uriBuilder;
	}

	public static <T> WebAPIRequest<T> newInstance(String host, String path,
			JSONObject entityJSON, HttpEntityEnclosingRequestBase request,
			WebAPICallback<T> callback, ResponseConverter<T> responseConverter,
			String authenticationToken) throws WebAPIException {

		String uri = buildUri(host, path).toString();
		try {
			if (entityJSON == null) {
				entityJSON = new JSONObject();
			}

			if (authenticationToken.length() > 0) {
				entityJSON.put("authentication_token", authenticationToken);
			}
			request.setURI(new URI(uri));
			String entityString = entityJSON.toString();
			StringEntity entity = new StringEntity(entityString, "UTF-8");
			entity.setContentType("application/json");
			request.setEntity(entity);
		} catch (JSONException e) {
			throw new WebAPIException(false, e);
		} catch (UnsupportedEncodingException e) {
			throw new WebAPIException(false, e);
		} catch (URISyntaxException e) {
			throw new WebAPIException(false, e);
		}
		return new WebAPIRequest<T>(request, request, callback,
				responseConverter);
	}

	public static <T> WebAPIRequest<T> newInstance(String host, String path,
			Map<String, String> params, HttpRequestBase request,
			WebAPICallback<T> callback, ResponseConverter<T> responseConverter,
			String authenticationToken) throws WebAPIException {

		Uri.Builder uriBuilder = buildUri(host, path);

		if (authenticationToken != null && authenticationToken.length() > 0) {
			uriBuilder.appendQueryParameter("authentication_token",
					authenticationToken);
		}
		if (params != null) {
			for (Entry<String, String> entry : (new TreeMap<String, String>(
					params)).entrySet()) {
				uriBuilder.appendQueryParameter(entry.getKey(),
						entry.getValue());
			}
		}
		String uri = uriBuilder.toString();
		Log.d(TAG, uri);

		try {
			request.setURI(new URI(uri));
		} catch (URISyntaxException e) {
			throw new WebAPIException(false, e);
		}

		return new WebAPIRequest<T>(request, request, callback,
				responseConverter);
	}

}
