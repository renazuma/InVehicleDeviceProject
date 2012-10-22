package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice;

import java.io.File;
import org.apache.commons.lang3.SerializationUtils;
import android.test.AndroidTestCase;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceProvider;

public class LocalDataTestCase extends AndroidTestCase {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * 本当にシリアライズすることができるかのチェック
	 */
	public void testSerializable() throws Exception {
		LocalData ld1 = new LocalData();
		ld1.url = "http://example.com/" + Math.random();
		ld1.file = new File("foo" + Math.random());
		ld1.serviceProvider = new ServiceProvider();
		ld1.serviceProvider.setName("テストサービスプロバイダー");
		LocalData ld2 = SerializationUtils.clone(ld1);
		assertEquals(ld1.url, ld2.url);
		assertEquals(ld1.file, ld2.file);
		assertFalse(ld1.file == ld2.file);
		assertEquals(ld1.serviceProvider.getName(), ld2.serviceProvider.getName());
	}
}
