package com.kogasoftware.odt.invehicledevice.test.util;

import android.os.Handler;
import android.test.ActivityInstrumentationTestCase2;

import com.kogasoftware.odt.invehicledevice.ui.activity.EmptyActivity;

public class EmptyActivityInstrumentationTestCase2 extends
		ActivityInstrumentationTestCase2<EmptyActivity> {

	public EmptyActivityInstrumentationTestCase2() {
		super("com.kogasoftware.odt.invehicledevice", EmptyActivity.class);
	}

	public Handler getActivityHandler() throws InterruptedException {
		return MockActivityUnitTestCase.getActivityHandler(getActivity());
	}
}
