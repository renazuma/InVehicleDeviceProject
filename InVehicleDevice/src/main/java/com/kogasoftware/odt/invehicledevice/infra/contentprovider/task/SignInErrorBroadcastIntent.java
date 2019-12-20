package com.kogasoftware.odt.invehicledevice.infra.contentprovider.task;

import android.content.Intent;

/**
 * サインイン失敗通知
 */
public class SignInErrorBroadcastIntent extends Intent {
	public static final String ACTION = SignInErrorBroadcastIntent.class
			.getName();
	private static final String MESSAGE_KEY = ACTION + ":message";

	public SignInErrorBroadcastIntent(String message) {
		super(ACTION);
		putExtra(MESSAGE_KEY, message);
	}

	public static SignInErrorBroadcastIntent of(Intent intent) {
		return new SignInErrorBroadcastIntent(
				intent.getStringExtra(MESSAGE_KEY));
	}

	public String getMessage() {
		return getStringExtra(MESSAGE_KEY);
	}
}
