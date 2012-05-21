package com.kogasoftware.odt.webapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;

import android.util.Log;

import com.google.common.base.Optional;

/**
 * WebAPIRequestを管理するクラス
 */
public class WebAPIRequestQueue {
	private static final String TAG = WebAPIRequestQueue.class.getSimpleName();
	private static final Object FILE_ACCESS_LOCK = new Object(); // ファイルアクセス中のスレッドを一つに制限するためのロック。将来的にはロックの粒度をファイル毎にする必要があるかもしれない。
	private static class SerializationList extends LinkedList<WebAPIRequest<?>> {
		private static final long serialVersionUID = 6728979449299498099L;
	}

	// 作業中リクエスト
	protected final Queue<WebAPIRequest<?>> processingQueue = new LinkedList<WebAPIRequest<?>>();
	// 作業待ちリクエスト
	protected final Queue<WebAPIRequest<?>> waitingQueue = new LinkedList<WebAPIRequest<?>>();
	// 作業待ちリクエストの追加待ち処理を実装するためのセマフォ。パーミットの数は必ずwaitingQueueのサイズと同じか多くなるようにする。
	protected final Semaphore waitingQueuePollPermissions = new Semaphore(0);
	// 各キューの整合性を保つためのロック
	protected final Object queueLock = new Object();
	// バックアップ先ファイル名
	protected final Optional<File> optionalBackupFile;

	public WebAPIRequestQueue() {
		optionalBackupFile = Optional.absent();
	}

	/**
	 * コンストラクタ backupFileが指定されている場合はそのファイルからデータを読み出す
	 * 
	 * @param backupFile
	 *            データを読み出すファイル
	 */
	public WebAPIRequestQueue(File backupFile) {
		this.optionalBackupFile = Optional.of(backupFile);
		if (!backupFile.exists()) {
			return;
		}
		synchronized (FILE_ACCESS_LOCK) {
			try {
				Object object = SerializationUtils.deserialize(new FileInputStream(backupFile));
				if (!(object instanceof SerializationList)) {
					Log.w(TAG, "!(" + object + " instanceof SerializationList)");
					return;
				}
				waitingQueue.addAll((SerializationList) object);
			} catch (IOException e) {
				Log.w(TAG, e);
			} catch (SerializationException e) {
				Log.e(TAG, e.toString(), e);
			} finally {
				waitingQueuePollPermissions.release(waitingQueue.size());
			}
		}
	}

	/**
	 * 作業待ちリクエストを追加し、作業待ちリクエストの追加待ちをしているスレッドを一つ起こせるようにする
	 * 
	 * @param request
	 */
	public void add(WebAPIRequest<?> request) {
		synchronized (queueLock) {
			waitingQueue.add(request);
			waitingQueuePollPermissions.release();
			backup();
		}
	}

	/**
	 * 現在のリクエストの保存
	 */
	protected void backup() {
		for (File backupFile : optionalBackupFile.asSet()) {
			backup(backupFile);
		}
	}

	protected void backup(File backupFile) {
		SerializationList list = new SerializationList();
		synchronized (queueLock) {
			list.addAll(waitingQueue);
			list.addAll(processingQueue);
		}

		synchronized (FILE_ACCESS_LOCK) {
			try {
				SerializationUtils.serialize(list, new FileOutputStream(backupFile));
			} catch (SerializationException e) {
				Log.e(TAG, e.toString(), e);
			} catch (IOException e) {
				Log.w(TAG, e);
			}
		}
	}

	/**
	 * リクエストを削除
	 * 
	 * @param request
	 */
	public void remove(WebAPIRequest<?> request) {
		synchronized (queueLock) {
			waitingQueue.remove(request);
			processingQueue.remove(request);
			backup();
		}
	}

	/**
	 * 作業中リクエストを作業待ちリクエストに移動
	 * 
	 * @param request
	 *            リトライ対象のリクエスト
	 */
	public void retry(WebAPIRequest<?> request) {
		synchronized (queueLock) {
			processingQueue.remove(request);
			add(request);
		}
	}

	/**
	 * 作業待ちリクエストからリクエストを一つ取得し、そのリクエストを作業中リクエストに保存
	 * 作業待ちリクエストがない場合は、作業待ちリクエストが追加されるまで待つ
	 * 
	 * @return リクエスト
	 * @throws InterruptedException
	 */
	public WebAPIRequest<?> take() throws InterruptedException {
		while (true) {
			waitingQueuePollPermissions.acquire(); // synchronizedの外で待つ
			synchronized (queueLock) {
				WebAPIRequest<?> request = waitingQueue.poll();
				if (request == null) {
					continue;
				}
				backup();
				processingQueue.add(request);
				return request;
			}
		}
	}
}
