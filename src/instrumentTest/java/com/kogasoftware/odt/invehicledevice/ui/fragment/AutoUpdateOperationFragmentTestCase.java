package com.kogasoftware.odt.invehicledevice.ui.fragment;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.kogasoftware.odt.invehicledevice.apiclient.InVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Platform;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Operation;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage;
import com.kogasoftware.odt.invehicledevice.testutil.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.testutil.TestUtil;
import com.kogasoftware.odt.invehicledevice.ui.fragment.AutoUpdateOperationFragment;

class TestAutoUpdateOperationFragment extends
		AutoUpdateOperationFragment<String> {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return new View(getActivity());
	}

	Integer operationScheduleReceiveSequence = 0;
	BlockingQueue<Operation> operations = new LinkedBlockingQueue<Operation>();

	public static TestAutoUpdateOperationFragment newInstance() {
		return AutoUpdateOperationFragment.newInstance(
				new TestAutoUpdateOperationFragment(), "");
	}

	@Override
	public void onUpdateOperation(Operation operation) {
		operations.add(operation);
	}

	@Override
	protected Integer getOperationSchedulesReceiveSequence() {
		return operationScheduleReceiveSequence;
	}
}

public class AutoUpdateOperationFragmentTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	InVehicleDeviceService s;
	InVehicleDeviceApiClient ac;
	LocalData ld;
	EventDispatcher ed;
	TestAutoUpdateOperationFragment f;
	ScheduledExecutorService ses;

	OperationSchedule os;
	Platform p;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ses = Executors.newScheduledThreadPool(1);
		ld = new LocalData();
		ed = mock(EventDispatcher.class);
		ac = mock(InVehicleDeviceApiClient.class);
		when(ac.withSaveOnClose()).thenReturn(ac);
		s = mock(InVehicleDeviceService.class);
		when(s.getLocalStorage()).thenReturn(new LocalStorage(ld));
		when(s.getEventDispatcher()).thenReturn(ed);
		when(s.getApiClient()).thenReturn(ac);
		when(s.getScheduledExecutorService()).thenReturn(ses);
		a.setService(s);

		os = new OperationSchedule();
		p = new Platform();
		os.setPlatform(p);
		os.setOperationRecord(new OperationRecord());
		ld.operation.operationSchedules.add(os);
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			ses.shutdownNow();
		} finally {
			super.tearDown();
		}
	}

	public void testUpdateOperationIfArgumentIsOld() throws Throwable {
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				f = TestAutoUpdateOperationFragment.newInstance();
				
				f.operationScheduleReceiveSequence = 99; // 古い
				ld.operation.operationScheduleReceiveSequence = 100; // 新しい
				
				// FragmentをActivityに配置
				int id = 12345;
				FrameLayout fl = new FrameLayout(a);
				fl.setId(id);
				a.setContentView(fl);
				FragmentManager fm = a.getSupportFragmentManager();
				fm.beginTransaction().add(id, f).commit();
			}
		});
		
		TestUtil.assertShow(f);
		
		assertNotNull(f.operations.poll(5, TimeUnit.SECONDS));
	}

	public void testNotUpdateOperationIfArgumentIsNew() throws Throwable {
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				f = TestAutoUpdateOperationFragment.newInstance();
				
				f.operationScheduleReceiveSequence = 100; // 古くない
				ld.operation.operationScheduleReceiveSequence = 100;
				
				// FragmentをActivityに配置
				int id = 12345;
				FrameLayout fl = new FrameLayout(a);
				fl.setId(id);
				a.setContentView(fl);
				FragmentManager fm = a.getSupportFragmentManager();
				fm.beginTransaction().add(id, f).commit();
			}
		});
		
		TestUtil.assertShow(f);
		
		assertNull(f.operations.poll(5, TimeUnit.SECONDS));
	}
}
