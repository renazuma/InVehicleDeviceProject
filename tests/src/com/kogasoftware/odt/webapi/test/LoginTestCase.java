package com.kogasoftware.odt.webapi.test;

import java.util.Date;

import junit.framework.TestCase;

import org.json.JSONArray;

import com.kogasoftware.odt.webapi.WebAPI;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.Platform;
import com.kogasoftware.odt.webapi.model.User;

public class LoginTestCase extends TestCase {
	private final String TAG = LoginTestCase.class.getSimpleName();
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

	public void testUsersShow() {
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
		user.setAge(120);
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
		user.setServiceProviderId(1);
		user.setSex(1);
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
