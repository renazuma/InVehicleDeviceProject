package com.kogasoftware.odt.invehicledevice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.google.common.base.Charsets;

public class AsyncPostJSONTask extends AsyncTask<Void, Integer, JSONObject> {
	private final String T = LogTag.get(AsyncPostJSONTask.class);

	public interface OnJSONResultListener {
		public void onJSONResult(JSONObject result);
	}

	final private String postUrl;
	final private String postData;
	private OnJSONResultListener listener = new OnJSONResultListener() {
		@Override
		public void onJSONResult(JSONObject result) {
		}
	};

	public AsyncPostJSONTask(String url, String postdata) {
		this.postUrl = url;
		this.postData = postdata;
	}

	public void setOnJSONResultListener(OnJSONResultListener listener) {
		this.listener = listener;
	}

	@Override
	protected JSONObject doInBackground(Void... params) {
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost();
			URI uri = new URI(postUrl);
			request.setURI(uri);
			request.setHeader("Content-type", "application/json");

			StringEntity entity = new StringEntity(postData,
					Charsets.UTF_8.toString());

			request.setEntity(entity);

			HttpResponse response = httpClient.execute(request);

			int res = response.getStatusLine().getStatusCode();
			Log.d(T, "Status:" + res);

			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			String l;
			while ((l = br.readLine()) != null) {
				sb.append(l).append("\n");
			}

			JSONObject jsonRes;
			if (res >= 200 && res < 300) {
				Log.d(T, "response JSON: " + sb.toString());
				jsonRes = new JSONObject(sb.toString());
			} else {
				jsonRes = new JSONObject();
				jsonRes.put("code", res);
				jsonRes.put("body", sb.toString());
			}

			return jsonRes;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		super.onPostExecute(result);

		if (listener != null) {
			listener.onJSONResult(result);
		}
	}
}
