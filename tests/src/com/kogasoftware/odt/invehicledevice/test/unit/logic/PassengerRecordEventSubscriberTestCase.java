package com.kogasoftware.odt.invehicledevice.test.unit.logic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Function;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.backgroundtask.PassengerRecordEventSubscriber;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Reader;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.VoidReader;
import com.kogasoftware.odt.invehicledevice.logic.event.OrientationChangedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.PauseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.StopEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.TemperatureChangedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.VehicleNotificationRepliedEvent;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.modalview.NotificationModalView;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLogs;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class PassengerRecordEventSubscriberTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	CommonLogic cl;
	StatusAccess sa;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sa = new StatusAccess(getActivity());
		cl = new CommonLogic(getActivity(), getActivityHandler(), sa);

		assertEquals(
				cl.countRegisteredClass(PassengerRecordEventSubscriber.class)
						.intValue(), 1);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (cl != null) {
			cl.dispose();
		}
	}

	public void xtestAddUnexpectedPassenger() {
		fail("stub");
	}

	public void xtestGetOn() {
		fail("stub");
	}

	public void xtestGetOff() {
		fail("stub");
	}
}
