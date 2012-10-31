package com.kogasoftware.odt.apiclient;

import java.nio.ByteBuffer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Charsets;

import android.util.Log;

public class ApiClients {
	private static final String TAG = ApiClients.class.getSimpleName();

	public static JSONArray parseJSONArray(byte[] rawResponse)
			throws JSONException {
		String json = ApiClients.decodeByteArray(rawResponse);
		Log.d(TAG + "#parseJSONArray", json);
		return new JSONArray(json);
	}

	public static String decodeByteArray(byte[] byteArray) {
		return Charsets.ISO_8859_1.decode(ByteBuffer.wrap(byteArray))
				.toString();
	}
}
