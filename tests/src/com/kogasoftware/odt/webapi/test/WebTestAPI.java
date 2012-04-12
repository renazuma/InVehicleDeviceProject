package com.kogasoftware.odt.webapi.test;

import com.kogasoftware.odt.webapi.WebAPI;
import com.kogasoftware.odt.webapi.WebAPIException;

public class WebTestAPI extends WebAPI {

	protected static final String TEST_SERVER_HOST = "http://192.168.104.63:3333";
	protected static final String PATH_CLEAN = "/clean";

	@Override
	protected String getServerHost() {
		return TEST_SERVER_HOST;
	}

	/**
	 * DatabaseCleaner を呼び出してDBを全クリアする
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int cleanDatabase(WebAPICallback<Void> callback) throws WebAPIException {
		return post(PATH_CLEAN, null, callback, new ResponseConverter<Void>() {
			@Override
			public Void convert(byte[] rawResponse)
					throws Exception {
				return null;
			}
		});
	}	
	
}
