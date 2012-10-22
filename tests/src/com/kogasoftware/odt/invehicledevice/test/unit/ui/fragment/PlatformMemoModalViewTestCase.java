package com.kogasoftware.odt.invehicledevice.test.unit.ui.fragment;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Platform;

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
		getInstrumentation().waitForIdleSync();
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
		getInstrumentation().waitForIdleSync();
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
		getInstrumentation().waitForIdleSync();
		assertFalse(mv.isShown());
	}

	private void assertShow(String memo) throws Exception {
		OperationSchedule os = new OperationSchedule();
		Platform p = new Platform();
		p.setMemo(memo);
		os.setPlatform(p);
		when(s.getCurrentOperationSchedule()).thenReturn(Optional.of(os));
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				mv.show();
			}
		});

		TestUtil.assertShow(mv);
		assertTrue(solo.searchText(memo));
	}

	public void testShow() throws Exception {
		assertShow("こんにちは");
	}

	public void testHide() throws Exception {
		assertShow("こんばんわ");
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				mv.hide();
			}
		});
		getInstrumentation().waitForIdleSync();
		assertFalse(mv.isShown());
	}

	public void testBackButton() throws Exception {
		assertShow("プラットフォームメモ");
		solo.clickOnButton("戻る");
		getInstrumentation().waitForIdleSync();
		assertFalse(mv.isShown());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}

