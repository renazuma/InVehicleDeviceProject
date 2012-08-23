package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview;

import static org.mockito.Mockito.*;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.modalview.PlatformMemoModalView;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;

public class PlatformMemoModalViewTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	InVehicleDeviceService s;
	PlatformMemoModalView mv;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		s = mock(InVehicleDeviceService.class);
		mv = new PlatformMemoModalView(a, s);
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				a.setContentView(mv);
			}
		});
	}

	public void testShow_NoOperationSchedule() throws Exception {
		when(s.getCurrentOperationSchedule()).thenReturn(
				Optional.<OperationSchedule> absent());
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				mv.show();
			}
		});
		assertFalse(mv.isShown());
	}

	public void testShow_NoPlatform() throws Exception {
		OperationSchedule os = new OperationSchedule();
		when(s.getCurrentOperationSchedule()).thenReturn(Optional.of(os));
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				mv.show();
			}
		});
		assertFalse(mv.isShown());
	}

	public void testShow_NoPlatformMemo() throws Exception {
		OperationSchedule os = new OperationSchedule();
		os.setPlatform(new Platform());
		when(s.getCurrentOperationSchedule()).thenReturn(Optional.of(os));
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				mv.show();
			}
		});
		assertFalse(mv.isShown());
	}

	public void testShow_SuccessAndHideButton() throws Exception {
		callTestShow_Success(true);
	}

	public void testShow_SuccessAndHideMethod() throws Exception {
		callTestShow_Success(false);
	}

	public void callTestShow_Success(Boolean hideButton) throws Exception {
		OperationSchedule os = new OperationSchedule();
		Platform p = new Platform();
		String memo = "こんにちは" + Math.random();
		p.setMemo(memo);
		os.setPlatform(p);
		when(s.getCurrentOperationSchedule()).thenReturn(Optional.of(os));
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				mv.show();
			}
		});
		assertTrue(mv.isShown());
		assertTrue(solo.searchText(memo));

		if (hideButton) {
			solo.clickOnButton("戻る");
		} else {
			runOnUiThreadSync(new Runnable() {
				@Override
				public void run() {
					mv.hide();
				}
			});
		}
		getInstrumentation().waitForIdleSync();
		assertFalse(mv.isShown());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
