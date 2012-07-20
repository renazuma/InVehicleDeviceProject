package com.kogasoftware.odt.invehicledevice.test.unit.logic;

import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Reader;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.VoidReader;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Writer;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKey;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class LocalDataSourceTestCase extends EmptyActivityInstrumentationTestCase2 {
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
		LocalDataSource lds = new LocalDataSource();
		lds.withReadLock(new Reader<Date>() {
			@Override
			public Date read(LocalData ld) {
				return ld.updatedDate;
			}
		});
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData ld) {
				ld.vehicleNotifications.clear();
			}
		});
		lds.close();
	}

	/**
	 * コンストラクタ。clearStatusFileが呼ばれたら内容をクリア
	 */
	public void testConstructor_2() throws Exception {
		Context c = getInstrumentation().getTargetContext();
		// 保存
		LocalDataSource lds1 = new LocalDataSource(c);
		lds1.withWriteLock(new Writer() {
			@Override
			public void write(LocalData ld) {
				ld.passengerRecords.clear();
				ld.passengerRecords.add(new PassengerRecord());
				ld.repliedVehicleNotifications.clear();
				ld.repliedVehicleNotifications
						.add(new VehicleNotification());
			}
		});
		lds1.close();
		Thread.sleep(500); // 保存されるのを待つ

		// クリアされていないことを確認
		LocalDataSource lds2 = new LocalDataSource(c);
		lds2.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData ld) {
				assertEquals(1, ld.passengerRecords.size());
				assertEquals(1, ld.repliedVehicleNotifications.size());
			}
		});
		lds2.close();

		// クリアされることを確認
		LocalDataSource.clearSavedFile();
		LocalDataSource lds3 = new LocalDataSource(c);
		lds3.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData ld) {
				assertTrue(ld.passengerRecords.isEmpty());
				assertTrue(ld.repliedVehicleNotifications.isEmpty());
			}
		});
		lds3.close();

		// 復活しないことを確認
		LocalDataSource lds4 = new LocalDataSource(c);
		lds4.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData ld) {
				assertTrue(ld.passengerRecords.isEmpty());
				assertTrue(ld.repliedVehicleNotifications.isEmpty());
			}
		});
		lds4.close();
	}

	/**
	 * コンストラクタ。SharedPreferencesKeyのCLEAR_REQUIREDがtrueの場合 内容をクリア
	 */
	public void testConstructor_3() throws Exception {
		Context c = getInstrumentation().getTargetContext();

		// 保存
		LocalDataSource.clearSavedFile();
		LocalDataSource lds1 = new LocalDataSource(c);
		lds1.withWriteLock(new Writer() {
			@Override
			public void write(LocalData ld) {
				ld.remainingOperationSchedules.add(new OperationSchedule());
			}
		});
		lds1.close();
		Thread.sleep(500); // 保存されるのを待つ

		// クリアされていないことを確認
		LocalDataSource lds2 = new LocalDataSource(c);
		lds2.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData ld) {
				assertEquals(ld.remainingOperationSchedules.size(), 1);
			}
		});
		lds2.close();

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(c);
		preferences.edit()
				.putBoolean(SharedPreferencesKey.CLEAR_STATUS_BACKUP, true)
				.commit();
		// クリアされることを確認
		LocalDataSource lds3 = new LocalDataSource(c);
		lds3.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData ld) {
				assertTrue(ld.remainingOperationSchedules.isEmpty());
			}
		});
		lds3.close();

		// SharedPreferencesKey.CLEAR_REQUIREDがfalseになっていることを確認
		assertFalse(preferences.getBoolean(
				SharedPreferencesKey.CLEAR_STATUS_BACKUP, true));

		// データが復活しないことを確認
		LocalDataSource lds4 = new LocalDataSource(c);
		lds4.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData ld) {
				assertTrue(ld.remainingOperationSchedules.isEmpty());
			}
		});
		lds4.close();
	}
}
