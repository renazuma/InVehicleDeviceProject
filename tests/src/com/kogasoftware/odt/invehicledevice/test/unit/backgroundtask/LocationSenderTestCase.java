package com.kogasoftware.odt.invehicledevice.test.unit.backgroundtask;

import java.util.concurrent.atomic.AtomicReference;

import com.kogasoftware.odt.invehicledevice.backgroundtask.BackgroundTask;
import com.kogasoftware.odt.invehicledevice.backgroundtask.LocationSender;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.datasource.DummyDataSource;

public class LocationSenderTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	StatusAccess sa;
	CommonLogic cl;
	DummyDataSource dds;
	LocationSender ls;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dds = new DummyDataSource();
		DataSourceFactory.setInstance(dds);
		sa = new StatusAccess(getActivity());
		cl = new CommonLogic(getActivity(), getActivityHandler(), sa);
		ls = new LocationSender(cl);
	}

	@Override
	protected void tearDown() throws Exception {
		if (cl != null) {
			cl.dispose();
		}
		super.tearDown();
	}

	public void testBackgroundTaskによってUiEventBusに登録される() throws Exception {
		final AtomicReference<BackgroundTask> bt = new AtomicReference<BackgroundTask>();
		Thread t = new Thread() {
			@Override
			public void run() {
				bt.set(new BackgroundTask(cl,
						getInstrumentation().getContext(), sa));
				bt.get().loop();
			}
		};
		t.start();
		Thread.sleep(1000);
		assertEquals(cl.countRegisteredClass(LocationSender.class).intValue(),
				1);
		bt.get().quit();
	}

	/**
	 * 現在のServiceUnitStatusLogをサーバーへ送信
	 */
	public void testRun_1() throws Exception {
		sa.write(new Writer() {
			@Override
			public void write(Status status) {
				status.serviceUnitStatusLogLocationEnabled = true;
				status.serviceUnitStatusLog.setTemperature(500);
			}
		});
		assertTrue(dds.sendServiceUnitStatusLogArgs.isEmpty());
		ls.run();
		assertEquals(dds.sendServiceUnitStatusLogArgs.size(), 1);
		assertEquals(dds.sendServiceUnitStatusLogArgs.get(0).getTemperature()
				.get().intValue(), 500);
	}

	/**
	 * serviceUnitStatusLogLocationEnabledがfalseの場合は送信しない
	 */
	public void testRun_2() throws Exception {
		assertTrue(dds.sendServiceUnitStatusLogArgs.isEmpty());
		sa.write(new Writer() {
			@Override
			public void write(Status status) {
				status.serviceUnitStatusLogLocationEnabled = false;
			}
		});
		assertTrue(dds.sendServiceUnitStatusLogArgs.isEmpty());
		sa.write(new Writer() {
			@Override
			public void write(Status status) {
				status.serviceUnitStatusLogLocationEnabled = true;
				status.serviceUnitStatusLog.setOrientation(123);
			}
		});
		ls.run();
		assertEquals(dds.sendServiceUnitStatusLogArgs.size(), 1);
		assertEquals(dds.sendServiceUnitStatusLogArgs.get(0).getOrientation()
				.get().intValue(), 123);
		sa.write(new Writer() {
			@Override
			public void write(Status status) {
				status.serviceUnitStatusLogLocationEnabled = false;
			}
		});
		ls.run();
		assertEquals(dds.sendServiceUnitStatusLogArgs.size(), 1);
	}
}