package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice.sensor;

import android.telephony.SignalStrength;
import android.test.AndroidTestCase;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.ServiceUnitStatusLogLogic;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.sensor.SignalStrengthListener;

import static org.mockito.Mockito.*;

public class SignalStrengthListenerTestCase extends AndroidTestCase {
	InVehicleDeviceService s;
	ServiceUnitStatusLogLogic susll;
	SignalStrengthListener ssl;
	EventDispatcher ed;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ed = mock(EventDispatcher.class);
		s = mock(InVehicleDeviceService.class);
		when(s.getEventDispatcher()).thenReturn(ed);
		susll = new ServiceUnitStatusLogLogic(s);
		ssl = new SignalStrengthListener(susll);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testConvertSignalStrengthToPercentage_NoGsm() throws Exception {
		// GSMでない場合は失敗
		SignalStrength signalStrength = mock(SignalStrength.class);
		when(signalStrength.isGsm()).thenReturn(false);
		assertFalse(SignalStrengthListener.convertSignalStrengthToPercentage(
				signalStrength).isPresent());
	}

	public void testConvertSignalStrengthToPercentage_Gsm() throws Exception {
		// GSMの場合は値を計算
		SignalStrength signalStrength = mock(SignalStrength.class);
		when(signalStrength.isGsm()).thenReturn(true);

		// 以下、機種ごとに変わる可能性あり
		Integer[][] rules = new Integer[][] { { 0, -1 }, { 0, 0 }, { 0, 1 },
				{ 0, 2 }, { 25, 3 }, { 25, 4 }, { 50, 5 }, { 50, 6 },
				{ 50, 7 }, { 75, 8 }, { 75, 9 }, { 75, 10 }, { 75, 11 },
				{ 100, 12 }, { 100, 13 }, { 100, 14 }, { 100, 15 },
				{ 100, 98 }, { 0, 99 }, { 100, 100 }, { 100, 101 }, };
		for (Integer[] rule : rules) {
			Integer expected = rule[0];
			Integer gsmSignalStrength = rule[1];

			when(signalStrength.isGsm()).thenReturn(true);
			when(signalStrength.getGsmSignalStrength()).thenReturn(
					gsmSignalStrength);
			assertEquals(expected, SignalStrengthListener
					.convertSignalStrengthToPercentage(signalStrength).get());
		}
	}

	public void testOnSignalStrengthsChanged_NoGsm() throws Exception {
		// GSMでない場合は呼ばれない
		SignalStrength ss = mock(SignalStrength.class);
		when(ss.isGsm()).thenReturn(false);
		ssl.onSignalStrengthsChanged(ss);
		verify(ed, never()).dispatchChangeSignalStrength(anyInt());
	}

	public void testOnSignalStrengthsChanged_Gsm() throws Exception {
		// GSMの場合
		SignalStrength ss = mock(SignalStrength.class);
		when(ss.isGsm()).thenReturn(true);
		ssl.onSignalStrengthsChanged(ss);
		verify(ed, times(1)).dispatchChangeSignalStrength(anyInt());
	}
}
