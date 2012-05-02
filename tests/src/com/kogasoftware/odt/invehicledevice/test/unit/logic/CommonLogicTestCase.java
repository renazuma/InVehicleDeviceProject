package com.kogasoftware.odt.invehicledevice.test.unit.logic;

import java.util.Date;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;

public class CommonLogicTestCase extends EmptyActivityInstrumentationTestCase2 {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testClearStatusFile_1() throws Exception {
		CommonLogic.clearStatusFile();
		fail("stub!");
	}

	public void testGetDate_1() throws Exception {
		CommonLogic.getDate();
		fail("stub!");
	}

	public void testSetDate_1() throws Exception {
		CommonLogic.setDate(new Date());
		fail("stub!");
	}
}
