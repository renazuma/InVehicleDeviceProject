package com.kogasoftware.odt.webapi.test;

import java.util.List;

import org.json.JSONObject;

import com.kogasoftware.odt.webapi.WebAPI;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.WebAPI.ResponseConverter;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class WebTestAPI extends WebAPI {

	protected static final String TEST_SERVER_HOST = "http://192.168.104.63:3333";
	protected static final String PATH_CLEAN = "/clean";

	@Override
	protected String getServerHost() {
		return TEST_SERVER_HOST;
	}

	public void cleanDatabase(WebAPICallback<Void> callback) throws WebAPIException {
		post(PATH_CLEAN, null, callback, new ResponseConverter<Void>() {
			@Override
			public Void convert(byte[] rawResponse)
					throws Exception {
				return null;
			}
		});
	}

}
