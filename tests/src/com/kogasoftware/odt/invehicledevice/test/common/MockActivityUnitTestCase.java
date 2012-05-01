package com.kogasoftware.odt.invehicledevice.test.common;

import android.app.Activity;
import android.content.Intent;
import android.test.ActivityUnitTestCase;

public class MockActivityUnitTestCase extends
		ActivityUnitTestCase<MockActivity> {

	private Activity a = null;

	public MockActivityUnitTestCase() {
		super(MockActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		startActivity(new Intent(), null, null);
		a = getActivity();
	}

	@Override
	protected void tearDown() throws Exception {
		a.finish();
		super.tearDown();
	}
}
