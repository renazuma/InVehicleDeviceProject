package com.kogasoftware.odt.invehicledevice.empty;

import android.os.Build;

/** FindBugs警告避け用クラス */
public class EmptyThread extends Thread {
	public EmptyThread() {
		super();
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
			super.start(); // Android2.3以前ではstart()していないThreadは非常にGCされにくいようなので、start()しておく(EmptyThreadTestCaseも参照)	
		}
	}

	@Override
	public void start() {
	}

	@Override
	public void run() {
	}
}
