package com.kogasoftware.odt.invehicledevice.service.voicedownloaderservice;

import com.google.android.vending.expansion.downloader.DownloaderClientMarshaller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * 音声合成用ファイルダウンロードを開始する
 */
public class VoiceDownloadAlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			DownloaderClientMarshaller.startDownloadServiceIfRequired(context,
					intent, VoiceDownloaderService.class);
		} catch (NameNotFoundException e) {
			throw new RuntimeException(e); // Critical bug
		}
	}
}
