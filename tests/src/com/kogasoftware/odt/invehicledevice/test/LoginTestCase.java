package com.kogasoftware.odt.invehicledevice.test;

import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.kogasoftware.odt.invehicledevice.OperatorApi;
import com.kogasoftware.odt.invehicledevice.OperatorApi.BadStatusCodeException;

public class LoginTestCase extends TestCase {
	private final String TAG = LoginTestCase.class.getSimpleName();

	public void testAuthenticationTokenユーザー名失敗() throws InterruptedException {
		final AtomicBoolean r = new AtomicBoolean(true);
		OperatorApi api = new OperatorApi();
		api.getAuthenticationToken("i_mogi", "8942549",
				new OperatorApi.AsyncCallback<String>() {
					@Override
					public void onError(Exception exception) {
						if (exception instanceof BadStatusCodeException) {
							r.set(false);
						}
					}
				});
		api.join();
		assertFalse(r.get());
	}

	public void testAuthenticationTokenパスワード失敗() throws InterruptedException {
		final AtomicBoolean r = new AtomicBoolean(true);
		OperatorApi api = new OperatorApi();

		api.getAuthenticationToken("i_mogi", "89423952845",
				new OperatorApi.AsyncCallback<String>() {
					@Override
					public void onError(Exception exception) {
						if (exception instanceof BadStatusCodeException) {
							r.set(false);
						}
					}
				});
		api.join();
		assertFalse(r.get());
	}

	public void testAuthenticationToken成功() throws InterruptedException {
		final AtomicBoolean r = new AtomicBoolean(false);
		OperatorApi api = new OperatorApi();
		final StringBuilder token = new StringBuilder();

		api.getAuthenticationToken("i_mogi", "i_mogi",
				new OperatorApi.AsyncCallback<String>() {
					@Override
					public void onError(Exception exception) {
						Log.e(TAG, "", exception);
						exception.printStackTrace();
					}

					@Override
					public void onSuccess(String asyncToken) {
						r.set(true);
						token.append(asyncToken);
					}
				});
		api.join();
		assertTrue(r.get());
		assertFalse(token.toString().isEmpty());
	}

	private String getValidToken() throws InterruptedException {
		final AtomicBoolean r = new AtomicBoolean(false);
		OperatorApi api = new OperatorApi();
		final StringBuilder token = new StringBuilder();

		api.getAuthenticationToken("i_mogi", "i_mogi",
				new OperatorApi.AsyncCallback<String>() {
					@Override
					public void onError(Exception exception) {
						Log.e(TAG, "", exception);
						exception.printStackTrace();
					}

					@Override
					public void onSuccess(String asyncToken) {
						r.set(true);
						token.append(asyncToken);
					}
				});
		api.join();
		assertFalse(token.toString().isEmpty());
		return token.toString();
	}

	public void testトークンを使って車載機情報を取得に成功() throws InterruptedException {
		String token = getValidToken();
		final AtomicBoolean r = new AtomicBoolean(false);
		OperatorApi api = new OperatorApi(token);
		final StringBuilder modelName = new StringBuilder();
		api.getInVehicleDevice(1, new OperatorApi.AsyncCallback<JSONObject>() {
			@Override
			public void onSuccess(JSONObject inVehicleDevice) {
				try {
					modelName.append(inVehicleDevice.get("model_name"));
					r.set(true);
				} catch (JSONException e) {
				}
			}
		});
		api.join();
		assertTrue(r.get());
		assertEquals("車載機アプリ開発用車載機", modelName.toString());
	}

	public void test無効なトークンを使って車載機情報を取得に失敗() throws InterruptedException {
		{
			String token = getValidToken().substring(1) + "x";
			OperatorApi api = new OperatorApi(token);
			final AtomicBoolean r = new AtomicBoolean(false);
			api.getInVehicleDevice(1,
					new OperatorApi.AsyncCallback<JSONObject>() {
						@Override
						public void onError(Exception e) {
							r.set(true);
						}
					});
			api.join();
			assertTrue(r.get());
		}
		{
			String token = getValidToken() + "x";
			OperatorApi api = new OperatorApi(token);
			final AtomicBoolean r = new AtomicBoolean(false);
			api.getInVehicleDevice(1,
					new OperatorApi.AsyncCallback<JSONObject>() {
						@Override
						public void onError(Exception e) {
							r.set(true);
						}
					});
			api.join();
			assertTrue(r.get());
		}
	}

	public void testトークンを使ってわず車載機情報を取得に失敗() throws InterruptedException {
		OperatorApi api = new OperatorApi("");
		final AtomicBoolean r = new AtomicBoolean(false);
		api.getInVehicleDevice(1, new OperatorApi.AsyncCallback<JSONObject>() {
			@Override
			public void onError(Exception e) {
				r.set(true);
			}
		});
		api.join();
		assertTrue(r.get());
	}

}
