package com.kogasoftware.odt.invehicledevice.test.unit.logic;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Reader;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.VoidReader;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.test.common.MockActivityUnitTestCase;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class StatusAccessTestCase extends MockActivityUnitTestCase {
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
		sa.read(new Reader<Boolean>() {
			@Override
			public Boolean read(Status status) {
				return status.stopped;
			}
		});
		sa.write(new Writer() {
			@Override
			public void write(Status status) {
				status.stopped = true;
			}
		});
	}

	/**
	 * コンストラクタ。clear引数が指定されたら内容をクリア
	 */
	public void testConstructor_2() throws Exception {
		// 保存
		StatusAccess sa1 = new StatusAccess(getActivity(), true);
		sa1.write(new Writer() {
			@Override
			public void write(Status status) {
				status.unhandledPassengerRecords.add(new PassengerRecord());
				status.sendLists.repliedVehicleNotifications
						.add(new VehicleNotification());
			}
		});
		Thread.sleep(500); // 保存されるのを待つ

		// クリアされていないことを確認
		StatusAccess sa2 = new StatusAccess(getActivity(), false);
		sa2.read(new VoidReader() {
			@Override
			public void read(Status status) {
				assertEquals(status.unhandledPassengerRecords.size(), 1);
				assertEquals(
						status.sendLists.repliedVehicleNotifications.size(), 1);
			}
		});

		// クリアされることを確認
		StatusAccess sa3 = new StatusAccess(getActivity(), true);
		sa3.read(new VoidReader() {
			@Override
			public void read(Status status) {
				assertTrue(status.unhandledPassengerRecords.isEmpty());
				assertTrue(status.sendLists.repliedVehicleNotifications
						.isEmpty());
			}
		});

		// 一度クリアされたら、falseを指定してもデータが復活しないことを確認
		StatusAccess sa4 = new StatusAccess(getActivity(), false);
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
	 * コンストラクタ。SharedPreferenceのCLEAR_REQUIRED_SHARED_PREFERENCE_KEYがtrueの場合
	 * 内容をクリア
	 */
	public void testConstructor_3() throws Exception {
		// 保存
		StatusAccess sa1 = new StatusAccess(getActivity(), true);
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
		StatusAccess sa2 = new StatusAccess(getActivity(), false);
		sa2.read(new VoidReader() {
			@Override
			public void read(Status status) {
				assertEquals(status.remainingOperationSchedules.size(), 1);
				assertEquals(status.sendLists.getOnPassengerRecords.size(), 1);
			}
		});

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		preferences
				.edit()
				.putBoolean(StatusAccess.CLEAR_REQUIRED_SHARED_PREFERENCE_KEY,
						true).commit();

		// クリアされることを確認
		StatusAccess sa3 = new StatusAccess(getActivity(), false);
		sa3.read(new VoidReader() {
			@Override
			public void read(Status status) {
				assertTrue(status.unhandledPassengerRecords.isEmpty());
				assertTrue(status.sendLists.repliedVehicleNotifications
						.isEmpty());
			}
		});

		// CLEAR_REQUIRED_SHARED_PREFERENCE_KEYがfalseになっていることを確認
		assertFalse(preferences.getBoolean(
				StatusAccess.CLEAR_REQUIRED_SHARED_PREFERENCE_KEY, true));

		// 一度クリアされたら、falseでもデータが復活しないことを確認
		StatusAccess sa4 = new StatusAccess(getActivity(), false);
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
