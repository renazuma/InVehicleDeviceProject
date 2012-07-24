package com.kogasoftware.odt.webapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.tuple.Pair;

import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

/**
 * WebAPIRequestを管理するクラス
 */
public class WebAPIRequestQueue {
	private static final String TAG = WebAPIRequestQueue.class.getSimpleName();
	private static final Object FILE_ACCESS_LOCK = new Object(); // ファイルアクセス中のスレッドを一つに制限するためのロック。将来的にはロックの粒度をファイル毎にする必要があるかもしれない。
	public static final String UNIQUE_GROUP = "";

	protected static class InstanceState implements Serializable {
		protected static final long serialVersionUID = 672897944999498097L;
		protected final LinkedList<Pair<String, List<WebAPIRequest<?>>>> requestsByGroup;

		public InstanceState(
				LinkedList<Pair<String, List<WebAPIRequest<?>>>> requestsByGroup) {
			this.requestsByGroup = Lists.newLinkedList(requestsByGroup);
		}
	}

	// 一意のブループ名を作るための値
	protected final AtomicInteger uniqueGroupSequence = new AtomicInteger(0);
	// 作業中のグループ
	protected final Set<String> processingGroups = new HashSet<String>();
	// グループ毎作業待ちリクエスト
	protected final LinkedList<Pair<String, List<WebAPIRequest<?>>>> requestsByGroup = Lists
			.newLinkedList();
	// 「グループ毎作業待ちリクエスト」の追加待ち処理を実装するためのセマフォ。パーミットの数は必ずrequestsByGroup内の作業中でないリクエストの数と同じか多くなるようにする。
	protected final Semaphore waitingQueuePollPermissions = new Semaphore(0);
	// 各キューの整合性を保つためのロック
	protected final Object queueLock = new Object();
	// バックアップ先ファイル名
	protected final Optional<File> optionalBackupFile;

	/**
	 * コンストラクタ
	 */
	public WebAPIRequestQueue() {
		optionalBackupFile = Optional.absent();
	}

	/**
	 * コンストラクタ
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
				InstanceState instanceState = (InstanceState) object;
				requestsByGroup.addAll(instanceState.requestsByGroup);
			} catch (IndexOutOfBoundsException e) {
				Log.e(TAG, e.toString(), e);
			} catch (IOException e) {
				Log.w(TAG, e);
			} catch (SerializationException e) {
				Log.e(TAG, e.toString(), e);
			} finally {
				waitingQueuePollPermissions.release(requestsByGroup.size());
			}
		}
	}

	protected List<WebAPIRequest<?>> findOrCreateGroup(String group) {
		synchronized (queueLock) {
			// 同名のグループを探す
			for (Pair<String, List<WebAPIRequest<?>>> pair : requestsByGroup) {
				if (pair.getKey().equals(group)) {
					return pair.getValue();
				}
			}
			// ない場合新規作成
			List<WebAPIRequest<?>> newList = Lists.newLinkedList();
			requestsByGroup.addFirst(Pair.of(group, newList));
			return newList;
		}
	}

	/**
	 * リクエストを追加し、作業待ちリクエストの追加待ちをしているスレッドを一つ起こせるようにする
	 * 
	 * @param request
	 */
	public void add(WebAPIRequest<?> request, String group) {
		synchronized (queueLock) {
			if (group.equals(UNIQUE_GROUP)) {
				// ユニークなグループ名を作成
				while (true) {
					Integer s = uniqueGroupSequence.getAndIncrement();
					group = "group-" + s;
					if (findOrCreateGroup(group).isEmpty()) {
						break;
					}
				}
			}
			findOrCreateGroup(group).add(request);
			backup();
			waitingQueuePollPermissions.release();
		}
	}

	public void add(WebAPIRequest<?> request) {
		add(request, UNIQUE_GROUP);
	}

	/**
	 * 現在の状態を保存
	 */
	protected void backup() {
		for (File backupFile : optionalBackupFile.asSet()) {
			backup(backupFile);
		}
	}

	protected void backup(File backupFile) {
		LinkedList<Pair<String, List<WebAPIRequest<?>>>> backupRequestsByGroup = Lists
				.newLinkedList();
		synchronized (queueLock) {
			for (Pair<String, List<WebAPIRequest<?>>> pair : Lists
					.newLinkedList(requestsByGroup)) {
				List<WebAPIRequest<?>> backupRequests = Lists.newLinkedList();
				for (WebAPIRequest<?> request : pair.getValue()) {
					if (request.isSaveOnClose()) {
						backupRequests.add(request);
					}
				}
				if (!backupRequests.isEmpty()) {
					backupRequestsByGroup.add(Pair.of(pair.getKey(), backupRequests));
				}
			}
		}

		synchronized (FILE_ACCESS_LOCK) {
			try {
				SerializationUtils.serialize(new InstanceState(backupRequestsByGroup),
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
			for (Pair<String, List<WebAPIRequest<?>>> entry : Lists
					.newLinkedList(requestsByGroup)) {
				String group = entry.getKey();
				List<WebAPIRequest<?>> requests = entry.getValue();
				if (!requests.remove(request)) {
					continue;
				}
				if (processingGroups.remove(group)) {
					waitingQueuePollPermissions.release(requests.size());
				}
				if (requests.isEmpty()) {
					requestsByGroup.remove(entry);
				}
				backup();
				break;
			}
		}
	}

	/**
	 * リクエストの中断
	 * 
	 * @param reqkey
	 */
	public void abort(int reqkey) {
		WebAPIRequest<?> foundRequest = null;
		synchronized (queueLock) {
			for (Pair<String, List<WebAPIRequest<?>>> entry : Lists
					.newLinkedList(requestsByGroup)) {
				for (WebAPIRequest<?> request : entry.getValue()) {
					if (request.getReqKey() == reqkey) {
						foundRequest = request;
						remove(request);
						break;
					}
				}
			}
		}
		if (foundRequest != null) {
			foundRequest.abort();
		}
	}

	/**
	 * 指定したリクエストに対応するグループを作業中から削除し、グループ全体の処理順を最後に移動。
	 * 
	 * @param request
	 *            リトライ対象のリクエスト
	 */
	public void retry(WebAPIRequest<?> request) {
		synchronized (queueLock) {
			for (Pair<String, List<WebAPIRequest<?>>> entry : Lists
					.newLinkedList(requestsByGroup)) {
				String group = entry.getKey();
				List<WebAPIRequest<?>> requests = entry.getValue();
				if (!requests.contains(request)) {
					continue;
				}
				if (processingGroups.remove(group)) {
					waitingQueuePollPermissions.release(requests.size());
				}
				requestsByGroup.remove(entry);
				requestsByGroup.addLast(entry);
				for (WebAPIRequest<?> retryRequest : requests) {
					retryRequest.setRetry(true);
				}
				backup();
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
				for (Pair<String, List<WebAPIRequest<?>>> entry : Lists
						.newLinkedList(requestsByGroup)) {
					String group = entry.getKey();
					List<WebAPIRequest<?>> requests = entry.getValue();
					if (requests.isEmpty()) {
						processingGroups.remove(group);
						requestsByGroup.remove(entry);
						continue;
					}
					if (processingGroups.contains(group)) {
						continue;
					}
					processingGroups.add(group);
					return requests.get(0);
				}
			}
		}
	}

	public void setSaveOnClose(int reqkey, boolean saveOnClose) {
		synchronized (queueLock) {
			for (Pair<String, List<WebAPIRequest<?>>> entry : Lists
					.newLinkedList(requestsByGroup)) {
				for (WebAPIRequest<?> request : entry.getValue()) {
					if (request.getReqKey() == reqkey) {
						request.setSaveOnClose(saveOnClose);
						backup();
						break;
					}
				}
			}
		}
	}
}
