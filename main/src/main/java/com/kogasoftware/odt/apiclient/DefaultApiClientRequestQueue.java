package com.kogasoftware.odt.apiclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.SerializationException;

import com.kogasoftware.odt.apiclient.Serializations;

import org.apache.commons.lang3.tuple.Pair;

import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.io.Closeables;

/**
 * ApiClientRequestを管理するクラス
 */
public class DefaultApiClientRequestQueue {
	private static final String TAG = DefaultApiClientRequestQueue.class
			.getSimpleName();
	private static final Object FILE_ACCESS_LOCK = new Object(); // ファイルアクセス中のスレッドを一つに制限するためのロック。将来的にはロックの粒度をファイル毎にする必要があるかもしれない。
	public static final String UNIQUE_GROUP = "";

	protected static class InstanceState implements Serializable {
		protected static final long serialVersionUID = 672897944999498097L;
		protected final LinkedList<Pair<String, List<DefaultApiClientRequest<?>>>> requestsByGroup;

		public InstanceState(
				LinkedList<Pair<String, List<DefaultApiClientRequest<?>>>> requestsByGroup) {
			this.requestsByGroup = Lists.newLinkedList(requestsByGroup);
		}
	}

	// 一意のブループ名を作るための値
	protected final AtomicInteger uniqueGroupSequence = new AtomicInteger(0);
	// 作業中のグループ
	protected final Set<String> processingGroups = new HashSet<String>();
	// グループ毎作業待ちリクエスト
	protected final LinkedList<Pair<String, List<DefaultApiClientRequest<?>>>> requestsByGroup = Lists
			.newLinkedList();
	// 「グループ毎作業待ちリクエスト」の追加待ち処理を実装するためのセマフォ。パーミットの数は必ずrequestsByGroup内の作業中でないリクエストの数と同じか多くなるようにする。
	protected final Semaphore waitingQueuePollPermissions = new Semaphore(0);
	// 各キューの整合性を保つためのロック
	protected final Object queueLock = new Object();
	// バックアップ先ファイル名
	protected final Optional<File> optionalBackupFile;
	// バックアップを行いたい場合true
	protected final AtomicBoolean backupRequest = new AtomicBoolean(false);

	/**
	 * コンストラクタ
	 */
	public DefaultApiClientRequestQueue() {
		optionalBackupFile = Optional.absent();
	}

	/**
	 * コンストラクタ
	 * 
	 * @param backupFile
	 *            データを読み出すファイル
	 */
	public DefaultApiClientRequestQueue(File backupFile) {
		this.optionalBackupFile = Optional.of(backupFile);
		if (!backupFile.exists()) {
			return;
		}
		synchronized (FILE_ACCESS_LOCK) {
			InputStream inputStream = null;
			try {
				inputStream = new FileInputStream(backupFile);
				InstanceState instanceState = Serializations.deserialize(
						inputStream, InstanceState.class);
				requestsByGroup.addAll(instanceState.requestsByGroup);
			} catch (FileNotFoundException e) {
				Log.w(TAG, e);
			} catch (SerializationException e) {
				Log.e(TAG, e.toString(), e);
			} finally {
				waitingQueuePollPermissions.release(requestsByGroup.size());
				Closeables.closeQuietly(inputStream);
			}
		}
		for (Pair<String, List<DefaultApiClientRequest<?>>> entry : requestsByGroup) {
			String group = entry.getKey();
			for (DefaultApiClientRequest<?> request : entry.getValue()) {
				Log.i(TAG, "restored: " + request + " group=" + group);
			}
		}
	}

	protected List<DefaultApiClientRequest<?>> findOrCreateGroup(String group) {
		synchronized (queueLock) {
			// 同名のグループを探す
			for (Pair<String, List<DefaultApiClientRequest<?>>> pair : requestsByGroup) {
				if (pair.getKey().equals(group)) {
					return pair.getValue();
				}
			}
			// ない場合新規作成
			List<DefaultApiClientRequest<?>> newList = Lists.newLinkedList();
			requestsByGroup.addFirst(Pair.of(group, newList));
			return newList;
		}
	}

	/**
	 * リクエストを追加し、作業待ちリクエストの追加待ちをしているスレッドを一つ起こせるようにする
	 * 
	 * @param request
	 */
	public void add(DefaultApiClientRequest<?> request, String group) {
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
			backupRequest.set(true);
			waitingQueuePollPermissions.release();
		}
	}

	public void add(DefaultApiClientRequest<?> request) {
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
		LinkedList<Pair<String, List<DefaultApiClientRequest<?>>>> backupRequestsByGroup = Lists
				.newLinkedList();
		synchronized (queueLock) {
			for (Pair<String, List<DefaultApiClientRequest<?>>> pair : Lists
					.newLinkedList(requestsByGroup)) {
				List<DefaultApiClientRequest<?>> backupRequests = Lists
						.newLinkedList();
				for (DefaultApiClientRequest<?> request : pair.getValue()) {
					if (request.getConfig().getSaveOnClose()) {
						backupRequests.add(request);
					}
				}
				if (!backupRequests.isEmpty()) {
					backupRequestsByGroup.add(Pair.of(pair.getKey(),
							backupRequests));
				}
			}
		}

		synchronized (FILE_ACCESS_LOCK) {
			try {
				Serializations.serialize(new InstanceState(
						backupRequestsByGroup),
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
	public void remove(DefaultApiClientRequest<?> request) {
		synchronized (queueLock) {
			for (Pair<String, List<DefaultApiClientRequest<?>>> entry : Lists
					.newLinkedList(requestsByGroup)) {
				String group = entry.getKey();
				List<DefaultApiClientRequest<?>> requests = entry.getValue();
				if (!requests.remove(request)) {
					continue;
				}
				if (processingGroups.remove(group)) {
					waitingQueuePollPermissions.release(requests.size());
				}
				if (requests.isEmpty()) {
					requestsByGroup.remove(entry);
				}
				backupRequest.set(true);
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
		DefaultApiClientRequest<?> foundRequest = null;
		synchronized (queueLock) {
			for (Pair<String, List<DefaultApiClientRequest<?>>> entry : Lists
					.newLinkedList(requestsByGroup)) {
				for (DefaultApiClientRequest<?> request : entry.getValue()) {
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
	public void retry(DefaultApiClientRequest<?> request) {
		synchronized (queueLock) {
			for (Pair<String, List<DefaultApiClientRequest<?>>> entry : Lists
					.newLinkedList(requestsByGroup)) {
				String group = entry.getKey();
				List<DefaultApiClientRequest<?>> requests = entry.getValue();
				if (!requests.contains(request)) {
					continue;
				}
				if (processingGroups.remove(group)) {
					waitingQueuePollPermissions.release(requests.size());
				}
				requestsByGroup.remove(entry);
				requestsByGroup.addLast(entry);
				for (DefaultApiClientRequest<?> retryRequest : requests) {
					retryRequest.setRetry(true);
				}
				backupRequest.set(true);
			}
		}
	}

	/**
	 * 作業中でないグループからリクエストを一つ取得し返す。またそのグループを作業中とする。 該当グループが存在しない場合は、存在するようになるまで待つ
	 * 
	 * @return リクエスト
	 * @throws InterruptedException
	 */
	public DefaultApiClientRequest<?> take() throws InterruptedException {
		while (true) {
			waitingQueuePollPermissions.acquire(); // synchronizedの外で待つ
			synchronized (queueLock) {
				if (backupRequest.getAndSet(false)) {
					backup();
				}
				for (Pair<String, List<DefaultApiClientRequest<?>>> entry : Lists
						.newLinkedList(requestsByGroup)) {
					String group = entry.getKey();
					List<DefaultApiClientRequest<?>> requests = entry
							.getValue();
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
}
