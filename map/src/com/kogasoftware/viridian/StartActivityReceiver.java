package com.kogasoftware.viridian;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartActivityReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// 「MainActivity.class」を起動するアクティビティに指定
		Intent activity = new Intent(context, MainActivity.class);
		// アクティビティを起動するための定数を指定
		activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// アクティビティを起動する
		context.startActivity(activity);
	}
}