package com.kogasoftware.odt.apiclient;

import java.nio.ByteBuffer;

import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

import com.google.common.base.Charsets;

public class ApiClients {
	private static final String TAG = ApiClients.class.getSimpleName();

	public static JSONArray parseJSONArray(byte[] rawResponse)
			throws JSONException {
		String json = ApiClients.decodeByteArray(rawResponse);
		Log.d(TAG + "#parseJSONArray", json);
		return new JSONArray(json);
	}

	public static String decodeByteArray(byte[] byteArray) {
		return Charsets.UTF_8.decode(ByteBuffer.wrap(byteArray))
				.toString();
	}
}
