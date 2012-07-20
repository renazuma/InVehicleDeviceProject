package com.kogasoftware.odt.invehicledevice.test.unit.logic;

import android.location.GpsStatus;
import android.location.Location;
import android.test.ServiceTestCase;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.VoidReader;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Writer;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.ServiceUnitStatusLogLogic;

public class ServiceUnitStatusLogLogicTestCase extends
		ServiceTestCase<InVehicleDeviceService> {
	public ServiceUnitStatusLogLogicTestCase() {
		super(InVehicleDeviceService.class);
	}

	LocalDataSource sa;
	InVehicleDeviceService s;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setupService();
		s = getService();
		sa = new LocalDataSource(s);
		sa.withWriteLock(new Writer() {
			@Override
			public void write(LocalData status) {
				status.vehicleNotifications.clear();
				status.repliedVehicleNotifications.clear();
				status.receivingOperationScheduleChangedVehicleNotifications
						.clear();
				status.receivedOperationScheduleChangedVehicleNotifications
						.clear();
			}
		});
	}

	@Override
	protected void tearDown() throws Exception {
		shutdownService();
		super.tearDown();
	}

	public void testSetLocation() {
		String provider = "test";
		for (Integer i = 0; i < 20; ++i) {
			final Integer lat = 10 + i * 2; // TODO:値が丸まっていないかのテスト
			final Integer lon = 45 + i;
			Location l = new Location(provider);
			l.setLatitude(lat);
			l.setLongitude(lon);
			s.changeLocation(l, Optional.<GpsStatus> absent());
			sa.withReadLock(new VoidReader() {
				@Override
				public void read(LocalData status) {
					assertEquals(status.serviceUnitStatusLog.getLatitude()
							.intValue(), lat.intValue());
					assertEquals(status.serviceUnitStatusLog.getLongitude()
							.intValue(), lon.intValue());
				}
			});
		}
	}

	public void testSetOrientation() throws Exception {
		final Double f1 = 10.0;
		final Double f2 = 20.0;

		Thread.sleep(ServiceUnitStatusLogLogic.ORIENTATION_SAVE_PERIOD_MILLIS);
		s.changeOrientation(f1);
		
		sa.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData status) {
				assertEquals(status.serviceUnitStatusLog.getOrientation().get()
						.intValue(), f1.intValue());
			}
		});

		Thread.sleep(ServiceUnitStatusLogLogic.ORIENTATION_SAVE_PERIOD_MILLIS);
		s.changeOrientation(f2);
		sa.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData status) {
				assertEquals(status.serviceUnitStatusLog.getOrientation().get()
						.intValue(), f2.intValue());
			}
		});
	}

	public void testSetTemperature() {
		final Double f1 = 30.0;
		final Double f2 = 40.0;

		s.changeTemperature(f1);
		sa.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData status) {
				assertEquals(status.serviceUnitStatusLog.getTemperature().get()
						.intValue(), f1.intValue());
			}
		});

		s.changeTemperature(f2);
		sa.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData status) {
				assertEquals(status.serviceUnitStatusLog.getTemperature().get()
						.intValue(), f2.intValue());
			}
		});
	}
}
