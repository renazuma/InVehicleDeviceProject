package com.kogasoftware.odt.apiclient;

import java.io.Closeable;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

public interface ApiClient extends Closeable {
	interface ResponseConverter<T> {
		T convert(byte[] rawResponse) throws Exception;
	}

	/**
	 * DELETEリクエスト
	 */
	<T> int delete(String path, ApiClientCallback<T> callback,
			ResponseConverter<? extends T> conv);

	/**
	 * GETリクエスト
	 */
	<T> int get(String path, Map<String, String> params, String requestGroup,
			ApiClientCallback<T> callback, ResponseConverter<? extends T> conv);

	/**
	 * POSTリクエスト
	 */
	<T> int post(String path, JsonNode param, JsonNode retryParam,
			String requestGroup, ApiClientCallback<T> callback,
			ResponseConverter<? extends T> conv);

	/**
	 * POSTリクエスト
	 */
	<T> int post(String path, JsonNode param, String requestGroup,
			ApiClientCallback<T> callback, ResponseConverter<? extends T> conv);

	/**
	 * PUTリクエスト
	 */
	<T> int put(String path, JsonNode param, String requestGroup,
			ApiClientCallback<T> callback, ResponseConverter<? extends T> conv);

	/**
	 * PUTリクエスト
	 */
	<T> int put(String path, JsonNode param, JsonNode retryParam,
			String requestGroup, ApiClientCallback<T> callback,
			ResponseConverter<? extends T> conv);

	/**
	 * リクエストを中断する
	 */
	void abort(int reqkey);

	/**
	 * 同じスレッドで次に実行するAPIの通信が終了していない場合、クローズ時にそれをファイルへ保存しコンストラクタで復活するようにする。
	 * ただし、復活後のリクエストは通信後のコールバックを行わない。
	 */
	ApiClient withSaveOnClose();

	ApiClient withSaveOnClose(boolean saveOnClose);

	/**
	 * 同じスレッドで次に実行するAPIの通信が、リトライするかを設定する。
	 */
	ApiClient withRetry(boolean retry);
}
