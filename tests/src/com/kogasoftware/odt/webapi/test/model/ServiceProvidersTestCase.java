package com.kogasoftware.odt.webapi.test.model;

import org.json.JSONException;

import com.kogasoftware.odt.webapi.model.ServiceProvider;
import com.kogasoftware.odt.webapi.model.ServiceProviders;
import com.kogasoftware.odt.webapi.model.ServiceProviders.ReservationTimeLimit;

import junit.framework.TestCase;


public class ServiceProvidersTestCase extends TestCase {
	public void testConstructor() {
		// カバレッジを満たすためのコード.今のところコンストラクタを使うことは無い.
		new ServiceProviders();
	}
	
	public void testParse() throws Exception {
		ServiceProvider sp = new ServiceProvider();
		ReservationTimeLimit rtl;
		
		try {
			sp.setReservationTimeLimit("{foo: 'bar', android_app: 1234}");
			rtl = ServiceProviders.parseReservationTimeLimit(sp);
			assertEquals(1234, rtl.getAndroidApp().intValue());
			fail();
		} catch(JSONException e) {
		}
		
		try {
			sp.setReservationTimeLimit("{operator_web: 532, bar: 89224}");
			rtl = ServiceProviders.parseReservationTimeLimit(sp);
			assertEquals(532, rtl.getOperatorWeb().intValue());
			fail();
		} catch(JSONException e) {
		}
		
		sp.setReservationTimeLimit("{android_app: 1, operator_web: 2}");
		rtl = ServiceProviders.parseReservationTimeLimit(sp);
		assertEquals(1, rtl.getAndroidApp().intValue());
		assertEquals(2, rtl.getOperatorWeb().intValue());
		
		sp.setReservationTimeLimit("{operator_web: 200, android_app: 100, hoge: 'fuga'}");
		rtl = ServiceProviders.parseReservationTimeLimit(sp);
		assertEquals(100, rtl.getAndroidApp().intValue());
		assertEquals(200, rtl.getOperatorWeb().intValue());
		
		sp.setReservationTimeLimit("{android_app: 18, hoge: 'zdfoisr', operator_web: 19}");
		rtl = ServiceProviders.parseReservationTimeLimit(sp);
		assertEquals(18, rtl.getAndroidApp().intValue());
		assertEquals(19, rtl.getOperatorWeb().intValue());
		
		sp.setReservationTimeLimit("{hoge: 8953, operator_web: 20, android_app: 21}");
		rtl = ServiceProviders.parseReservationTimeLimit(sp);
		assertEquals(21, rtl.getAndroidApp().intValue());
		assertEquals(20, rtl.getOperatorWeb().intValue());
	}
}
