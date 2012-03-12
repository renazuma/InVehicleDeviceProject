package com.kogasoftware.odt.webapi;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.util.Log;

import com.google.common.io.ByteStreams;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.Operator;
import com.kogasoftware.odt.webapi.model.User;

public class WebAPI {

	private final String authenticationToken;
	private final String TAG = WebAPI.class.getSimpleName();
	private final String BASE_URI = "http://10.1.10.161";

	public WebAPI() {
		this("");
	}

	public WebAPI(String authenticationToken) {
		this.authenticationToken = authenticationToken;
	}

	public class InVehicleDevices {
		public InVehicleDevice create(final InVehicleDevice inVehicleDevice) {

			Callable<InVehicleDevice> callable = new Callable<InVehicleDevice>() {
				@Override
				public InVehicleDevice call() throws Exception {
					// リクエストデータを作る（処理毎に固有）
					JSONObject postJSON = new JSONObject();
					JSONObject inVehicleDeviceJSON = inVehicleDevice
							.toJSONObject();
					postJSON.put(InVehicleDevice.JSON_NAME, inVehicleDeviceJSON);
					if (authenticationToken.length() > 0) {
						postJSON.put("authentication_token",
								authenticationToken);
					}

					// リクエストデータを作る（メソッド毎に固有）
					HttpPost httpPost = new HttpPost();
					httpPost.setURI(new URI(BASE_URI + InVehicleDevice.URL.ROOT));
					StringEntity entity = new StringEntity(postJSON.toString(),
							"UTF-8");
					entity.setContentType("application/json");
					httpPost.setEntity(entity);
					HttpRequestBase requestBase = httpPost;

					// リクエストを送る（共通）
					HttpClient httpClient = new DefaultHttpClient();
					HttpResponse httpResponse = httpClient.execute(requestBase);
					Integer statusCode = httpResponse.getStatusLine()
							.getStatusCode();
					String responseString = new String(
							ByteStreams.toByteArray(httpResponse.getEntity()
									.getContent()));

					// レスポンスを解析する（処理毎に固有）
					JSONObject responseJSON = new JSONObject(responseString);
					Log.i(TAG, responseJSON.toString());
					return new InVehicleDevice(responseJSON);
				}
			};

			try {
				return callable.call();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	public InVehicleDevices inVehicleDevices = new InVehicleDevices();

	public class Users {
		public List<User> index(final Map<String, String> params) {
			Callable<List<User>> callable = new Callable<List<User>>() {
				@Override
				public List<User> call() throws Exception {
					return new LinkedList<User>();
				}
			};

			try {
				return callable.call();
			} catch (Exception e) {
				e.printStackTrace();
				return new LinkedList<User>();
			}
		}

		public User show(final Integer id) {
			return new User();
		}

		public User create(final User user, final String password) {

			Callable<User> callable = new Callable<User>() {
				@Override
				public User call() throws Exception {
					// リクエストデータを作る（処理毎に固有）
					JSONObject postJSON = new JSONObject();
					JSONObject userJSON = user.toJSONObject();
					userJSON.put("password", password);
					postJSON.put(User.JSON_NAME, userJSON);
					if (authenticationToken.length() > 0) {
						postJSON.put("authentication_token",
								authenticationToken);
					}

					// リクエストデータを作る（メソッド毎に固有）
					HttpPost httpPost = new HttpPost();
					httpPost.setURI(new URI(BASE_URI + User.URL.ROOT));
					StringEntity entity = new StringEntity(postJSON.toString(),
							"UTF-8");
					entity.setContentType("application/json");
					httpPost.setEntity(entity);
					HttpRequestBase requestBase = httpPost;

					// リクエストを送る（共通）
					HttpClient httpClient = new DefaultHttpClient();
					HttpResponse httpResponse = httpClient.execute(requestBase);
					Integer statusCode = httpResponse.getStatusLine()
							.getStatusCode();
					String responseString = new String(
							ByteStreams.toByteArray(httpResponse.getEntity()
									.getContent()));

					// レスポンスを解析する（処理毎に固有）
					JSONObject responseJSON = new JSONObject(responseString);
					Log.i(TAG, responseJSON.toString());
					return new User(responseJSON);
				}
			};

			try {
				return callable.call();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		public User update(final User user) {
			return new User();
		}

		public void destroy(final Integer id) {
		}
	}

	public Users users = new Users();

	public class Operators {
		public String signIn(final String login, final String password) {

			Callable<String> callable = new Callable<String>() {
				@Override
				public String call() throws Exception {
					// リクエストデータを作る（処理毎に固有）
					JSONObject postJSON = new JSONObject();
					JSONObject operator = new JSONObject();
					operator.put("login", login);
					operator.put("password", password);
					postJSON.put("operator", operator);

					// リクエストデータを作る（メソッド毎に固有）
					HttpPost httpPost = new HttpPost();
					httpPost.setURI(new URI(BASE_URI + "/"
							+ Operator.CONTROLLER_NAME + "/sign_in.json"));
					StringEntity entity = new StringEntity(postJSON.toString(),
							"UTF-8");
					entity.setContentType("application/json");
					httpPost.setEntity(entity);
					HttpRequestBase requestBase = httpPost;

					// リクエストを送る（共通）
					HttpClient httpClient = new DefaultHttpClient();
					HttpResponse httpResponse = httpClient.execute(requestBase);
					Integer statusCode = httpResponse.getStatusLine()
							.getStatusCode();
					String responseString = new String(
							ByteStreams.toByteArray(httpResponse.getEntity()
									.getContent()));

					// レスポンスを解析する（処理毎に固有）
					JSONObject responseJSON = new JSONObject(responseString);
					Log.i(TAG, responseJSON.toString());
					return responseJSON.getString("authentication_token");
				}
			};

			try {
				return callable.call();
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
		}

	}

	public Operators operators = new Operators();

	// static class EmptyThread extends Thread {
	//
	// }
	//
	// abstract class JSONRequest<ResultType> {
	//
	// abstract public HttpRequestBase createHttpRequest() throws Exception;
	//
	// abstract public ResultType parseResponse(JSONObject response)
	// throws Exception;
	// }
	//
	// abstract class JSONGETRequest<ResultType> extends JSONRequest<ResultType>
	// {
	// final protected String uri;
	//
	// public JSONGETRequest(String uri) {
	// this.uri = uri;
	// }
	//
	// @Override
	// public HttpRequestBase createHttpRequest() throws Exception {
	// HttpGet request = new HttpGet();
	// String fullUri = baseUri + "/" + uri;
	// if (/* !authenticationToken.isEmpty() */authenticationToken
	// .length() != 0) {
	// fullUri += "?authentication_token=" + authenticationToken;
	// }
	// request.setURI(new URI(fullUri));
	// return request;
	// }
	// }
	//
	// abstract class JSONPOSTRequest<ResultType> extends
	// JSONRequest<ResultType> {
	// protected final String controller;
	// protected final String action;
	//
	// public JSONPOSTRequest(String controller, String action) {
	// this.controller = controller;
	// this.action = action;
	// }
	//
	// abstract protected JSONObject createRequest() throws Exception;
	//
	// @Override
	// public HttpRequestBase createHttpRequest() throws Exception {
	// HttpPost request = new HttpPost();
	// JSONObject requestJSON = createRequest();
	// if (/* !authenticationToken.isEmpty() */authenticationToken
	// .length() != 0) {
	// requestJSON.put("authentication_token", authenticationToken);
	// }
	// request.setURI(new URI(baseUri + "/" + controller + "/" + action
	// + ".json"));
	// StringEntity entity = new StringEntity(requestJSON.toString(),
	// "UTF-8");
	//
	// request.setEntity(entity);
	// return request;
	// }
	// }
	//
	// public static class BadStatusCodeException extends RuntimeException {
	// private static final long serialVersionUID = -375497023051947223L;
	// public final Integer statusCode;
	// public final String body;
	//
	// public BadStatusCodeException(Integer statusCode, String body) {
	// this.statusCode = statusCode;
	// this.body = body;
	// }
	// }
	//
	// public static class AsyncCallback<ResultType> {
	// public void onError(Exception exception) {
	// }
	//
	// public void onSuccess(ResultType result) {
	// }
	// }
	//
	// private final Object threadLock = new Object();
	// private Thread thread = new EmptyThread();
	//
	// public WebAPI() {
	// this("");
	// }
	//
	// public WebAPI(String authenticationToken) {
	// this.authenticationToken = authenticationToken;
	// }
	//
	// public void join() throws InterruptedException {
	// synchronized (threadLock) {
	// thread.join();
	// }
	// }
	//
	// public void getAuthenticationTokenXX(final String login,
	// final String password, final AsyncCallback<String> callback) {
	//
	// startJSONRequest(new JSONPOSTRequest<String>("operators", "sign_in") {
	// @Override
	// protected JSONObject createRequest() throws Exception {
	// JSONObject postJSON = new JSONObject();
	// JSONObject operator = new JSONObject();
	// operator.put("login", login);
	// operator.put("password", password);
	// postJSON.put("operator", operator);
	// return postJSON;
	// }
	//
	// @Override
	// public String parseResponse(JSONObject response) throws Exception {
	// return response.getString("authentication_token");
	// }
	// }, callback);
	// }
	//
	// public void getInVehicleDevice(Integer id,
	// AsyncCallback<JSONObject> callback) {
	// startJSONRequest(new JSONGETRequest<JSONObject>("in_vehicle_devices/"
	// + id + ".json") {
	// @Override
	// public JSONObject parseResponse(JSONObject response)
	// throws Exception {
	// return response;
	// }
	// }, callback);
	// }
	//
	// private <ResultType> void startJSONRequest(
	// final JSONRequest<ResultType> jsonRequest,
	// final AsyncCallback<ResultType> callback) {
	// synchronized (threadLock) {
	// thread.interrupt();
	// thread = new Thread() {
	// @Override
	// public void run() {
	// try {
	// HttpRequestBase request = jsonRequest
	// .createHttpRequest();
	// request.setHeader("Content-type", "application/json");
	// HttpClient httpClient = new DefaultHttpClient();
	// HttpResponse httpResponse = httpClient.execute(request);
	// int statusCode = httpResponse.getStatusLine()
	// .getStatusCode();
	// Log.d(TAG, "Status:" + statusCode);
	// String responseString = new String(
	// ByteStreams.toByteArray(httpResponse
	// .getEntity().getContent()) /*
	// * ,
	// * Charsets
	// * .UTF_8
	// */);
	// JSONObject response = new JSONObject(responseString);
	// if (statusCode / 100 != 2) {
	// throw new BadStatusCodeException(statusCode,
	// responseString);
	// }
	//
	// ResultType result = jsonRequest.parseResponse(response);
	// callback.onSuccess(result);
	// } catch (Exception e) {
	// callback.onError(e);
	// }
	// }
	// };
	// thread.start();
	// }
	// }
}
