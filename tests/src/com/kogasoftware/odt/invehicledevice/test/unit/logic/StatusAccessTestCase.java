package com.kogasoftware.odt.invehicledevice.test.unit.logic;

import java.util.Date;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.kogasoftware.odt.invehicledevice.logic.SharedPreferencesKey;
import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Reader;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.VoidReader;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class StatusAccessTestCase extends EmptyActivityInstrumentationTestCase2 {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * デフォルトコンストラクタ。非チェック例外が発生しないことのみ確認
	 */
	public void testConstructor_1() throws Exception {
		StatusAccess sa = new StatusAccess();
		sa.read(new Reader<Date>() {
			@Override
			public Date read(Status status) {
				return status.createdDate;
			}
		});
		sa.write(new Writer() {
			@Override
			public void write(Status status) {
				status.vehicleNotifications.clear();
			}
		});
	}

	/**
	 * コンストラクタ。clearStatusFileが呼ばれたら内容をクリア
	 */
	public void testConstructor_2() throws Exception {
		// 保存
		StatusAccess sa1 = new StatusAccess(getActivity());
		sa1.write(new Writer() {
			@Override
			public void write(Status status) {
				status.unhandledPassengerRecords.clear();
				status.unhandledPassengerRecords.add(new PassengerRecord());
				status.sendLists.repliedVehicleNotifications.clear();
				status.sendLists.repliedVehicleNotifications
						.add(new VehicleNotification());
			}
		});
		Thread.sleep(500); // 保存されるのを待つ

		// クリアされていないことを確認
		StatusAccess sa2 = new StatusAccess(getActivity());
		sa2.read(new VoidReader() {
			@Override
			public void read(Status status) {
				assertEquals(status.unhandledPassengerRecords.size(), 1);
				assertEquals(
						status.sendLists.repliedVehicleNotifications.size(), 1);
			}
		});

		// クリアされることを確認
		StatusAccess.clearSavedFile();
		StatusAccess sa3 = new StatusAccess(getActivity());
		sa3.read(new VoidReader() {
			@Override
			public void read(Status status) {
				assertTrue(status.unhandledPassengerRecords.isEmpty());
				assertTrue(status.sendLists.repliedVehicleNotifications
						.isEmpty());
			}
		});

		// 復活しないことを確認
		StatusAccess sa4 = new StatusAccess(getActivity());
		sa4.read(new VoidReader() {
			@Override
			public void read(Status status) {
				assertTrue(status.unhandledPassengerRecords.isEmpty());
				assertTrue(status.sendLists.repliedVehicleNotifications
						.isEmpty());
			}
		});
	}

	/**
	 * コンストラクタ。SharedPreferencesKeyのCLEAR_REQUIREDがtrueの場合 内容をクリア
	 */
	public void testConstructor_3() throws Exception {
		// 保存
		StatusAccess.clearSavedFile();
		StatusAccess sa1 = new StatusAccess(getActivity());
		sa1.write(new Writer() {
			@Override
			public void write(Status status) {
				status.remainingOperationSchedules.add(new OperationSchedule());
				status.sendLists.getOnPassengerRecords
						.add(new PassengerRecord());
			}
		});
		Thread.sleep(500); // 保存されるのを待つ

		// クリアされていないことを確認
		StatusAccess sa2 = new StatusAccess(getActivity());
		sa2.read(new VoidReader() {
			@Override
			public void read(Status status) {
				assertEquals(status.remainingOperationSchedules.size(), 1);
				assertEquals(status.sendLists.getOnPassengerRecords.size(), 1);
			}
		});

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		preferences.edit()
				.putBoolean(SharedPreferencesKey.CLEAR_REQUIRED, true).commit();

		// クリアされることを確認
		StatusAccess sa3 = new StatusAccess(getActivity());
		sa3.read(new VoidReader() {
			@Override
			public void read(Status status) {
				assertTrue(status.unhandledPassengerRecords.isEmpty());
				assertTrue(status.sendLists.repliedVehicleNotifications
						.isEmpty());
			}
		});

		// SharedPreferencesKey.CLEAR_REQUIREDがfalseになっていることを確認
		assertFalse(preferences.getBoolean(SharedPreferencesKey.CLEAR_REQUIRED,
				true));

		// データが復活しないことを確認
		StatusAccess sa4 = new StatusAccess(getActivity());
		sa4.read(new VoidReader() {
			@Override
			public void read(Status status) {
				assertTrue(status.unhandledPassengerRecords.isEmpty());
				assertTrue(status.sendLists.repliedVehicleNotifications
						.isEmpty());
			}
		});
	}
}
