package com.kogasoftware.odt.webapi.test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.test.ActivityInstrumentationTestCase2;

import com.kogasoftware.odt.webapi.WebAPI;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class WebAPITestCase extends ActivityInstrumentationTestCase2<DummyActivity> {

	public WebAPITestCase(String pkg, Class<DummyActivity> activityClass) {
		super(pkg, activityClass);
	}

	List<VehicleNotification> res;

	public void testGetVehicleNotifications() throws Exception {
		WebAPI api = new WebAPI("7EhKVSpqMu4a2p8SKDrH");
		final CountDownLatch latch = new CountDownLatch(1);
		res = null;
		
		api.getVehicleNotifications(new WebAPICallback<List<VehicleNotification>>() {
			@Override
			public void onSucceed(int reqkey, List<VehicleNotification> result) {
				res = result;
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
		
		latch.await(100, TimeUnit.SECONDS);
		
		assertNotNull(res);
		assertEquals(1, res.size());
	}
	
}
