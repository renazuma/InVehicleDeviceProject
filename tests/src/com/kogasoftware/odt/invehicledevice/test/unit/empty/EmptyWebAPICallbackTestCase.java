package com.kogasoftware.odt.invehicledevice.test.unit.empty;

import com.kogasoftware.odt.invehicledevice.empty.EmptyWebAPICallback;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;

import android.test.AndroidTestCase;

public class EmptyWebAPICallbackTestCase extends AndroidTestCase {
	public void testSmallInstance() throws Exception {
		TestUtil.assertEmptyObject(getContext(), EmptyWebAPICallback.class);
	}
}
