package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import junitx.framework.ComparableAssert;

import com.kogasoftware.odt.apiclient.Serializations;

import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTimeUtils;

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
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.Writer;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKeys;
import com.kogasoftware.odt.invehicledevice.testutil.TestUtil;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.UnmergedOperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;

public class LocalStorageTestCase extends AndroidTestCase {
	static final Long S = TestUtil.getLocalStorageSaveMillis();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TestUtil.clearLocalStorage(getContext());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		Thread.sleep(S); // 保存されるのを待つ
	}

	/**
	 * デフォルトコンストラクタ。非チェック例外が発生しないことのみ確認
	 */
	public void testConstructor() throws Exception {
		LocalStorage ls = new LocalStorage();
		ls.withReadLock(new Reader<Date>() {
			@Override
			public Date read(LocalData ld) {
				return ld.updatedDate;
			}
		});
		ls.withWriteLock(new Writer() {
			@Override
			public void write(LocalData ld) {
				ld.vehicleNotifications.clear();
			}
		});
		ls.close();
	}

	/**
	 * コンストラクタ。clearSavedFileが呼ばれたら内容をクリア
	 */
	public void testClearSavedFile() throws Exception {
		Context c = getContext();
		// 保存
		LocalStorage ls1 = new LocalStorage(c);
		ls1.withWriteLock(new Writer() {
			@Override
			public void write(LocalData ld) {
				ld.operation.passengerRecords.clear();
				ld.operation.passengerRecords.add(new PassengerRecord());
				ld.vehicleNotifications.clear();
				ld.vehicleNotifications.put(VehicleNotificationStatus.REPLIED,
						new VehicleNotification());
			}
		});
		ls1.close();
		Thread.sleep(S); // 保存されるのを待つ

		// クリアされていないことを確認
		LocalStorage ls2 = new LocalStorage(c);
		ls2.withReadLock(new Reader<Integer>() {
			@Override
			public Integer read(LocalData ld) {
				assertEquals(1, ld.operation.passengerRecords.size());
				assertEquals(
						1,
						ld.vehicleNotifications.get(
								VehicleNotificationStatus.REPLIED).size());
				return 0;
			}
		});
		ls2.close();
		Thread.sleep(S); // 保存されるのを待つ

		// クリアされることを確認
		LocalStorage.clearSavedFile();
		LocalStorage ls3 = new LocalStorage(c);
		ls3.withReadLock(new Reader<Integer>() {
			@Override
			public Integer read(LocalData ld) {
				assertTrue(ld.operation.passengerRecords.isEmpty());

				assertTrue(ld.vehicleNotifications.get(
						VehicleNotificationStatus.REPLIED).isEmpty());
				return 0;
			}
		});
		ls3.close();
		Thread.sleep(S); // 保存されるのを待つ

		// 復活しないことを確認
		LocalStorage ls4 = new LocalStorage(c);
		ls4.withReadLock(new Reader<Integer>() {
			@Override
			public Integer read(LocalData ld) {
				assertTrue(ld.operation.passengerRecords.isEmpty());
				assertTrue(ld.vehicleNotifications.get(
						VehicleNotificationStatus.REPLIED).isEmpty());
				return 0;
			}
		});
		ls4.close();
	}

	/**
	 * コンストラクタ。SharedPreferencesKeyのCLEAR_REQUIREDがtrueの場合 内容をクリア
	 */
	public void testClearRequired() throws Exception {
		Context c = getContext();

		// 保存
		LocalStorage.clearSavedFile();
		LocalStorage ls1 = new LocalStorage(c);
		ls1.withWriteLock(new Writer() {
			@Override
			public void write(LocalData ld) {
				ld.operation.operationSchedules.add(new OperationSchedule());
			}
		});
		ls1.close();
		Thread.sleep(S); // 保存されるのを待つ

		// クリアされていないことを確認
		LocalStorage ls2 = new LocalStorage(c);
		ls2.withReadLock(new Reader<Integer>() {
			@Override
			public Integer read(LocalData ld) {
				assertEquals(ld.operation.operationSchedules.size(), 1);
				return 0;
			}
		});
		ls2.close();

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(c);
		preferences.edit()
				.putBoolean(SharedPreferencesKeys.CLEAR_STATUS_BACKUP, true)
				.commit();
		// クリアされることを確認
		LocalStorage ls3 = new LocalStorage(c);
		ls3.withReadLock(new Reader<Integer>() {
			@Override
			public Integer read(LocalData ld) {
				assertTrue(ld.operation.operationSchedules.isEmpty());
				return 0;
			}
		});
		ls3.close();

		// SharedPreferencesKey.CLEAR_REQUIREDがfalseになっていることを確認
		assertFalse(preferences.getBoolean(
				SharedPreferencesKeys.CLEAR_STATUS_BACKUP, true));

		// データが復活しないことを確認
		LocalStorage ls4 = new LocalStorage(c);
		ls4.withReadLock(new Reader<Integer>() {
			@Override
			public Integer read(LocalData ld) {
				assertTrue(ld.operation.operationSchedules.isEmpty());
				return 0;
			}
		});
		ls4.close();
	}

	/**
	 * 読み書きロックのロジックがきちんと動いているかどうかのチェック
	 */
	public void xtestReadWriteLock() throws Exception {
		final CyclicBarrier cb = new CyclicBarrier(200);
		final LocalStorage ls = new LocalStorage(getContext());
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
				ls.withReadLock(new Reader<Integer>() {
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
				ls.withWriteLock(new Writer() {
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
			if (i % 2 == 0) {
				threads[i] = new Thread(useWriter);
			} else {
				threads[i] = new Thread(useReader);
			}
			threads[i].start();
		}
		for (Integer i = 0; i < threads.length; ++i) {
			threads[i].join();
		}

		ls.close();

		// 同時アクセスが頻繁に発生しているか
		assertTrue(threads.length * 0.7 + " < " + numConcurrentAccesses.get(),
				threads.length * 0.7 < numConcurrentAccesses.get());
		// その他エラーが発生していないか
		assertFalse(error.get());
	}

	/**
	 * 指定した周期でデータの永続化が発生しているか
	 */
	public void xtestSavePeriod() throws InterruptedException {
		Integer savePeriod = 1000;

		// 永続化されるかの確認
		LocalStorage ls = new LocalStorage(getContext(), savePeriod);
		final VehicleNotification vn = new VehicleNotification();
		vn.setId(1);
		ls.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.vehicleNotifications.clear();
				localData.vehicleNotifications.put(
						VehicleNotificationStatus.UNHANDLED,
						Serializations.clone(vn));
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
		ls.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.vehicleNotifications.clear();
				localData.vehicleNotifications.put(
						VehicleNotificationStatus.UNHANDLED,
						Serializations.clone(vn));
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

		ls.close();
		read1.close();
		read2.close();
		read3.close();
	}

	/**
	 * 新運行スケジュール受信時刻をまたいだらデータがクリアされるかのチェック
	 */
	public void testNewSchedule() throws Exception {
		// 日付を指定
		Date d = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		d = DateUtils.setHours(d,
				InVehicleDeviceService.NEW_SCHEDULE_DOWNLOAD_HOUR);
		d = DateUtils.setMinutes(d,
				InVehicleDeviceService.NEW_SCHEDULE_DOWNLOAD_MINUTE);
		d = DateUtils.addMilliseconds(d, (int) -S * 2);
		DateTimeUtils.setCurrentMillisFixed(d.getTime());

		LocalStorage ls1 = new LocalStorage(getContext());
		ls1.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.operation.operationScheduleReceiveSequence = 1;
			}
		});
		ls1.close();
		Thread.sleep(S);

		// 新運行スケジュール受信時刻をまたいでいない場合はそのまま
		LocalStorage ls2 = new LocalStorage(getContext());
		Boolean b1 = ls2.withReadLock(new Reader<Boolean>() {
			@Override
			public Boolean read(LocalData localData) {
				return localData.operation.operationScheduleReceiveSequence > 0;
			}
		});
		assertTrue(b1.booleanValue());

		// 新運行スケジュール受信時刻をまたいだばあいはクリアさせる
		DateTimeUtils.setCurrentMillisFixed(DateUtils.addMilliseconds(d,
				(int) (S * 3)).getTime());
		LocalStorage ls3 = new LocalStorage(getContext());
		Boolean b2 = ls3.withReadLock(new Reader<Boolean>() {
			@Override
			public Boolean read(LocalData localData) {
				return localData.operation.operationScheduleReceiveSequence > 0;
			}
		});
		assertFalse(b2.booleanValue());

		// 保存が走ることがあるのでcloseは最後
		ls2.close();
		ls3.close();
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
		final UnmergedOperationSchedule os = new UnmergedOperationSchedule();
		os.setId(12345);
		final AtomicReference<UnmergedOperationSchedule> resultOs = new AtomicReference<UnmergedOperationSchedule>();
		final LocalStorage ls = new LocalStorage(getContext());
		ls.withWriteLock(new Writer() {
			@Override
			public void write(LocalData ld) {
				ld.operation.operationSchedules.clear();
				ld.operation.operationSchedules.add(os.toOperationSchedule());
			}
		});

		final Long threadId = Thread.currentThread().getId();
		final BackgroundReader<UnmergedOperationSchedule> br = new BackgroundReader<UnmergedOperationSchedule>() {
			@Override
			public UnmergedOperationSchedule readInBackground(LocalData ld) {
				ComparableAssert.assertNotEquals(threadId, Thread
						.currentThread().getId());
				ComparableAssert.assertNotEquals(threadId, ht.getId());
				return ld.operation.operationSchedules.get(0).getSourceOperationSchedules().get(0);
			}

			@Override
			public void onRead(UnmergedOperationSchedule result) {
				resultOs.set(result);
				assertEquals(Thread.currentThread().getId(), ht.getId());
				assertTrue(ht.quit());
			}
		};
		Handler handler = new Handler(ht.getLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				ls.read(br);
			}
		});
		try {
			ht.join(5000);
		} finally {
			ls.close();
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
		final UnmergedOperationSchedule os = new UnmergedOperationSchedule();
		os.setId(54321);
		final LocalStorage ls = new LocalStorage(getContext());
		ls.withWriteLock(new Writer() {
			@Override
			public void write(LocalData ld) {
				ld.operation.operationSchedules.clear();
			}
		});

		final Long threadId = Thread.currentThread().getId();
		final BackgroundWriter bw = new BackgroundWriter() {
			@Override
			public void writeInBackground(LocalData ld) {
				ComparableAssert.assertNotEquals(threadId, Thread
						.currentThread().getId());
				ComparableAssert.assertNotEquals(threadId, ht.getId());
				ld.operation.operationSchedules.add(os.toOperationSchedule());
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
				ls.write(bw);
			}
		});
		try {
			ht.join(5000);
		} finally {
			ls.close();
			ht.quit();
		}
		UnmergedOperationSchedule resultOs = ls
				.withReadLock(new Reader<UnmergedOperationSchedule>() {
					@Override
					public UnmergedOperationSchedule read(LocalData localData) {
						return localData.operation.operationSchedules.get(0).getSourceOperationSchedules().get(0);
					}
				});
		assertEquals(os.getId(), resultOs.getId());
	}
}
