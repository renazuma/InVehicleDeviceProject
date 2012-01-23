package com.kogasoftware.odt.invehicledevice.test;

import java.io.IOException;

import org.openqa.selenium.By;

import com.google.android.testing.nativedriver.client.AndroidNativeDriver;
import com.google.android.testing.nativedriver.client.AndroidNativeElement;

public class InVehicleDeviceTestCase extends NativeDriverTestCase {

	private AndroidNativeDriver driver = null;

	@Override
	protected void setUp() throws InterruptedException, IOException {
		driver = getDriver();
		driver.startActivity("com.kogasoftware.odt.invehicledevice"
				+ ".InVehicleDeviceActivity");
	}

	@Override
	protected void tearDown() {
		driver.quit();
	}

	public void test起動時は走行中と表示() {
		AndroidNativeElement s = driver.findElement(By.id("status_text_view"));
		assertEquals("走行中", s.getText());
	}

	public void test到着ボタンを押すと停車中と表示() {
		AndroidNativeElement b = driver.findElement(By
				.id("change_status_button"));
		b.click();
		AndroidNativeElement s = driver.findElement(By.id("status_text_view"));
		assertEquals("停車中", s.getText());
	}

	public void xtest停留所画面で出発ボタンを押すと確認画面が表示() {
		test到着ボタンを押すと停車中と表示(); // 停車状態にする

		AndroidNativeElement s = driver
				.findElement(By.id("check_start_layout"));
		String a = s.getAttribute("android:layout_width");
		String bx = s.getAttribute("layout_width");
		String c = s.getAttribute("android:id");
		String d = s.getAttribute("id");

		assertEquals("gone", s.getAttribute("visibility"));
		AndroidNativeElement b = driver.findElement(By
				.id("change_status_button"));
		b.click();
		assertEquals("visible", s.getAttribute("android:visibility"));
	}

	public void xtest停留所画面で出発ボタンを押すと確認画面が表示ーはい() {
		xtest停留所画面で出発ボタンを押すと確認画面が表示();
		AndroidNativeElement b = driver.findElement(By.id("start_button"));
		b.click();
		AndroidNativeElement s = driver.findElement(By.id("status_text_view"));
		assertEquals("走行中", s.getText());
		assertEquals("gone", driver.findElement(By.id("check_start_layout"))
				.getAttribute("android:visibility"));
	}

	public void xtest停留所画面で出発ボタンを押すと確認画面が表示ーいいえ() {
		xtest停留所画面で出発ボタンを押すと確認画面が表示();
		AndroidNativeElement b = driver.findElement(By
				.id("start_cancel_button"));
		b.click();
		AndroidNativeElement s = driver.findElement(By.id("status_text_view"));
		assertEquals("停車中", s.getText());
		assertEquals("gone", driver.findElement(By.id("check_start_layout"))
				.getAttribute("android:visibility"));
	}
}
