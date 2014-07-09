package com.kogasoftware.odt.invehicledevice.ui;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Toast;

import com.kogasoftware.odt.invehicledevice.ui.activity.EmptyActivity;
import com.robotium.solo.Solo;

public class BigToastTestCase
		extends
			ActivityInstrumentationTestCase2<EmptyActivity> {
	Solo solo;

	public BigToastTestCase() {
		super(EmptyActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}

	public void testMakeText() throws Throwable {
		callTestMakeText("hoge", Toast.LENGTH_SHORT);
		callTestMakeText("ああああ", Toast.LENGTH_LONG);
		callTestMakeText("いいいい", Toast.LENGTH_SHORT);
		callTestMakeText("あいうえおかきくけこさしすせそ", Toast.LENGTH_LONG);
	}

	public void callTestMakeText(final String text, final int duration)
			throws Throwable {
		assertFalse(solo.searchText(text, true));
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				BigToast.makeText(solo.getCurrentActivity(), text, duration)
						.show();
			}
		});
		assertTrue(solo.searchText(text, true));
		for (int i = 0; solo.searchText(text, true); ++i) {
			assertTrue(i < 100);
			Thread.sleep(500);
		}
	}
}
