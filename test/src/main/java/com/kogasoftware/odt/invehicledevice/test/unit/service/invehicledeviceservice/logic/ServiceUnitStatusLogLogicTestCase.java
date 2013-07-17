package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice.logic;

import static org.mockito.Mockito.*;

import java.io.Closeable;
import java.util.concurrent.Callable;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import android.location.GpsStatus;
import android.location.Location;
import android.test.AndroidTestCase;

import com.google.common.base.Optional;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.apiclient.ApiClientCallback;
import com.kogasoftware.odt.invehicledevice.apiclient.InVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.ServiceUnitStatusLogLogic;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;

public class ServiceUnitStatusLogLogicTestCase extends AndroidTestCase {
	LocalData ld = new LocalData();
	InVehicleDeviceService s;
	InVehicleDeviceApiClient ac;
	ServiceUnitStatusLogLogic susll;
	EventDispatcher ed;
	Closeable th;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ld.vehicleNotifications.clear();
		ac = mock(InVehicleDeviceApiClient.class);
		when(ac.withSaveOnClose()).thenReturn(ac);
		ed = mock(EventDispatcher.class);
		s = mock(InVehicleDeviceService.class);
		when(s.getLocalStorage()).thenReturn(new LocalStorage(ld));
		when(s.getEventDispatcher()).thenReturn(ed);
		when(s.getApiClient()).thenReturn(ac);
		susll = new ServiceUnitStatusLogLogic(s);
		th = TestUtil.setTestThreadHandler();
	}

	@Override
	protected void tearDown() throws Exception {
		Closeables.closeQuietly(th);
		super.tearDown();
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
//			susll.changeLocation(l, Optional.<GpsStatus> absent());
//			TestUtil.assertChange(new Callable<Boolean>() {
//				@Override
//				public Boolean call() throws Exception {
//					return ld.serviceUnitStatusLog.getLatitude().intValue() == lat
//							.intValue()
//							&& ld.serviceUnitStatusLog.getLongitude()
//									.intValue() == lon.intValue();
//				}
//			});
//			ArgumentCaptor<Location> ac = ArgumentCaptor
//					.forClass(Location.class);
//			verify(ed, timeout(500).times(i + 1)).dispatchChangeLocation(
//					ac.capture(), Mockito.<Optional<GpsStatus>> any());
//			assertEquals(ac.getValue().getLatitude(), l.getLatitude());
//			assertEquals(ac.getValue().getLongitude(), l.getLongitude());
		}
	}

	public void testSetOrientation() throws Exception {
		final Double f1 = 10.0;
		final Double f2 = 20.0;

		Thread.sleep(ServiceUnitStatusLogLogic.ORIENTATION_SAVE_PERIOD_MILLIS);
		susll.changeOrientation(f1);
		TestUtil.assertChange(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return ld.serviceUnitStatusLog.getOrientation().or(-1)
						.intValue() == f1.intValue();
			}
		});

		Thread.sleep(ServiceUnitStatusLogLogic.ORIENTATION_SAVE_PERIOD_MILLIS);
		susll.changeOrientation(f2);
		TestUtil.assertChange(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return ld.serviceUnitStatusLog.getOrientation().or(-1)
						.intValue() == f2.intValue();
			}
		});

		ArgumentCaptor<Double> ac = ArgumentCaptor.forClass(Double.class);
		verify(ed, timeout(500).times(2)).dispatchChangeOrientation(
				ac.capture());
		assertEquals(ac.getAllValues().get(0), f1);
		assertEquals(ac.getAllValues().get(1), f2);
	}

	public void testSetTemperature() {
		final Double f1 = 30.0;
		final Double f2 = 40.0;

		susll.changeTemperature(f1);
		TestUtil.assertChange(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return ld.serviceUnitStatusLog.getTemperature().or(-1)
						.intValue() == f1.intValue();
			}
		});

		susll.changeTemperature(f2);
		TestUtil.assertChange(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return ld.serviceUnitStatusLog.getTemperature().or(-1)
						.intValue() == f2.intValue();
			}
		});

		ArgumentCaptor<Double> ac = ArgumentCaptor.forClass(Double.class);
		verify(ed, timeout(500).times(2)).dispatchChangeTemperature(
				ac.capture());
		assertEquals(ac.getAllValues().get(0), f1);
		assertEquals(ac.getAllValues().get(1), f2);
	}

	public void testGetWithReadLock() {
		for (Integer i = 0; i < 5; ++i) {
			ld.serviceUnitStatusLog.setId(i);
			assertEquals(i, susll.getWithReadLock().getId());
		}
	}

	public void testSendWithReadLock() {
		for (Integer i = 0; i < 5; ++i) {
			ld.serviceUnitStatusLog.setId(i);
			susll.sendWithReadLock();
			ArgumentCaptor<ServiceUnitStatusLog> c = ArgumentCaptor
					.forClass(ServiceUnitStatusLog.class);
			verify(ac, times(i + 1)).sendServiceUnitStatusLog(c.capture(),
					Mockito.<ApiClientCallback<ServiceUnitStatusLog>> any());
			assertEquals(i, c.getValue().getId());
		}
	}
}
