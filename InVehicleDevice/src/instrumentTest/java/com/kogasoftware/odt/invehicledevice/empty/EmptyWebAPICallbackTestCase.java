package com.kogasoftware.odt.invehicledevice.empty;

import com.kogasoftware.odt.apiclient.EmptyApiClientCallback;
import com.kogasoftware.odt.invehicledevice.testutil.TestUtil;

import android.test.InstrumentationTestCase;

public class EmptyWebAPICallbackTestCase extends InstrumentationTestCase {
	public void testSmallInstance() throws Exception {
		TestUtil.assertEmptyObject(getInstrumentation(), EmptyApiClientCallback.class);
	}
}
