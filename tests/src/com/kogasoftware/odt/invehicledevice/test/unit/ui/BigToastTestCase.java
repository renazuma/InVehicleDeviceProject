package com.kogasoftware.odt.invehicledevice.test.unit.ui;

import android.widget.Toast;

import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.BigToast;

public class BigToastTestCase extends EmptyActivityInstrumentationTestCase2 {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testMakeText() throws Exception {
		callTestMakeText("hoge", Toast.LENGTH_SHORT);
		callTestMakeText("ああああ", Toast.LENGTH_LONG);
		callTestMakeText("いいいい", Toast.LENGTH_SHORT);
		callTestMakeText("あいうえおかきくけこさしすせそ", Toast.LENGTH_LONG);
	}

	public void callTestMakeText(final String text, final int duration) throws Exception {
		assertFalse(solo.searchText(text, true));
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				BigToast.makeText(getActivity(), text, duration).show();
			}
		});
		assertTrue(solo.searchText(text, true));
		while (solo.searchText(text, true)) {
		}
	}
}
