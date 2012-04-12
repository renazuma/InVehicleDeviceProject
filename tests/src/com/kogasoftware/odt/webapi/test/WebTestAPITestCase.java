package com.kogasoftware.odt.webapi.test;

import java.util.concurrent.CountDownLatch;

import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;

import android.test.ActivityInstrumentationTestCase2;

public class WebTestAPITestCase extends
		ActivityInstrumentationTestCase2<DummyActivity> {

	public WebTestAPITestCase() {
		super("com.kogasoftware.odt.webapi.test", DummyActivity.class);
	}
	
	public void testTruncateVehicleNotifications() throws Exception {
		WebTestAPI api = new WebTestAPI();
		final CountDownLatch latch = new CountDownLatch(1);

		api.cleanDatabase(new WebAPICallback<Void>() {
			@Override
			public void onSucceed(int reqkey, Void result) {
				latch.countDown();
			}
			
			@Override
			public void onFailed(int reqkey) {
				latch.countDown();
			}
			
			@Override
			public void onException(int reqkey, WebAPIException ex) {
				latch.countDown();
			}
		});

		latch.await();
	}

}
