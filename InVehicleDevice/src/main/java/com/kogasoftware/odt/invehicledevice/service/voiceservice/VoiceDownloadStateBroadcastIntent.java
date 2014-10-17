package com.kogasoftware.odt.invehicledevice.service.voiceservice;

import android.content.Intent;

/**
 * 音声合成用ファイルのダウンロード状況の通知
 */
public class VoiceDownloadStateBroadcastIntent extends Intent {
	public static final String ACTION = VoiceDownloadStateBroadcastIntent.class
			.getName();
	private static final String MESSAGE_KEY = ACTION + ":message";

	public VoiceDownloadStateBroadcastIntent(String message) {
		super(ACTION);
		putExtra(MESSAGE_KEY, message);
	}

	public static VoiceDownloadStateBroadcastIntent of(Intent intent) {
		return new VoiceDownloadStateBroadcastIntent(
				intent.getStringExtra(MESSAGE_KEY));
	}

	public String getMessage() {
		return getStringExtra(MESSAGE_KEY);
	}
}
