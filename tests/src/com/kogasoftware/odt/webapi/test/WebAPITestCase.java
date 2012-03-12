package com.kogasoftware.odt.webapi.test;

import android.test.ActivityInstrumentationTestCase2;

public class WebAPITestCase extends
		ActivityInstrumentationTestCase2<DummyActivity> {

	public WebAPITestCase() {
		super("com.kogasoftware.odt.webapi.test", DummyActivity.class);
	}

	public void testAdd() {
		// assertEquals(WebAPI.add(1, 2), 3);
		assertTrue(true);
	}

	public void testFoo() {
		assertTrue(true);
		getActivity();
	}

}
