package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Writer;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.OperationScheduleLogic;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.ui.modalview.ScheduleModalView;
import com.kogasoftware.odt.webapi.model.OperationRecord;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;

public class ScheduleModalViewTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	InVehicleDeviceService s;
	ScheduleModalView mv;
	OperationSchedule os0 = new OperationSchedule();
	OperationSchedule os1 = new OperationSchedule();
	OperationSchedule os2 = new OperationSchedule();
	OperationSchedule os3 = new OperationSchedule();
	OperationSchedule os4 = new OperationSchedule();
	OperationSchedule os5 = new OperationSchedule();
	OperationSchedule os6 = new OperationSchedule();
	OperationSchedule os7 = new OperationSchedule();
	OperationSchedule os8 = new OperationSchedule();
	OperationSchedule os9 = new OperationSchedule();
	OperationRecord or0 = new OperationRecord();
	OperationRecord or1 = new OperationRecord();
	OperationRecord or2 = new OperationRecord();
	OperationRecord or3 = new OperationRecord();
	OperationRecord or4 = new OperationRecord();
	OperationRecord or5 = new OperationRecord();
	OperationRecord or6 = new OperationRecord();
	OperationRecord or7 = new OperationRecord();
	OperationRecord or8 = new OperationRecord();
	OperationRecord or9 = new OperationRecord();
	List<OperationSchedule> oss;
	Optional<OperationSchedule> cos;
	OperationScheduleLogic osl;
	LocalDataSource lds;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		lds = new LocalDataSource(getInstrumentation().getContext());
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.operationSchedules.clear();
			}
		});

		s = mock(InVehicleDeviceService.class);
		when(s.getLocalDataSource()).thenReturn(lds);
		mv = new ScheduleModalView(a, s);

		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				a.setContentView(mv);
			}
		});

		os0.setId(100);
		os1.setId(101);
		os2.setId(102);
		os3.setId(103);
		os4.setId(104);
		os5.setId(105);
		os6.setId(106);
		os7.setId(107);
		os8.setId(108);
		os9.setId(109);

		os0.setOperationRecord(or0);
		os1.setOperationRecord(or1);
		os2.setOperationRecord(or2);
		os3.setOperationRecord(or3);
		os4.setOperationRecord(or4);
		os5.setOperationRecord(or5);
		os6.setOperationRecord(or6);
		os7.setOperationRecord(or7);
		os8.setOperationRecord(or8);
		os9.setOperationRecord(or9);

		oss = Lists.newLinkedList();
		cos = Optional.of(os0);
		osl = new OperationScheduleLogic(s);

		when(s.getOperationSchedules()).thenAnswer(
				new Answer<List<OperationSchedule>>() {
					@Override
					public List<OperationSchedule> answer(
							InvocationOnMock invocation) throws Throwable {
						return osl.getOperationSchedules();
					}
				});
		when(s.getRemainingOperationSchedules()).thenAnswer(
				new Answer<List<OperationSchedule>>() {
					@Override
					public List<OperationSchedule> answer(
							InvocationOnMock invocation) throws Throwable {
						return osl.getRemainingOperationSchedules();
					}
				});
		when(s.getCurrentOperationSchedule()).thenAnswer(
				new Answer<Optional<OperationSchedule>>() {
					@Override
					public Optional<OperationSchedule> answer(
							InvocationOnMock invocation) throws Throwable {
						return osl.getCurrentOperationSchedule();
					}
				});
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testShow0() throws Exception {
		Platform p0 = new Platform();
		p0.setName("駅A");
		os0.setPlatform(p0);
		oss.add(os0);
		assertShow();
		assertTrue(solo.searchText(p0.getName(), true));
	}

	public void testShow1() throws Exception {
		Platform p0 = new Platform();
		p0.setName("駅X");
		Platform p1 = new Platform();
		p1.setName("駅Y");
		os0.setPlatform(p0);
		os1.setPlatform(p1);
		oss.add(os0);
		oss.add(os1);
		assertShow();
		assertTrue(solo.searchText(p0.getName(), true));
		assertTrue(solo.searchText(p1.getName(), true));
	}

	public void testShow2() throws Exception {
		Platform p0 = new Platform();
		p0.setName("上野駅A");
		Platform p1 = new Platform();
		p1.setName("北千住駅B");
		Platform p2 = new Platform();
		p2.setName("土浦駅C");
		os0.setPlatform(p0);
		os1.setPlatform(p1);
		os2.setPlatform(p2);
		oss.add(os0);
		oss.add(os1);
		oss.add(os2);
		assertShow();
		assertTrue(solo.searchText(p0.getName(), true));
		assertTrue(solo.searchText(p1.getName(), true));
		assertTrue(solo.searchText(p2.getName(), true));
	}

	public void testAutoScroll0() throws Exception {
		Platform p0 = new Platform();
		Platform p2 = new Platform();
		p0.setName("スクロールして隠れる駅");
		p2.setName("スクロールして最上部に来る駅");
		os0.setPlatform(p0);
		os2.setPlatform(p2);
		or0.setDepartedAt(new Date());
		or1.setDepartedAt(new Date());

		oss.add(os0);
		oss.add(os1);
		oss.add(os2);
		oss.add(os3);
		oss.add(os4);
		oss.add(os5);
		oss.add(os6);
		oss.add(os7);
		oss.add(os8);
		oss.add(os9);

		assertShow();
		assertFalse(solo.searchText(p0.getName(), true));

		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				mv.hide();
			}
		});
		TestUtil.assertHide(mv);
		assertShow();
		assertTrue(solo.searchText(p2.getName(), true));
	}

	public void testAutoScroll1() throws Exception {
		Platform p4 = new Platform();
		Platform p7 = new Platform();
		p4.setName("スクロールして隠れる駅");
		p7.setName("スクロールして最上部に来る駅");
		os4.setPlatform(p4);
		os7.setPlatform(p7);
		or0.setDepartedAt(new Date());
		or1.setDepartedAt(new Date());
		or2.setDepartedAt(new Date());
		or3.setDepartedAt(new Date());
		or4.setDepartedAt(new Date());
		or5.setDepartedAt(new Date());
		or6.setDepartedAt(new Date());

		oss.add(os0);
		oss.add(os1);
		oss.add(os2);
		oss.add(os3);
		oss.add(os4);
		oss.add(os5);
		oss.add(os6);
		oss.add(os7);
		oss.add(os8);
		oss.add(os9);

		assertShow();

		assertFalse(solo.searchText(p4.getName(), true));

		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				mv.hide();
			}
		});
		TestUtil.assertHide(mv);
		assertShow();
		assertTrue(solo.searchText(p7.getName(), true));
	}

	public void xtestAutoScroll2() throws Exception {
		Platform p7 = new Platform();
		Platform p9 = new Platform();
		p7.setName("下限のため隠れない駅");
		p9.setName("下限のため最下部の駅");
		os7.setPlatform(p7);
		os9.setPlatform(p9);
		or0.setDepartedAt(new Date());
		or1.setDepartedAt(new Date());
		or2.setDepartedAt(new Date());
		or3.setDepartedAt(new Date());
		or4.setDepartedAt(new Date());
		or5.setDepartedAt(new Date());
		or6.setDepartedAt(new Date());
		or7.setDepartedAt(new Date());
		or8.setDepartedAt(new Date());

		oss.add(os0);
		oss.add(os1);
		oss.add(os2);
		oss.add(os3);
		oss.add(os4);
		oss.add(os5);
		oss.add(os6);
		oss.add(os7);
		oss.add(os8);
		oss.add(os9);

		assertShow();

		assertTrue(solo.searchText(p7.getName(), true));

		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				mv.hide();
			}
		});
		TestUtil.assertHide(mv);
		assertShow();
		assertTrue(solo.searchText(p9.getName(), true));
	}

	public void testHide() throws Exception {
		Platform p0 = new Platform();
		p0.setName("駅A");
		os0.setPlatform(p0);
		oss.add(os0);

		assertShow();
		assertTrue(mv.isShown());

		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				mv.hide();
			}
		});

		TestUtil.assertHide(mv);
	}

	protected void assertShow() throws Exception {
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.operationSchedules.addAll(oss);
			}
		});

		assertFalse(mv.isShown());

		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				mv.show();
			}
		}, 50000);

		TestUtil.assertShow(mv);
	}
}
