package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview;

import java.util.concurrent.CountDownLatch;

import android.app.Activity;
import android.view.View;

import com.google.common.base.Function;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Writer;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.modalview.DepartureCheckModalView;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;
import static org.mockito.Mockito.*;

public class DepartureCheckModalViewTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	InVehicleDeviceService s;
	LocalDataSource sa;
	DepartureCheckModalView mv;
	Activity a;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		a = getActivity();
		s = mock(InVehicleDeviceService.class);
		sa = new LocalDataSource(getActivity());
		mv = new DepartureCheckModalView(a, s);

		sa.withWriteLock(new Writer() { // TODO もっとスマートにする
			@Override
			public void write(LocalData status) {
				OperationSchedule os1 = new OperationSchedule();
				OperationSchedule os2 = new OperationSchedule();
				os1.setPlatform(new Platform());
				os2.setPlatform(new Platform());
				status.remainingOperationSchedules.clear();
				status.remainingOperationSchedules.add(os1);
				status.remainingOperationSchedules.add(os2);
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

//		cl.postEvent(new DepartureCheckModalView.ShowEvent(
//				new ReservationArrayAdapter(getInstrumentation()
//						.getContext(), cl)));
		getInstrumentation().waitForIdleSync();

		assertTrue(mv.isShown());
		assertEquals(mv.getVisibility(), View.VISIBLE);
	}

	public void test出発するボタンを押すとEnterDrivePhaseEvent通知() throws Exception {
		testShowEvent();
		final CountDownLatch cdl = new CountDownLatch(1);
		solo.clickOnView(solo.getView(R.id.departure_button));
		cdl.await();
	}

	public void test戻るボタンを押すと消える() throws Exception {
		testShowEvent();
		solo.clickOnView(solo.getView(R.id.departure_check_close_button));
		getInstrumentation().waitForIdleSync();
		assertFalse(mv.isShown());
	}
}
