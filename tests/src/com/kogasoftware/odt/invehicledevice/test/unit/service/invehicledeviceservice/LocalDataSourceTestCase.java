package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.time.DateUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Reader;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.VoidReader;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Writer;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKeys;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class LocalDataSourceTestCase extends AndroidTestCase {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TestUtil.clearStatus();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		Thread.sleep(200); // 保存されるのを待つ
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
	public void testReadWriteClear1() throws Exception {
		Context c = getContext();
		// 保存
		LocalDataSource lds1 = new LocalDataSource(c);
		lds1.withWriteLock(new Writer() {
			@Override
			public void write(LocalData ld) {
				ld.passengerRecords.clear();
				ld.passengerRecords.add(new PassengerRecord());
				ld.repliedVehicleNotifications.clear();
				ld.repliedVehicleNotifications.add(new VehicleNotification());
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
	public void testReadWriteClear() throws Exception {
		Context c = getContext();

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
				.putBoolean(SharedPreferencesKeys.CLEAR_STATUS_BACKUP, true)
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
				SharedPreferencesKeys.CLEAR_STATUS_BACKUP, true));

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

	/**
	 * 読み書きロックのロジックがきちんと動いているかどうかのチェック
	 */
	public void testReadWriteLock() throws Exception {
		final CyclicBarrier cb = new CyclicBarrier(200);
		final LocalDataSource lds = new LocalDataSource(getContext());
		final AtomicBoolean working = new AtomicBoolean(false);
		final AtomicInteger numConcurrentAccesses = new AtomicInteger(0);
		final AtomicBoolean error = new AtomicBoolean(false);

		// 同時アクセスを行うRunnable
		final Runnable readAccess = new Runnable() {
			@Override
			public void run() {
				try {
					if (working.getAndSet(true)) {
						numConcurrentAccesses.addAndGet(1);
					}
					Thread.sleep(300);
				} catch (InterruptedException e) {
				} finally {
					working.set(false);
				}
			}
		};

		// Reader<T>を使い同時アクセスを行うRunnable
		final Runnable useReader = new Runnable() {
			@Override
			public void run() {
				try {
					cb.await();
				} catch (InterruptedException e) {
					return;
				} catch (BrokenBarrierException e) {
					return;
				}
				lds.withReadLock(new Reader<Integer>() {
					@Override
					public Integer read(LocalData localData) {
						readAccess.run();
						return 0;
					}
				});
			}
		};

		// VoidReaderを使い同時アクセスを行うRunnable
		final Runnable useVoidReader = new Runnable() {
			@Override
			public void run() {
				try {
					cb.await();
				} catch (InterruptedException e) {
					return;
				} catch (BrokenBarrierException e) {
					return;
				}
				lds.withReadLock(new Reader<Integer>() {
					@Override
					public Integer read(LocalData localData) {
						readAccess.run();
						return 0;
					}
				});
			}
		};

		// Writerを使い同時アクセスを行うRunnable
		final Runnable useWriter = new Runnable() {
			@Override
			public void run() {
				try {
					cb.await();
				} catch (InterruptedException e) {
					error.set(true);
					return;
				} catch (BrokenBarrierException e) {
					error.set(true);
					return;
				}
				lds.withWriteLock(new Writer() {
					@Override
					public void write(LocalData localData) {
						try {
							if (working.getAndSet(true)) {
								// 書きこみロック時に他のスレッドが動いていたらエラー
								error.set(true);
							}
							Thread.sleep(300);
						} catch (InterruptedException e) {
						} finally {
							working.set(false);
						}
					}
				});
			}
		};

		// 同時アクセス用にスレッド起動
		Thread[] threads = new Thread[cb.getParties()];
		for (Integer i = 0; i < threads.length; ++i) {
			if (i % 15 == 0) {
				threads[i] = new Thread(useWriter);
			} else if (i % 2 == 0) {
				threads[i] = new Thread(useReader);
			} else {
				threads[i] = new Thread(useVoidReader);
			}
			threads[i].start();
		}
		for (Integer i = 0; i < threads.length; ++i) {
			threads[i].join();
		}

		lds.close();

		// 同時アクセスが頻繁に発生しているか
		assertTrue(threads.length * 0.7 + " < " + numConcurrentAccesses.get(),
				threads.length * 0.7 < numConcurrentAccesses.get());
		// その他エラーが発生していないか
		assertFalse(error.get());
	}

	/**
	 * 指定した周期でデータの永続化が発生しているか
	 */
	public void testSavePeriod() throws InterruptedException {
		Integer savePeriod = 1000;

		// 永続化されるかの確認
		LocalDataSource lds = new LocalDataSource(getContext(), savePeriod);
		final VehicleNotification vn = new VehicleNotification();
		vn.setId(1);
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.vehicleNotifications.clear();
				localData.vehicleNotifications.add(SerializationUtils.clone(vn));
			}
		});
		Thread.sleep(savePeriod / 5);
		LocalDataSource read1 = new LocalDataSource(getContext());
		Integer id1 = read1.withReadLock(new Reader<Integer>() {
			@Override
			public Integer read(LocalData localData) {
				return localData.vehicleNotifications.get(0).getId();
			}
		});
		assertEquals(1, id1.intValue());

		// savePeriod経過していない状態では、永続化されないことを確認
		vn.setId(2);
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.vehicleNotifications.clear();
				localData.vehicleNotifications.add(SerializationUtils.clone(vn));
			}
		});
		Thread.sleep(savePeriod / 5);
		LocalDataSource read2 = new LocalDataSource(getContext());
		Integer id2 = read2.withReadLock(new Reader<Integer>() {
			@Override
			public Integer read(LocalData localData) {
				return localData.vehicleNotifications.get(0).getId();
			}
		});
		assertEquals(1, id2.intValue());

		// savePeriodが経過すると、永続化されることを確認
		Thread.sleep(savePeriod);
		LocalDataSource read3 = new LocalDataSource(getContext());
		Integer id3 = read3.withReadLock(new Reader<Integer>() {
			@Override
			public Integer read(LocalData localData) {
				return localData.vehicleNotifications.get(0).getId();
			}
		});
		assertEquals(2, id3.intValue());

		lds.close();
		read1.close();
		read2.close();
		read3.close();
	}

	/**
	 * 新運行スケジュール受信時刻をまたいだらデータがクリアされるかのチェック
	 */
	public void testNewSchedule() throws Exception {
		Integer s = 300;

		// 日付を指定
		Date d = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		d = DateUtils.setHours(d,
				InVehicleDeviceService.NEW_SCHEDULE_DOWNLOAD_HOUR);
		d = DateUtils.setMinutes(d,
				InVehicleDeviceService.NEW_SCHEDULE_DOWNLOAD_MINUTE);
		d = DateUtils.addSeconds(d, -10);
		InVehicleDeviceService.setMockDate(d);

		LocalDataSource lds = new LocalDataSource(getContext());
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.operationScheduleInitializedSign.release();
			}
		});
		lds.close();
		Thread.sleep(s);

		// 新運行スケジュール受信時刻をまたいでいない場合はそのまま
		lds = new LocalDataSource(getContext());
		Integer p1 = lds.withReadLock(new Reader<Integer>() {
			@Override
			public Integer read(LocalData localData) {
				return localData.operationScheduleInitializedSign
						.availablePermits();
			}
		});
		lds.close();
		Thread.sleep(s);
		assertEquals(1, p1.intValue());

		// 新運行スケジュール受信時刻をまたいだばあいはクリアさせる
		InVehicleDeviceService.setMockDate(DateUtils.addSeconds(d, 10));
		lds = new LocalDataSource(getContext());
		Integer p2 = lds.withReadLock(new Reader<Integer>() {
			@Override
			public Integer read(LocalData localData) {
				return localData.operationScheduleInitializedSign
						.availablePermits();
			}
		});
		lds.close();
		Thread.sleep(s);
		assertEquals(0, p2.intValue());
	}
}
