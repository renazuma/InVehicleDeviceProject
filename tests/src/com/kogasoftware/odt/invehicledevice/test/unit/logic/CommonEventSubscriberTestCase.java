package com.kogasoftware.odt.invehicledevice.test.unit.logic;

import android.location.Location;

import com.kogasoftware.odt.invehicledevice.backgroundtask.CommonEventSubscriber;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.VoidReader;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.logic.event.LocationReceivedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.OrientationChangedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.TemperatureChangedEvent;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;

public class CommonEventSubscriberTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	CommonLogic cl;
	StatusAccess sa;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sa = new StatusAccess(getActivity());
		sa.write(new Writer() {
			@Override
			public void write(Status status) {
				status.vehicleNotifications.clear();
				status.repliedVehicleNotifications.clear();
				status.receivingOperationScheduleChangedVehicleNotifications
						.clear();
				status.receivedOperationScheduleChangedVehicleNotifications
						.clear();
			}
		});

		cl = new CommonLogic(getActivity(), getActivityHandler(), sa);

		assertEquals(cl.countRegisteredClass(CommonEventSubscriber.class)
				.intValue(), 1);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (cl != null) {
			cl.dispose();
		}
	}

	public void testSetLocation() {
		String provider = "test";
		for (Integer i = 0; i < 20; ++i) {
			final Integer lat = 10 + i * 2; // TODO:値が丸まっていないかのテスト
			final Integer lon = 45 + i;
			Location l = new Location(provider);
			l.setLatitude(lat);
			l.setLongitude(lon);
			cl.postEvent(new LocationReceivedEvent(l));
			getInstrumentation().waitForIdleSync();
			sa.read(new VoidReader() {
				@Override
				public void read(Status status) {
					assertEquals(status.serviceUnitStatusLog.getLatitude()
							.intValue(), lat.intValue());
					assertEquals(status.serviceUnitStatusLog.getLongitude()
							.intValue(), lon.intValue());
				}
			});
		}
	}

	public void testSetOrientation() {
		final Float f1 = 10f;
		final Float f2 = 20f;

		cl.postEvent(new OrientationChangedEvent(f1));
		getInstrumentation().waitForIdleSync();
		sa.read(new VoidReader() {
			@Override
			public void read(Status status) {
				assertEquals(status.serviceUnitStatusLog.getOrientation().get()
						.intValue(), f1.intValue());
			}
		});

		cl.postEvent(new OrientationChangedEvent(f2));
		getInstrumentation().waitForIdleSync();
		sa.read(new VoidReader() {
			@Override
			public void read(Status status) {
				assertEquals(status.serviceUnitStatusLog.getOrientation().get()
						.intValue(), f2.intValue());
			}
		});
	}

	public void testSetTemperature() {
		final Float f1 = 30f;
		final Float f2 = 40f;

		cl.postEvent(new TemperatureChangedEvent(f1));
		getInstrumentation().waitForIdleSync();
		sa.read(new VoidReader() {
			@Override
			public void read(Status status) {
				assertEquals(status.serviceUnitStatusLog.getTemperature().get()
						.intValue(), f1.intValue());
			}
		});

		cl.postEvent(new TemperatureChangedEvent(f2));
		getInstrumentation().waitForIdleSync();
		sa.read(new VoidReader() {
			@Override
			public void read(Status status) {
				assertEquals(status.serviceUnitStatusLog.getTemperature().get()
						.intValue(), f2.intValue());
			}
		});
	}
}
