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

	public void callTestMakeText(String text, int duration) throws Exception {
		Toast t = BigToast.makeText(getInstrumentation().getTargetContext()
				.getApplicationContext(), text, duration);
		assertFalse(solo.searchText(text, true));
		t.show();
		assertTrue(solo.searchText(text, true));
		while (solo.searchText(text, true)) {
		}
	}
}
