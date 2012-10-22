package com.kogasoftware.odt.apiclient;

public interface ApiClientCallback<T> {
	/**
	 * 例外発生時のコールバック
	 * 
	 * @param reqkey
	 *            リクエスト時の reqkey
	 * @param ex
	 *            例外オブジェクト
	 */
	void onException(int reqkey, ApiClientException ex);

	/**
	 * リクエスト失敗時のコールバック
	 * 
	 * @param reqkey
	 *            リクエスト時の reqkey
	 * @param statusCode
	 *            HTTPステータス
	 */
	void onFailed(int reqkey, int statusCode, String response);

	/**
	 * リクエスト成功時のコールバック
	 * 
	 * @param reqkey
	 *            リクエスト時の reqkey
	 * @param statusCode
	 *            HTTPステータス
	 * @param result
	 *            結果のオブジェクト
	 */
	void onSucceed(int reqkey, int statusCode, T result);
}
