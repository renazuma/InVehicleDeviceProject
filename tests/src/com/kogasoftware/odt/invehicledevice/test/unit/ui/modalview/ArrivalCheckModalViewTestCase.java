package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview;

import java.util.concurrent.CountDownLatch;

import android.app.Activity;
import android.view.View;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Writer;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.modalview.ArrivalCheckModalView;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;
import static org.mockito.Mockito.*;

public class ArrivalCheckModalViewTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	InVehicleDeviceService s;
	LocalDataSource sa;
	ArrivalCheckModalView mv;
	Activity a;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		a = getActivity();
		sa = new LocalDataSource(getActivity());
		s = mock(InVehicleDeviceService.class);
		mv = new ArrivalCheckModalView(a, s);
		
		sa.withWriteLock(new Writer() {
			@Override
			public void write(LocalData status) {
				OperationSchedule os = new OperationSchedule();
				Platform p = new Platform();
				p.setName("乗降場X");
				os.setPlatform(p);
				status.remainingOperationSchedules.clear();
				status.remainingOperationSchedules.add(os);
			}
		});
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void xtestEventBusに自動で登録される() throws Exception {
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				getActivity().setContentView(R.layout.in_vehicle_device);
			}
		});
//		try {
//			assertEquals(cl2.countRegisteredClass(DepartureCheckModalView.class)
//					.intValue(), 1);
//		} finally {
//			cl2.dispose();
//		}
	}

	/**
	 * ShowEventを受け取ると表示される
	 */
	public void testShowEvent() throws InterruptedException {
		assertFalse(mv.isShown());
		assertNotSame(mv.getVisibility(), View.VISIBLE);

		getInstrumentation().waitForIdleSync();

		assertTrue(mv.isShown());
		assertEquals(mv.getVisibility(), View.VISIBLE);
	}

	public void test到着予定の乗降場名が表示される1() throws InterruptedException {
		callTest到着予定の乗降場名が表示される("乗降場K");	
	}

	public void test到着予定の乗降場名が表示される2() throws InterruptedException {
		callTest到着予定の乗降場名が表示される("乗降場L");	
	}

	public void test到着予定の乗降場名が表示される3() throws InterruptedException {
		callTest到着予定の乗降場名が表示される("乗降場M");	
	}
	
	protected void callTest到着予定の乗降場名が表示される(final String name) throws InterruptedException {
		sa.withWriteLock(new Writer() {
			@Override
			public void write(LocalData status) {
				OperationSchedule os = new OperationSchedule();
				Platform p = new Platform();
				p.setName(name);
				os.setPlatform(p);
				status.remainingOperationSchedules.clear();
				status.remainingOperationSchedules.add(os);
			}
		});
		testShowEvent();
		assertTrue(solo.searchText(name, true));
	}	
	
	public void test到着するボタンを押すとEnterPlatformPhaseEvent通知() throws Exception {
		testShowEvent();
		final CountDownLatch cdl = new CountDownLatch(1);
		solo.clickOnView(solo.getView(R.id.arrival_button));
		cdl.await();
	}

	public void test戻るボタンを押すと消える() throws Exception {
		testShowEvent();
		solo.clickOnView(solo.getView(R.id.arrival_check_close_button));
		getInstrumentation().waitForIdleSync();
		assertFalse(mv.isShown());
	}
}
