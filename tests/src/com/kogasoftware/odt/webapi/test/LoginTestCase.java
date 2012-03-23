package com.kogasoftware.odt.webapi.test;

import java.text.ParseException;

import junit.framework.TestCase;

import org.json.JSONException;

import com.kogasoftware.odt.webapi.WebAPI;
import com.kogasoftware.odt.webapi.model.User;

public class LoginTestCase extends TestCase {
	private String token = "";
	private WebAPI api = new WebAPI();

	@Override
	public void setUp() throws Exception {
		super.setUp();
		//		api = new WebAPI();
		//		token = api.operators.signIn("i_mogi", "i_mogi");
		//		assertTrue(token.length() > 0);
		//		api = new WebAPI(token);
	}

	public void testParseUser() throws JSONException, ParseException {
		//		User user = new User(new JSONObject("{id: '10', last_name: '日本語'}"));
		//		assertEquals(user.getId().longValue(), 10L);
		//		assertEquals(user.getLastName(), "日本語");
		//		assertFalse(user.getCurrentSignInAt().isPresent());
		//		User user2 = new User(
		//				new JSONObject(
		//						"{id: 11, current_sign_in_at: '2012-01-01T00:00:00.000+09:00'}"));
		//		assertEquals(user2.getId().longValue(), 11L);
		//
		//		assertTrue(user2.getCurrentSignInAt().isPresent());
		//		User user3 = new User(new JSONObject(
		//				"{id: 20, reservations: [{memo: '予約1'}, {user_id: '50'}, {user: {id: 60, last_name: 'にほんご'}}]}"));
		//		assertEquals(user3.getId().longValue(), 20L);
		//		assertEquals(user3.getReservations().size(), 3);
		//
		//		assertTrue(user3.getReservations().get(0).getMemo().isPresent());
		//		assertEquals(user3.getReservations().get(0).getMemo().get(), "予約1");
		//
		//		assertFalse(user3.getReservations().get(1).getMemo().isPresent());
		//		assertEquals(user3.getReservations().get(1).getUserId().longValue(),
		//				50L);
		//
		//		assertFalse(user3.getReservations().get(2).getMemo().isPresent());
		//		assertEquals(user3.getReservations().get(2).getUserId().longValue(),
		//				60L);
		//		assertTrue(user3.getReservations().get(2).getUser().isPresent());
		//		assertEquals(user3.getReservations().get(2).getUser().get().getId().longValue(), 60L);
		//		assertEquals(user3.getReservations().get(2).getUser().get().getLastName(), "にほんご");

	}


	public void testUsersUpdate(final User user) {
	}

	public void testUsersDestroy(final Integer id) {
	}
}
