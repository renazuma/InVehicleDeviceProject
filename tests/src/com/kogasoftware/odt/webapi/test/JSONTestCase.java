package com.kogasoftware.odt.webapi.test;

import java.util.LinkedList;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONObject;

import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class JSONTestCase extends TestCase {
	 public void testVehicleNotificationParser() throws Exception {
		 String json = "[{\"body\":\"\u30c6\u30b9\u30c8\u30e1\u30c3\u30bb\u30fc\u30b8\",\"created_at\":\"2012-04-04T15:41:18+09:00\"}]";
		 JSONArray jsonArray = new JSONArray(json);
		 
		 LinkedList<VehicleNotification> vl = VehicleNotification.parseList(jsonArray);
		 
		 assertEquals(1, vl.size());
		 
		 VehicleNotification vn = vl.getFirst();		 
		 assertEquals("テストメッセージ", vn.getBody().get());
		 assertFalse(vn.getOperator().isPresent());
	 }
	 
	 public void testNull() throws Exception {
		 String json = "{\"body\":null,\"created_at\":\"2012-04-11T18:23:45Z\",\"id\":2,\"in_vehicle_device_id\":1,\"operator_id\":1,\"read_at\":null,\"response\":null,\"updated_at\":\"2012-04-11T18:23:45Z\"}";
		 
		 JSONObject jsonObject = new JSONObject(json);
		 
		 VehicleNotification vn = VehicleNotification.parse(jsonObject).orNull();
		 assertNull(vn.getBody().orNull());
		 assertNull(vn.getReadAt().orNull());
		 assertNull(vn.getResponse().orNull());
	 }

	 public void testOptional() throws Exception {
		 String json = "{\"created_at\":\"2012-04-11T18:23:45Z\",\"id\":2,\"in_vehicle_device_id\":1,\"operator_id\":1,\"updated_at\":\"2012-04-11T18:23:45Z\"}";
		 
		 JSONObject jsonObject = new JSONObject(json);
		 
		 VehicleNotification vn = VehicleNotification.parse(jsonObject).orNull();
		 assertNull(vn.getBody().orNull());
		 assertNull(vn.getReadAt().orNull());
		 assertNull(vn.getResponse().orNull());
	 }

	 public void testStringNull() throws Exception {
		 String json = "{\"body\": \"null\",\"created_at\":\"2012-04-11T18:23:45Z\",\"id\":2,\"in_vehicle_device_id\":1,\"operator_id\":1,\"read_at\":null,\"response\":null,\"updated_at\":\"2012-04-11T18:23:45Z\"}";
		 
		 JSONObject jsonObject = new JSONObject(json);
		 
		 VehicleNotification vn = VehicleNotification.parse(jsonObject).orNull();
		 assertEquals("null", vn.getBody().orNull());
		 assertNull(vn.getReadAt().orNull());
		 assertNull(vn.getResponse().orNull());
	 }

}
