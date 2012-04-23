package com.kogasoftware.odt.invehicledevice.test;

import java.util.Date;
import java.util.List;

import org.json.JSONException;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceActivity;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.datasource.DummyDataSource;
import com.kogasoftware.odt.invehicledevice.datasource.ScheduleChangedTestDataSource;
import com.kogasoftware.odt.invehicledevice.logic.Logic;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.ReservationCandidate;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class ScheduleChangedTestCase extends
ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;

	public ScheduleChangedTestCase() {
		super("com.kogasoftware.odt.invehicledevice",
				InVehicleDeviceActivity.class);
		DataSourceFactory.setInstance(new ScheduleChangedTestDataSource());
		Logic.clearStatusFile();
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
		sync();
	}
	
	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		super.tearDown();
	}

	private void sync() throws InterruptedException {
		getInstrumentation().waitForIdleSync();
		Thread.sleep(500);
	}

	public void test01_テスト起動() throws Exception {
		assertTrue(solo.searchText("乗降場A"));
	
		solo.clickOnButton("到着しました");
		sync();
		solo.clickOnView(solo.getView(R.id.change_phase_button));
		sync();
		solo.clickOnView(solo.getView(R.id.start_button));
		sync();
	
		assertFalse(solo.searchText("乗降場A"));
		assertTrue(solo.searchText("乗降場B"));
		Thread.sleep(50 * 1000); // 通知を待つ
		
		assertFalse(solo.searchText("乗降場B"));
		assertTrue(solo.searchText("乗降場C"));
		
		solo.clickOnButton("到着しました");
		sync();
		solo.clickOnView(solo.getView(R.id.change_phase_button));
		sync();
		solo.clickOnView(solo.getView(R.id.start_button));
		sync();
		
		assertFalse(solo.searchText("乗降場C"));
		assertTrue(solo.searchText("乗降場B"));
	}
}
