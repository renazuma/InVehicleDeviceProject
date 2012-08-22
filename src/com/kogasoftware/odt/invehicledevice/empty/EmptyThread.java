package com.kogasoftware.odt.invehicledevice.empty;

/** FindBugs警告避け用クラス */
public class EmptyThread extends Thread {
	public EmptyThread() {
		super();
		super.start(); // Android2.3ではstart()していないThreadは非常にGCされにくいようなので、start()しておく(EmptyThreadTestCaseも参照)
	}
	
	@Override
	public void start() {
		// コンストラクタでsuper.start()が呼ばれているため、二重起動を防ぐため親のメソッドを呼ばないようにする
	}
	
	@Override
	public void run() {
	}
}
