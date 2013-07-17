package com.kogasoftware.odt.invehicledevice.test.util;

import junitx.framework.ObjectAssert;
import android.view.View;
import android.widget.Button;

import com.kogasoftware.odt.invehicledevice.test.R;

public class EmptyActivityInstrumentationTestCase2TestCase extends
		EmptyActivityInstrumentationTestCase2 {

	public void testInflateAndAddTestLayout() throws InterruptedException {
		View v = inflateAndAddTestLayout(R.layout.test_inflate_and_add_test_layout);
		View b1 = a.findViewById(R.id.test_inflate_and_add_test_layout_button);
		View b2 = v.findViewById(R.id.test_inflate_and_add_test_layout_button);
		assertNotNull(b1);
		ObjectAssert.assertInstanceOf(Button.class, b1);
		assertEquals(b1, b2);
	}
}
