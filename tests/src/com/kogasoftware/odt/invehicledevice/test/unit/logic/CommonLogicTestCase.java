package com.kogasoftware.odt.invehicledevice.test.unit.logic;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.ServiceUnitStatusLogs;
import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Reader;
import com.kogasoftware.odt.invehicledevice.logic.event.PauseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.StopEvent;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;

public class CommonLogicTestCase extends EmptyActivityInstrumentationTestCase2 {
	CommonLogic cl;
	StatusAccess sa;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cl = new CommonLogic(getActivity(), getActivityHandler());
		sa = cl.getStatusAccess();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (cl != null) {
			cl.dispose();
		}
	}

	/**
	 * PauseEventを受信すると状態がStatus.PAUSEとなる
	 */
	public void testPauseEvent() throws Exception {
		ServiceUnitStatusLog sul = sa.read(new Reader<ServiceUnitStatusLog>() {
			@Override
			public ServiceUnitStatusLog read(Status status) {
				return status.serviceUnitStatusLog;
			}
		});
		assertNotSame(sul.getStatus().get(), ServiceUnitStatusLogs.Status.PAUSE);
		cl.postEvent(new PauseEvent());
		getInstrumentation().waitForIdleSync();
		assertEquals(sul.getStatus().get(), ServiceUnitStatusLogs.Status.PAUSE);
	}

	/**
	 * StopEventを受信すると状態がStatus.STOPとなる
	 */
	public void testStopEvent() throws Exception {
		ServiceUnitStatusLog sul = sa.read(new Reader<ServiceUnitStatusLog>() {
			@Override
			public ServiceUnitStatusLog read(Status status) {
				return status.serviceUnitStatusLog;
			}
		});
		assertNotSame(sul.getStatus().get(), ServiceUnitStatusLogs.Status.STOP);
		cl.postEvent(new StopEvent());
		getInstrumentation().waitForIdleSync();
		assertEquals(sul.getStatus().get(), ServiceUnitStatusLogs.Status.STOP);
	}
}
