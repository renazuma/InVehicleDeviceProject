package com.kogasoftware.odt.webapi.test;

import java.text.ParseException;
import java.util.Date;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kogasoftware.odt.webapi.WebAPI;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.Platform;
import com.kogasoftware.odt.webapi.model.User;

public class LoginTestCase extends TestCase {
	private String token = "";
	private WebAPI api = new WebAPI();

	@Override
	public void setUp() throws Exception {
		super.setUp();
		api = new WebAPI();
		token = api.operators.signIn("i_mogi", "i_mogi");
		assertTrue(token.length() > 0);
		api = new WebAPI(token);
	}

	public void testParseUser() throws JSONException, ParseException {
		User user = new User(new JSONObject("{id: '10', last_name: '日本語'}"));
		assertEquals(user.getId().longValue(), 10L);
		assertEquals(user.getLastName(), "日本語");
		assertFalse(user.getCurrentSignInAt().isPresent());
		User user2 = new User(
				new JSONObject(
						"{id: 11, current_sign_in_at: '2012-01-01T00:00:00.000+09:00'}"));
		assertEquals(user2.getId().longValue(), 11L);

		assertTrue(user2.getCurrentSignInAt().isPresent());
		User user3 = new User(new JSONObject(
				"{id: 20, reservations: [{memo: '予約1'}, {user_id: '50'}, {user: {id: 60, last_name: 'にほんご'}}]}"));
		assertEquals(user3.getId().longValue(), 20L);
		assertEquals(user3.getReservations().size(), 3);

		assertTrue(user3.getReservations().get(0).getMemo().isPresent());
		assertEquals(user3.getReservations().get(0).getMemo().get(), "予約1");

		assertFalse(user3.getReservations().get(1).getMemo().isPresent());
		assertEquals(user3.getReservations().get(1).getUserId().longValue(),
				50L);

		assertFalse(user3.getReservations().get(2).getMemo().isPresent());
		assertEquals(user3.getReservations().get(2).getUserId().longValue(),
				60L);
		assertTrue(user3.getReservations().get(2).getUser().isPresent());
		assertEquals(user3.getReservations().get(2).getUser().get().getId().longValue(), 60L);
		assertEquals(user3.getReservations().get(2).getUser().get().getLastName(), "にほんご");

	}

	public void testPlatformsGet() throws Exception {
		JSONArray o = api.get(Platform.URL.ROOT);
		assertNotNull(o);
	}

	public void xtestInVehicleDevicesCreate() throws Exception {
		InVehicleDevice inVehicleDevice = new InVehicleDevice();
		inVehicleDevice.setModelName("model-name-testr835789");
		inVehicleDevice.setTypeNumber("type-number-test9289458");

		InVehicleDevice createdInVehicleDevice = api.inVehicleDevices
				.create(inVehicleDevice);
		assertNotNull(createdInVehicleDevice);
		assertEquals(createdInVehicleDevice.getModelName(),
				inVehicleDevice.getModelName());

	}

	public void xtestUsersCreate() throws Exception {
		User user = new User();
		user.setAddress("住所");
		user.setAge(120l);
		user.setBirthday(new Date());
		user.setEmail("foo@example.com");
		user.setEmail2("bar@example.com");
		user.setFamilyName("名字");
		user.setFamilyNameRuby("みょうじ");
		user.setHandicapped(false);
		user.setLogin("user" + (new Date()).getTime());
		user.setLastName("名前");
		user.setLastNameRuby("なまえ");
		user.setNeededCare(false);
		user.setRecommendNotification(false);
		user.setRecommendOk(false);
		user.setReserveNotification(false);
		user.setServiceProviderId(1l);
		user.setSex(1l);
		user.setTelephoneNumber("1234-5678");
		user.setWheelchair(false);

		User createdUser = api.users.create(user, "pass237h854");
		assertNotNull(createdUser);
		assertEquals(user.getLastName(), createdUser.getLastName());

		// User foundUser = api.users.show(createdUser.getId());
		// assertNotNull(foundUser);
		// assertEquals(user.getLastName(), foundUser.getLastName());
	}

	public void testUsersUpdate(final User user) {
	}

	public void testUsersDestroy(final Integer id) {
	}
}
