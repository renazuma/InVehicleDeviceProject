package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import android.location.GpsStatus;
import android.location.Location;
import android.test.AndroidTestCase;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.VoidReader;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.Writer;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.ServiceUnitStatusLogLogic;

public class ServiceUnitStatusLogLogicTestCase extends AndroidTestCase {
	LocalStorage lds;
	InVehicleDeviceService s;
	ServiceUnitStatusLogLogic susll;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		lds = new LocalStorage(getContext());
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData status) {
				status.vehicleNotifications.clear();
			}
		});
		s = mock(InVehicleDeviceService.class);
		when(s.getLocalStorage()).thenReturn(lds);
		when(s.getEventDispatcher()).thenReturn(new EventDispatcher());
		susll = new ServiceUnitStatusLogLogic(s);
	}

	public void testConstructor_NoServiceInteractions() {
		verifyZeroInteractions(s);
	}

	public void testSetLocation() {
		String provider = "test";
		for (Integer i = 0; i < 20; ++i) {
			final Integer lat = 10 + i * 2; // TODO:値が丸まっていないかのテスト
			final Integer lon = 45 + i;
			Location l = new Location(provider);
			l.setLatitude(lat);
			l.setLongitude(lon);
			susll.changeLocation(l, Optional.<GpsStatus> absent());
			lds.withReadLock(new VoidReader() {
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
		susll.changeOrientation(f1);

		lds.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData status) {
				assertEquals(status.serviceUnitStatusLog.getOrientation().get()
						.intValue(), f1.intValue());
			}
		});

		Thread.sleep(ServiceUnitStatusLogLogic.ORIENTATION_SAVE_PERIOD_MILLIS);
		susll.changeOrientation(f2);
		lds.withReadLock(new VoidReader() {
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

		susll.changeTemperature(f1);
		lds.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData status) {
				assertEquals(status.serviceUnitStatusLog.getTemperature().get()
						.intValue(), f1.intValue());
			}
		});

		susll.changeTemperature(f2);
		lds.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData status) {
				assertEquals(status.serviceUnitStatusLog.getTemperature().get()
						.intValue(), f2.intValue());
			}
		});
	}
}
