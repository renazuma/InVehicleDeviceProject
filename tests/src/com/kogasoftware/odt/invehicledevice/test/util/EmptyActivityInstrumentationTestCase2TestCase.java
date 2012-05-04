package com.kogasoftware.odt.invehicledevice.test.util;

import android.view.View;
import android.view.ViewGroup;

import com.kogasoftware.odt.invehicledevice.ui.modalview.ConfigModalView;

public class EmptyActivityInstrumentationTestCase2TestCase extends
		EmptyActivityInstrumentationTestCase2 {

	public void testInflateTestLayout() throws InterruptedException {
		View v = inflateAndAddTestLayout(com.kogasoftware.odt.invehicledevice.test.R.layout.test_config_modal_view);
		assertTrue(v instanceof ConfigModalView);
		ViewGroup vg = (ViewGroup) getActivity().findViewById(
				android.R.id.content);
		Integer childCount = vg.getChildCount();
		for (Integer i = 0; i < childCount; ++i) {
			if (vg.getChildAt(i) instanceof ConfigModalView) {
				return;
			}
		}
		fail();
	}
}
