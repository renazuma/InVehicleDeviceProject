package com.kogasoftware.odt.webapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Semaphore;

import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;

import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

/**
 * WebAPIRequestを管理するクラス
 */
public class WebAPIRequestQueue {
	private static final String TAG = WebAPIRequestQueue.class.getSimpleName();
	private static final Object FILE_ACCESS_LOCK = new Object(); // ファイルアクセス中のスレッドを一つに制限するためのロック。将来的にはロックの粒度をファイル毎にする必要があるかもしれない。
	public static final String DEFAULT_GROUP = "default_group";

	private static class InstanceState implements Serializable {
		private static final long serialVersionUID = 672897944999498098L;
		private final LinkedListMultimap<String, WebAPIRequest<?>> requests;

		public InstanceState(Multimap<String, WebAPIRequest<?>> requests) {
			this.requests = LinkedListMultimap.create(requests);
		}
	}

	// 作業中のグループ
	protected final Set<String> processingGroups = new HashSet<String>();
	// グループ毎作業待ちリクエスト
	protected final ListMultimap<String, WebAPIRequest<?>> requestsByGroup = LinkedListMultimap
			.create();
	// グループ毎作業待ちリクエストの追加待ち処理を実装するためのセマフォ。パーミットの数は必ずwaitingQueueのサイズと同じか多くなるようにする。
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
				Object object = SerializationUtils
						.deserialize(new FileInputStream(backupFile));
				if (!(object instanceof InstanceState)) {
					Log.w(TAG, "!(" + object + " instanceof InstanceState)");
					return;
				}
				requestsByGroup.putAll(((InstanceState) object).requests);
			} catch (IOException e) {
				Log.w(TAG, e);
			} catch (SerializationException e) {
				Log.e(TAG, e.toString(), e);
			} finally {
				waitingQueuePollPermissions.release(requestsByGroup.size());
			}
		}
	}

	/**
	 * リクエストを追加し、作業待ちリクエストの追加待ちをしているスレッドを一つ起こせるようにする
	 * 
	 * @param request
	 */
	public void add(WebAPIRequest<?> request, String group) {
		synchronized (queueLock) {
			requestsByGroup.put(group, request);
			backup();
			waitingQueuePollPermissions.release();
		}
	}

	public void add(WebAPIRequest<?> request) {
		add(request, DEFAULT_GROUP);
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
		Multimap<String, WebAPIRequest<?>> backupRequests = LinkedListMultimap
				.create();
		synchronized (queueLock) {
			backupRequests.putAll(requestsByGroup);
		}

		synchronized (FILE_ACCESS_LOCK) {
			try {
				SerializationUtils.serialize(new InstanceState(backupRequests),
						new FileOutputStream(backupFile));
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
			for (Entry<String, WebAPIRequest<?>> entry : LinkedListMultimap
					.create(requestsByGroup).entries()) {
				if (entry.getValue() != request) {
					continue;
				}
				String group = entry.getKey();
				processingGroups.remove(group);
				requestsByGroup.remove(group, request);
				backup();
				waitingQueuePollPermissions.release(requestsByGroup.get(group).size());
				break;
			}
		}
	}

	/**
	 * 指定したリクエストに対応するグループを作業中から削除し、グループ全体の処理順を最後に移動。
	 * 指定したリクエストがデフォルトグループの場合、さらにグループ内の処理順を最後に移動。
	 * 
	 * @param request
	 *            リトライ対象のリクエスト
	 */
	public void retry(WebAPIRequest<?> request) {
		synchronized (queueLock) {
			for (Entry<String, WebAPIRequest<?>> entry : LinkedListMultimap
					.create(requestsByGroup).entries()) {
				if (entry.getValue() != request) {
					continue;
				}
				String group = entry.getKey();
				processingGroups.remove(group);
				List<WebAPIRequest<?>> retryRequests = requestsByGroup.removeAll(group);
				for (WebAPIRequest<?> retryRequest : retryRequests) {
					retryRequest.setRetry(true);
				}
				requestsByGroup.putAll(group, retryRequests);
				if (group.equals(DEFAULT_GROUP)) {
					requestsByGroup.remove(group, request);
					requestsByGroup.put(group, request);
				}
				backup();
				waitingQueuePollPermissions.release(requestsByGroup.get(group).size());
				break;
			}
		}
	}

	/**
	 * 作業中でないグループからリクエストを一つ取得し返す。またそのグループを作業中とする。 該当グループが存在しない場合は、存在するようになるまで待つ
	 * 
	 * @return リクエスト
	 * @throws InterruptedException
	 */
	public WebAPIRequest<?> take() throws InterruptedException {
		while (true) {
			waitingQueuePollPermissions.acquire(); // synchronizedの外で待つ
			synchronized (queueLock) {
				for (String group : new LinkedList<String>(
						requestsByGroup.keys())) {
					if (!group.equals(DEFAULT_GROUP)) {
						if (processingGroups.contains(group)) {
							continue;
						}
						processingGroups.add(group);
					}
					List<WebAPIRequest<?>> requests = requestsByGroup
							.get(group);
					if (requests == null || requests.isEmpty()) {
						requestsByGroup.removeAll(group);
						continue;
					}
					return requests.get(0);
				}
			}
		}
	}
}
