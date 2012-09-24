package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.VoidReader;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Writer;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.OperationScheduleLogic;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class OperationScheduleLogicTestCase extends AndroidTestCase {
	InVehicleDeviceService s;
	OperationScheduleLogic osl;
	LocalDataSource lds;
	List<OperationSchedule> remotes = Lists.newLinkedList();
	List<VehicleNotification> vns = Lists.newLinkedList();

	@Override
	public void setUp() throws Exception {
		super.setUp();
		lds = new LocalDataSource(getContext());
		s = mock(InVehicleDeviceService.class);
		when(s.getLocalDataSource()).thenReturn(lds);
		when(s.isOperationInitialized()).thenReturn(true);
		osl = new OperationScheduleLogic(s);
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.passengerRecords.clear();
				localData.operationSchedules.clear();
			}
		});
	}

	@Override
	public void tearDown() throws Exception {
		try {
		} finally {
			super.tearDown();
		}
	}

	public void testConstructor_NoServiceInteractions() {
		verifyZeroInteractions(s);
	}
	
	public void testGetCurrentOperationSchedules() throws Exception {
		String s1 = "[]";
		callTestGetCurrentOperationSchedules(s1, Optional.<Integer> absent());

		String s2 = "[{id: 10}]";
		callTestGetCurrentOperationSchedules(s2, Optional.of(10));

		String s3 = "[{id: 11, operation_record: {}}]";
		callTestGetCurrentOperationSchedules(s3, Optional.of(11));

		String s4 = "[{id: 12, operation_record: {arrived_at: '2012-01-01'}}]";
		callTestGetCurrentOperationSchedules(s4, Optional.of(12));

		String s5 = "[{id: 13, operation_record: {departed_at: '2012-01-01'}}]";
		callTestGetCurrentOperationSchedules(s5, Optional.<Integer> absent());

		String s6 = "[{id: 14, operation_record: {departed_at: '2012-01-01'}}, {id: 15}]";
		callTestGetCurrentOperationSchedules(s6, Optional.of(15));
	}
}
