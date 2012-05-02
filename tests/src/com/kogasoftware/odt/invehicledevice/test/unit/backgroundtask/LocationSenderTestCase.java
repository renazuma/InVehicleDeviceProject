package com.kogasoftware.odt.invehicledevice.test.unit.backgroundtask;

import com.kogasoftware.odt.invehicledevice.backgroundtask.LocationSender;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.datasource.DummyDataSource;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;

public class LocationSenderTestCase extends EmptyActivityInstrumentationTestCase2 {
	CommonLogic cl;
	DummyDataSource dds;
	LocationSender ls;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dds = new DummyDataSource();
		DataSourceFactory.setInstance(dds);
		cl = new CommonLogic(getActivity(), getActivityHandler());
		ls = new LocationSender(cl);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * 現在のServiceUnitStatusLogをサーバーへ送信
	 */
	public void testRun_1() throws Exception {
		final ServiceUnitStatusLog l = new ServiceUnitStatusLog();
		cl.getStatusAccess().write(new Writer() {
			@Override
			public void write(Status status) {
				status.serviceUnitStatusLogLocationEnabled = true;
				status.serviceUnitStatusLog = l;
			}
		});
		assertTrue(dds.sendServiceUnitStatusLogArgs.isEmpty());
		ls.run();
		assertEquals(dds.sendServiceUnitStatusLogArgs.size(), 1);
		assertEquals(l, dds.sendServiceUnitStatusLogArgs.get(0));
	}

	/**
	 * serviceUnitStatusLogLocationEnabledがfalseの場合は送信しない
	 */
	public void testRun_2() throws Exception {
		final ServiceUnitStatusLog l = new ServiceUnitStatusLog();
		assertTrue(dds.sendServiceUnitStatusLogArgs.isEmpty());
		cl.getStatusAccess().write(new Writer() {
			@Override
			public void write(Status status) {
				status.serviceUnitStatusLogLocationEnabled = false;
			}
		});
		assertTrue(dds.sendServiceUnitStatusLogArgs.isEmpty());
		cl.getStatusAccess().write(new Writer() {
			@Override
			public void write(Status status) {
				status.serviceUnitStatusLogLocationEnabled = true;
				status.serviceUnitStatusLog = l;
			}
		});
		ls.run();
		assertEquals(dds.sendServiceUnitStatusLogArgs.size(), 1);
		assertEquals(l, dds.sendServiceUnitStatusLogArgs.get(0));
		cl.getStatusAccess().write(new Writer() {
			@Override
			public void write(Status status) {
				status.serviceUnitStatusLogLocationEnabled = false;
			}
		});
		ls.run();
		assertEquals(dds.sendServiceUnitStatusLogArgs.size(), 1);
	}
}