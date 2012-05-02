package com.kogasoftware.odt.invehicledevice.test.util;

import android.os.Handler;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.ui.activity.EmptyActivity;

public class EmptyActivityInstrumentationTestCase2 extends
		ActivityInstrumentationTestCase2<EmptyActivity> {

	public Solo solo;

	public EmptyActivityInstrumentationTestCase2() {
		super("com.kogasoftware.odt.invehicledevice", EmptyActivity.class);
	}

	public Handler getActivityHandler() throws InterruptedException {
		return MockActivityUnitTestCase.getActivityHandler(getActivity());
	}

	public void runOnUiThreadSync(Runnable runnable)
			throws InterruptedException {
		MockActivityUnitTestCase.runOnUiThreadSync(getActivity(), runnable);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	protected void tearDown() throws Exception {
		solo.finishInactiveActivities();
		super.tearDown();
	}
}
