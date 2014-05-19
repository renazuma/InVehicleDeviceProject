package com.kogasoftware.odt.invehicledevice.ui.fragment;

import static org.mockito.Mockito.*;
import android.app.Fragment;
import android.app.FragmentManager;
import android.widget.FrameLayout;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage;
import com.kogasoftware.odt.invehicledevice.testutil.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.testutil.TestUtil;
import com.kogasoftware.odt.invehicledevice.ui.fragment.PlatformMemoFragment;
import com.kogasoftware.odt.invehicledevice.apiclient.InVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.UnmergedOperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Platform;
import com.kogasoftware.odt.invehicledevice.R;

public class PlatformMemoFragmentTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	InVehicleDeviceService s;
	InVehicleDeviceApiClient ac;
	LocalData ld;
	EventDispatcher ed;
	Fragment f;
	Platform p;
	UnmergedOperationSchedule os;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ld = new LocalData();
		ed = mock(EventDispatcher.class);
		ac = mock(InVehicleDeviceApiClient.class);
		when(ac.withSaveOnClose()).thenReturn(ac);
		s = mock(InVehicleDeviceService.class);
		when(s.getLocalStorage()).thenReturn(new LocalStorage(ld));
		when(s.getEventDispatcher()).thenReturn(ed);
		when(s.getApiClient()).thenReturn(ac);
		a.setService(s);

		p = new Platform();
		os = new UnmergedOperationSchedule();
		os.setPlatform(p);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testShowAndHide() throws Throwable {
		assertShowAndHide("メモ2");
		assertShowAndHide("メモめも");
		assertShowAndHide("メメモモ");
	}

	public void assertShowAndHide(String memo) throws Throwable {
		p.setMemo(memo);

		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				int id = 12345;
				FrameLayout fl = new FrameLayout(a);
				fl.setId(id);
				a.setContentView(fl);
				f = PlatformMemoFragment.newInstance(OperationSchedule.create(Lists.newArrayList(os)).get(0));
				FragmentManager fm = a.getFragmentManager();
				fm.beginTransaction().add(id, f).commit();
			}
		});
		TestUtil.assertShow(f);
		assertTrue(solo.searchText(p.getMemo()));
		solo.clickOnView(solo.getView(R.id.platform_memo_close_button));
		TestUtil.assertHide(f);
	}
}
