package com.kogasoftware.odt.invehicledevice.test.unit.ui.arrayadapter;

import java.util.ArrayList;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.OperationScheduleArrayAdapter;
import com.kogasoftware.odt.webapi.model.OperationSchedule;

public class OperationScheduleArrayAdapterTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	CommonLogic cl;
	OperationScheduleArrayAdapter osaa;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cl = newCommonLogic();
		osaa = new OperationScheduleArrayAdapter(getInstrumentation()
				.getContext(), new ArrayList<OperationSchedule>(), cl);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (cl != null) {
			cl.dispose();
		}
	}

	public void testOperationScheduleが表示される() {
		fail("stub!");
	}

	public void testOperationScheduleが変更されたら変更後の表示になる() {
		fail("stub!");
	}

	public void test最後のOperationScheduleの出発時刻は表示されない() {
		fail("stub!");
	}

	public void test最初のOperationScheduleの到着時刻は表示されない() {
		fail("stub!");
	}

	public void test終了したOperationScheduleは表示色が変更される() {
		fail("stub!");
	}
}
