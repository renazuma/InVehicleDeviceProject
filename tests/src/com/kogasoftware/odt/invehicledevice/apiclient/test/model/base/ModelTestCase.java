package com.kogasoftware.odt.invehicledevice.apiclient.test.model.base;

import java.util.List;

import junit.framework.TestCase;

import com.kogasoftware.odt.invehicledevice.apiclient.model.*;

public class ModelTestCase extends TestCase {
	public void testVehicleNotificationParser() throws Exception {
		String json = "[{\"body\":\"\u30c6\u30b9\u30c8\u30e1\u30c3\u30bb\u30fc\u30b8\",\"created_at\":\"2012-04-04T15:41:18+09:00\"}]";

		List<VehicleNotification> vl = VehicleNotification.parseList(json);

		assertEquals(1, vl.size());

		VehicleNotification vn = vl.get(0);
		assertEquals("テストメッセージ", vn.getBody());
		assertFalse(vn.getOperator().isPresent());
	}

	public void testNull() throws Exception {
		String json = "{\"event_at\":null,\"created_at\":\"2012-04-11T18:23:45Z\",\"id\":2,\"in_vehicle_device_id\":1,\"operator_id\":1,\"read_at\":null,\"response\":null,\"updated_at\":\"2012-04-11T18:23:45Z\"}";

		VehicleNotification vn = VehicleNotification.parse(json);
		assertNull(vn.getEventAt().orNull());
		assertNull(vn.getReadAt().orNull());
		assertNull(vn.getResponse().orNull());
	}

	public void testOptional() throws Exception {
		String json = "{\"created_at\":\"2012-04-11T18:23:45Z\",\"id\":2,\"in_vehicle_device_id\":1,\"operator_id\":1,\"updated_at\":\"2012-04-11T18:23:45Z\"}";

		VehicleNotification vn = VehicleNotification.parse(json);
		assertNull(vn.getEventAt().orNull());
		assertNull(vn.getReadAt().orNull());
		assertNull(vn.getResponse().orNull());
	}

	public void testStringNull() throws Exception {
		String json = "{\"body\": \"null\",\"created_at\":\"2012-04-11T18:23:45Z\",\"id\":2,\"in_vehicle_device_id\":1,\"operator_id\":1,\"read_at\":null,\"response\":null,\"updated_at\":\"2012-04-11T18:23:45Z\"}";

		VehicleNotification vn = VehicleNotification.parse(json);
		assertEquals("null", vn.getBody());
		assertNull(vn.getReadAt().orNull());
		assertNull(vn.getResponse().orNull());
	}
}
