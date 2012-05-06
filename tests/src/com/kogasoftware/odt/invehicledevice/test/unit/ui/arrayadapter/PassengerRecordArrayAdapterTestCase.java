package com.kogasoftware.odt.invehicledevice.test.unit.ui.arrayadapter;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.PassengerRecordArrayAdapter;

public class PassengerRecordArrayAdapterTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	CommonLogic cl;
	PassengerRecordArrayAdapter raa;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cl = newCommonLogic();
		raa = new PassengerRecordArrayAdapter(
				getInstrumentation().getContext(), cl);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (cl != null) {
			cl.dispose();
		}
	}

	public void testPassengerRecordが表示される() throws Exception {
		fail("stub!");
	}
}
