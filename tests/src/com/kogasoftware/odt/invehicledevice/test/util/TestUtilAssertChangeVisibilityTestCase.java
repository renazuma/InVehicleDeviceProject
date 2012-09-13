package com.kogasoftware.odt.invehicledevice.test.util;

import junit.framework.AssertionFailedError;
import android.content.Context;
import android.content.Intent;
import android.test.InstrumentationTestCase;

import com.kogasoftware.odt.invehicledevice.ui.activity.EmptyActivity;

public class TestUtilAssertChangeVisibilityTestCase extends
		InstrumentationTestCase {
	public void setUp() throws Exception {
		super.setUp();
	}

	public void tearDown() throws Exception {
		try {
		} finally {
			super.tearDown();
		}
	}

	public void testAssertChangeVisibility() {
		// 表示されないことを確認
		Context context = getInstrumentation().getContext();
		try {
			TestUtil.assertChangeVisibility(context, EmptyActivity.class, true);
			throw new RuntimeException();
		} catch (AssertionFailedError e) {
		}
		TestUtil.assertChangeVisibility(context, EmptyActivity.class, false);

		// 表示
		Intent intent = new Intent(
				getInstrumentation().getTargetContext(), EmptyActivity.class);
		intent.setAction(Intent.ACTION_DEFAULT);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);

		// 表示されることを確認
		TestUtil.assertChangeVisibility(context, EmptyActivity.class, true);
		try {
			TestUtil.assertChangeVisibility(context, EmptyActivity.class, false);
			throw new RuntimeException();
		} catch (AssertionFailedError e) {
		}
	}
}
