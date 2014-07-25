package com.kogasoftware.odt.invehicledevice.service.voiceservice;

import java.io.File;
import java.io.IOException;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.io.FileUtils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.HandlerThread;
import android.os.Messenger;
import android.util.Log;

import com.google.android.vending.expansion.downloader.DownloadProgressInfo;
import com.google.android.vending.expansion.downloader.DownloaderClientMarshaller;
import com.google.android.vending.expansion.downloader.DownloaderServiceMarshaller;
import com.google.android.vending.expansion.downloader.Helpers;
import com.google.android.vending.expansion.downloader.IDownloaderClient;
import com.google.android.vending.expansion.downloader.IDownloaderService;
import com.google.android.vending.expansion.downloader.IStub;
import com.google.common.base.Joiner;
import com.kogasoftware.odt.invehicledevice.service.voicedownloaderservice.VoiceDownloaderService;

public class VoiceDownloaderClientThread extends HandlerThread
		implements
			IDownloaderClient {
	private static final String TAG = VoiceDownloaderClientThread.class
			.getSimpleName();
	private static final Integer VOICE_EXPANSION_APK_VERSION = 9157;
	private IDownloaderService downloaderService;
	private IStub downloaderClientStub;
	private final Object extractVoiceFileLock = new Object();
	private final Context context;

	public VoiceDownloaderClientThread(Context context, String name) {
		super(name);
		this.context = context;
	}

	@Override
	protected void onLooperPrepared() {
		super.onLooperPrepared();
		startVoiceDownloaderServiceIfRequired();
		startExtractVoiceFileThreadIfRequired();
	}

	void startVoiceDownloaderServiceIfRequired() {
		downloaderClientStub = DownloaderClientMarshaller.CreateStub(this,
				VoiceDownloaderService.class);
		downloaderClientStub.connect(context);
		if (isVoiceFileAccessible()) {
			Log.v(TAG,
					"startVoiceDownloaderServiceIfRequired() skipped by isVoiceFileAccessible()");
			return;
		}

		// Build an Intent to start this activity from the Notification
		Intent notifierIntent = new Intent(context, getClass());
		notifierIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				notifierIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// Start the download service (if required)
		try {
			DownloaderClientMarshaller.startDownloadServiceIfRequired(context,
					pendingIntent, VoiceDownloaderService.class);
		} catch (NameNotFoundException e) {
			throw new RuntimeException(e); // Fatal exception
		}
	}

	@Override
	public void onServiceConnected(Messenger m) {
		downloaderService = DownloaderServiceMarshaller.CreateProxy(m);
		downloaderService.onClientUpdated(downloaderClientStub.getMessenger());
		downloaderService
				.setDownloadFlags(IDownloaderService.FLAGS_DOWNLOAD_OVER_CELLULAR);
		downloaderService.requestContinueDownload();
	}

	@Override
	public void onDownloadStateChanged(int newState) {
		switch (newState) {
			case IDownloaderClient.STATE_IDLE :
				updateVoiceFileStateText("IDLE");
				break;
			case IDownloaderClient.STATE_CONNECTING :
				updateVoiceFileStateText("CONNECTING");
				break;
			case IDownloaderClient.STATE_FETCHING_URL :
				updateVoiceFileStateText("FETCHING_URL");
				break;
			case IDownloaderClient.STATE_DOWNLOADING :
				updateVoiceFileStateText("ダウンロード中");
				break;
			case IDownloaderClient.STATE_FAILED_CANCELED :
				updateVoiceFileStateText("⚠ CANCELED");
				break;
			case IDownloaderClient.STATE_FAILED :
				updateVoiceFileStateText("⚠ FAILED");
				break;
			case IDownloaderClient.STATE_FAILED_FETCHING_URL :
				updateVoiceFileStateText("⚠ FAILED_FETCHING_URL");
				break;
			case IDownloaderClient.STATE_FAILED_UNLICENSED :
				updateVoiceFileStateText("⚠ FAILED_UNLICENSED");
				break;
			case IDownloaderClient.STATE_PAUSED_NEED_CELLULAR_PERMISSION :
				updateVoiceFileStateText("⚠ PAUSED_NEED_CELLULAR_PERMISSION");
				break;
			case IDownloaderClient.STATE_PAUSED_WIFI_DISABLED_NEED_CELLULAR_PERMISSION :
				updateVoiceFileStateText("⚠ PAUSED_WIFI_DISABLED_NEED_CELLULAR_PERMISSION");
				break;
			case IDownloaderClient.STATE_PAUSED_BY_REQUEST :
				updateVoiceFileStateText("⚠ PAUSED_BY_REQUEST");
				break;
			case IDownloaderClient.STATE_PAUSED_ROAMING :
				updateVoiceFileStateText("⚠ PAUSED_ROAMING");
				break;
			case IDownloaderClient.STATE_PAUSED_SDCARD_UNAVAILABLE :
				updateVoiceFileStateText("⚠ PAUSED_SDCARD_UNAVAILABLE");
				break;
			case IDownloaderClient.STATE_COMPLETED :
				if (isVoiceFileAccessible()) {
					startExtractVoiceFileThreadIfRequired();
				} else {
					updateVoiceFileStateText("音声ファイルのダウンロードに失敗しました");
				}
				break;
			default :
				updateVoiceFileStateText("⚠ State: " + newState);
		}
	}

	private void updateVoiceFileStateText(String text) {
		Log.v(TAG, "updateVoiceFileStateText(\"" + text + "\")");
		context.sendBroadcast(new VoiceDownloadStateBroadcastIntent(text));
	}

	@Override
	public void onDownloadProgress(DownloadProgressInfo progress) {
		updateVoiceFileStateText(String.format("ダウンロード中 (%8d/%d)",
				progress.mOverallProgress, progress.mOverallTotal));
	}

	static File getExternalStorageFile(String... paths) throws IOException {
		String state = Environment.getExternalStorageState();
		if (!state.equals(Environment.MEDIA_MOUNTED)) {
			throw new IOException("ExternalStorageState: " + state);
		}
		return new File(Environment.getExternalStorageDirectory(), Joiner.on(
				File.separator).join(paths));
	}

	File getVoiceFile() throws IOException {
		String fileName = Helpers.getExpansionAPKFileName(context, true,
				VOICE_EXPANSION_APK_VERSION);
		return new File(Helpers.generateSaveFileName(context, fileName));
	}

	public static File getVoiceOutputDir() throws IOException {
		return getExternalStorageFile(".odt", "open_jtalk");
	}

	void extractVoiceFileIfRequired() throws IOException {
		synchronized (extractVoiceFileLock) {
			// 展開作業ディレクトリと出力ディレクトリを分ける。それにより、展開作業が中断したときのリカバリが簡単になると思う。
			File outputDir = getVoiceOutputDir();
			File extractDir = getExternalStorageFile(".odt",
					"open_jtalk.extract");
			if (outputDir.isDirectory()) {
				Log.v(TAG, "extractVoiceFileIfRequired() skipped by \""
						+ outputDir.getAbsolutePath() + "\".isDirectory()");
				return;
			}
			File voiceFile = getVoiceFile();
			Log.v(TAG,
					"extractVoiceFileIfRequired() outputDir=\""
							+ outputDir.getAbsolutePath() + " \"extractDir=\""
							+ extractDir.getAbsolutePath() + " \"voiceFile=\""
							+ voiceFile.getAbsolutePath() + "\"");

			FileUtils.deleteDirectory(extractDir);
			try {
				ZipFile zipFile = new ZipFile(voiceFile);
				zipFile.extractAll(extractDir.getAbsolutePath());
			} catch (ZipException e) {
				throw new IOException(e);
			}
			FileUtils.moveDirectory(extractDir, outputDir);
		}
		Log.v(TAG, "extractVoiceFileIfRequired() complete");
	}

	boolean isVoiceFileAccessible() {
		try {
			File voiceFile = getVoiceFile();
			Log.v(TAG, "isVoiceFileAccessible() " + voiceFile.getAbsolutePath());
			if (voiceFile.exists()) {
				Log.v(TAG, "isVoiceFileAccessible() true");
				return true;
			} else {
				Log.v(TAG, "isVoiceFileAccessible() false");
				return false;
			}
		} catch (IOException e) {
			Log.v(TAG, "isVoiceFileAccessible() false", e);
		}
		return false;
	}

	void startExtractVoiceFileThreadIfRequired() {
		if (!isVoiceFileAccessible()) {
			Log.v(TAG,
					"skip startExtractVoiceFileThreadIfRequired() by !isVoiceFileAccessible()");
			return;
		}
		try {
			if (getVoiceOutputDir().isDirectory()) {
				updateVoiceFileStateText("インストール済");
				return;
			}
		} catch (IOException e) {
			Log.v(TAG, "startExtractVoiceFileThreadIfRequired()", e);
		}
		new Thread() {
			@Override
			public void run() {
				try {
					updateVoiceFileStateText("展開中");
					extractVoiceFileIfRequired();
					File outputDirectory = getVoiceOutputDir();
					if (!outputDirectory.isDirectory()) {
						throw new IOException("!\""
								+ outputDirectory.getAbsolutePath()
								+ "\".isDirectory()");
					}
					updateVoiceFileStateText("インストール済");
				} catch (final IOException e) {
					Log.v(TAG, "extractVoiceFileIfRequired()", e);
					String message = "音声ファイルの展開に失敗しました。";
					updateVoiceFileStateText(message);
				}
			}
		}.start();
	}

	@Override
	public void run() {
		try {
			super.run();
		} finally {
			if (downloaderClientStub != null) {
				downloaderClientStub.disconnect(context);
			}
		}
	}
}
