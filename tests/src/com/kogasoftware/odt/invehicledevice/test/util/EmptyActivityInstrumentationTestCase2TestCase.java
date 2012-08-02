package com.kogasoftware.odt.invehicledevice.test.util;

import android.view.View;
import android.view.ViewGroup;

import com.kogasoftware.odt.invehicledevice.ui.modalview.NavigationModalView;

public class EmptyActivityInstrumentationTestCase2TestCase extends
		EmptyActivityInstrumentationTestCase2 {

	public void xtestInflateTestLayout() throws InterruptedException {
		View v = new NavigationModalView(null, null, null);
		assertTrue(v instanceof NavigationModalView);
		ViewGroup vg = (ViewGroup) getActivity().findViewById(
				android.R.id.content);
		Integer childCount = vg.getChildCount();
		for (Integer i = 0; i < childCount; ++i) {
			if (vg.getChildAt(i) instanceof NavigationModalView) {
				return;
			}
		}
		fail();
	}
}
