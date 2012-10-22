package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import junitx.framework.ComparableAssert;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.time.DateUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.VehicleNotificationStatus;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.BackgroundReader;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.BackgroundWriter;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.Reader;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.VoidReader;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.Writer;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKeys;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;

public class LocalStorageTestCase extends AndroidTestCase {
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
		LocalStorage lds = new LocalStorage();
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
		LocalStorage lds1 = new LocalStorage(c);
		lds1.withWriteLock(new Writer() {
			@Override
			public void write(LocalData ld) {
				ld.passengerRecords.clear();
				ld.passengerRecords.add(new PassengerRecord());
				ld.vehicleNotifications.clear();
				ld.vehicleNotifications.put(VehicleNotificationStatus.REPLIED,
						new VehicleNotification());
			}
		});
		lds1.close();
		Thread.sleep(500); // 保存されるのを待つ

		// クリアされていないことを確認
		LocalStorage lds2 = new LocalStorage(c);
		lds2.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData ld) {
				assertEquals(1, ld.passengerRecords.size());
				assertEquals(
						1,
						ld.vehicleNotifications.get(
								VehicleNotificationStatus.REPLIED).size());
			}
		});
		lds2.close();

		// クリアされることを確認
		LocalStorage.clearSavedFile();
		LocalStorage lds3 = new LocalStorage(c);
		lds3.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData ld) {
				assertTrue(ld.passengerRecords.isEmpty());

				assertTrue(ld.vehicleNotifications.get(
						VehicleNotificationStatus.REPLIED).isEmpty());
			}
		});
		lds3.close();

		// 復活しないことを確認
		LocalStorage lds4 = new LocalStorage(c);
		lds4.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData ld) {
				assertTrue(ld.passengerRecords.isEmpty());
				assertTrue(ld.vehicleNotifications.get(
						VehicleNotificationStatus.REPLIED).isEmpty());
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
		LocalStorage.clearSavedFile();
		LocalStorage lds1 = new LocalStorage(c);
		lds1.withWriteLock(new Writer() {
			@Override
			public void write(LocalData ld) {
				ld.operationSchedules.add(new OperationSchedule());
			}
		});
		lds1.close();
		Thread.sleep(500); // 保存されるのを待つ

		// クリアされていないことを確認
		LocalStorage lds2 = new LocalStorage(c);
		lds2.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData ld) {
				assertEquals(ld.operationSchedules.size(), 1);
			}
		});
		lds2.close();

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(c);
		preferences.edit()
				.putBoolean(SharedPreferencesKeys.CLEAR_STATUS_BACKUP, true)
				.commit();
		// クリアされることを確認
		LocalStorage lds3 = new LocalStorage(c);
		lds3.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData ld) {
				assertTrue(ld.operationSchedules.isEmpty());
			}
		});
		lds3.close();

		// SharedPreferencesKey.CLEAR_REQUIREDがfalseになっていることを確認
		assertFalse(preferences.getBoolean(
				SharedPreferencesKeys.CLEAR_STATUS_BACKUP, true));

		// データが復活しないことを確認
		LocalStorage lds4 = new LocalStorage(c);
		lds4.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData ld) {
				assertTrue(ld.operationSchedules.isEmpty());
			}
		});
		lds4.close();
	}

	/**
	 * 読み書きロックのロジックがきちんと動いているかどうかのチェック
	 */
	public void testReadWriteLock() throws Exception {
		final CyclicBarrier cb = new CyclicBarrier(200);
		final LocalStorage lds = new LocalStorage(getContext());
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
		LocalStorage lds = new LocalStorage(getContext(), savePeriod);
		final VehicleNotification vn = new VehicleNotification();
		vn.setId(1);
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.vehicleNotifications.clear();
				localData.vehicleNotifications.put(
						VehicleNotificationStatus.UNHANDLED,
						SerializationUtils.clone(vn));
			}
		});
		Thread.sleep(savePeriod / 5);
		LocalStorage read1 = new LocalStorage(getContext());
		Integer id1 = read1.withReadLock(new Reader<Integer>() {
			@Override
			public Integer read(LocalData localData) {
				return localData.vehicleNotifications
						.get(VehicleNotificationStatus.UNHANDLED).iterator()
						.next().getId();
			}
		});
		assertEquals(1, id1.intValue());

		// savePeriod経過していない状態では、永続化されないことを確認
		vn.setId(2);
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.vehicleNotifications.clear();
				localData.vehicleNotifications.put(
						VehicleNotificationStatus.UNHANDLED,
						SerializationUtils.clone(vn));
			}
		});
		Thread.sleep(savePeriod / 5);
		LocalStorage read2 = new LocalStorage(getContext());
		Integer id2 = read2.withReadLock(new Reader<Integer>() {
			@Override
			public Integer read(LocalData localData) {
				return localData.vehicleNotifications
						.get(VehicleNotificationStatus.UNHANDLED).iterator()
						.next().getId();
			}
		});
		assertEquals(1, id2.intValue());

		// savePeriodが経過すると、永続化されることを確認
		Thread.sleep(savePeriod);
		LocalStorage read3 = new LocalStorage(getContext());
		Integer id3 = read3.withReadLock(new Reader<Integer>() {
			@Override
			public Integer read(LocalData localData) {
				return localData.vehicleNotifications
						.get(VehicleNotificationStatus.UNHANDLED).iterator()
						.next().getId();
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

		LocalStorage lds = new LocalStorage(getContext());
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.operationScheduleInitializedSign.release();
			}
		});
		lds.close();
		Thread.sleep(s);

		// 新運行スケジュール受信時刻をまたいでいない場合はそのまま
		lds = new LocalStorage(getContext());
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
		lds = new LocalStorage(getContext());
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

	public void testReadInBackground() throws Exception {
		final CountDownLatch cdl = new CountDownLatch(1);
		final HandlerThread ht = new HandlerThread("") {
			@Override
			protected void onLooperPrepared() {
				cdl.countDown();
			}
		};
		ht.start();
		cdl.await();
		final OperationSchedule os = new OperationSchedule();
		os.setId(12345);
		final AtomicReference<OperationSchedule> resultOs = new AtomicReference<OperationSchedule>();
		final LocalStorage lds = new LocalStorage(getContext());
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData ld) {
				ld.operationSchedules.clear();
				ld.operationSchedules.add(os);
			}
		});

		final Long threadId = Thread.currentThread().getId();
		final BackgroundReader<OperationSchedule> br = new BackgroundReader<OperationSchedule>() {
			@Override
			public OperationSchedule readInBackground(LocalData ld) {
				ComparableAssert.assertNotEquals(threadId, Thread
						.currentThread().getId());
				ComparableAssert.assertNotEquals(threadId, ht.getId());
				return ld.operationSchedules.get(0);
			}

			@Override
			public void onRead(OperationSchedule result) {
				resultOs.set(result);
				assertEquals(Thread.currentThread().getId(), ht.getId());
				assertTrue(ht.quit());
			}
		};
		Handler handler = new Handler(ht.getLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				lds.read(br);
			}
		});
		try {
			ht.join(5000);
		} finally {
			lds.close();
			ht.quit();
		}
		assertNotNull(resultOs.get());
		assertEquals(os.getId(), resultOs.get().getId());
	}

	public void testWriteInBackground() throws Exception {
		final CountDownLatch cdl = new CountDownLatch(1);
		final HandlerThread ht = new HandlerThread("") {
			@Override
			protected void onLooperPrepared() {
				cdl.countDown();
			}
		};
		ht.start();
		cdl.await();
		final OperationSchedule os = new OperationSchedule();
		os.setId(54321);
		final LocalStorage lds = new LocalStorage(getContext());
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData ld) {
				ld.operationSchedules.clear();
			}
		});

		final Long threadId = Thread.currentThread().getId();
		final BackgroundWriter bw = new BackgroundWriter() {
			@Override
			public void writeInBackground(LocalData ld) {
				ComparableAssert.assertNotEquals(threadId, Thread
						.currentThread().getId());
				ComparableAssert.assertNotEquals(threadId, ht.getId());
				ld.operationSchedules.add(os);
			}

			@Override
			public void onWrite() {
				assertEquals(Thread.currentThread().getId(), ht.getId());
				assertTrue(ht.quit());
			}
		};
		Handler handler = new Handler(ht.getLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				lds.write(bw);
			}
		});
		try {
			ht.join(5000);
		} finally {
			lds.close();
			ht.quit();
		}
		OperationSchedule resultOs = lds.withReadLock(new Reader<OperationSchedule>(){
			@Override
			public OperationSchedule read(LocalData localData) {
				return localData.operationSchedules.get(0);
			}});
		assertEquals(os.getId(), resultOs.getId());
	}
}
