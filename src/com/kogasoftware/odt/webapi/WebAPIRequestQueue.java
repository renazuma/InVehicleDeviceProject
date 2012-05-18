package com.kogasoftware.odt.webapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import android.util.Log;

import com.google.common.io.Closeables;

/**
 * WebAPIRequestを管理するクラス
 */
public class WebAPIRequestQueue {
	private static final String TAG = WebAPIRequestQueue.class.getSimpleName();

	// 作業中リクエスト
	protected final Queue<WebAPIRequest<?>> processingQueue = new LinkedList<WebAPIRequest<?>>();
	// 作業待ちリクエスト
	protected final Queue<WebAPIRequest<?>> waitingQueue = new LinkedList<WebAPIRequest<?>>();
	// 作業待ちリクエストの追加待ち処理を実装するためのセマフォ。パーミットの数は必ずwaitingQueueのサイズと同じか多くなるようにする。
	protected final Semaphore waitingQueuePollPermissions = new Semaphore(0);
	// 各キューの整合性を保つためのロック
	protected final Object queueLock = new Object();
	// バックアップ先ファイル名
	protected final File backupFile;

	/**
	 * コンストラクタ saveFileが指定されている場合はそのファイルからデータを読み出す
	 * 
	 * @param savedFile
	 *            データを読み出すファイル
	 */
	public WebAPIRequestQueue(File savedFile) {
		this.backupFile = savedFile;
		if (backupFile == null) {
			return;
		}
		if (!backupFile.exists()) {
			return;
		}
		FileInputStream fileInputStream = null;
		ObjectInputStream objectInputStream = null;
		try {
			fileInputStream = new FileInputStream(savedFile);
			objectInputStream = new ObjectInputStream(fileInputStream);
			Object object = objectInputStream.readObject();
			if (!(object instanceof ArrayList<?>)) {
				Log.w(TAG, "!(" + object + " instanceof ArrayList<?>)");
				return;
			}
			ArrayList<?> arrayList = (ArrayList<?>) object;
			for (Object element : arrayList) {
				if (element instanceof WebAPIRequest<?>) {
					waitingQueue.add((WebAPIRequest<?>) element);
				}
			}
		} catch (IOException e) {
			Log.w(TAG, e);
		} catch (ClassNotFoundException e) {
			Log.w(TAG, e);
		} finally {
			waitingQueuePollPermissions.release(waitingQueue.size());
			Closeables.closeQuietly(objectInputStream);
			Closeables.closeQuietly(fileInputStream);
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
		if (backupFile == null) {
			return;
		}
		ArrayList<WebAPIRequest<?>> list = new ArrayList<WebAPIRequest<?>>();
		synchronized (queueLock) {
			list.addAll(waitingQueue);
			list.addAll(processingQueue);
		}

		ObjectOutputStream objectOutputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(backupFile);
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(list);
		} catch (IOException e) {
			Log.w(TAG, e);
		} finally {
			Closeables.closeQuietly(objectOutputStream);
			Closeables.closeQuietly(fileOutputStream);
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
