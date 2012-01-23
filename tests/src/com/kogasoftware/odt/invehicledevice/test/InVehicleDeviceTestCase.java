package com.kogasoftware.odt.invehicledevice.test;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.google.android.testing.nativedriver.client.AndroidNativeDriver;

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
		WebElement s = driver.findElement(By.id("status_text_view"));
		assertEquals("走行中", s.getText());
	}

	public void test到着ボタンを押すと停車中と表示() {
		WebElement b = driver.findElement(By.id("change_status_button"));
		b.click();
		WebElement s = driver.findElement(By.id("status_text_view"));
		assertEquals("停車中", s.getText());
	}

	public void xtest停留所画面で出発ボタンを押すと確認画面が表示() {
		test到着ボタンを押すと停車中と表示(); // 停車状態にする

		WebElement s = driver.findElement(By.id("check_start_overlay"));
		assertFalse(s.isEnabled());
		WebElement b = driver.findElement(By.id("change_status_button"));
		b.click();
		assertTrue(s.isEnabled());
	}

	public void xtest停留所画面で出発ボタンを押すと確認画面が表示ーはい() {
		test到着ボタンを押すと停車中と表示(); // 停車状態にする
		WebElement b = driver.findElement(By.id("start_button"));
		b.click();
		WebElement s = driver.findElement(By.id("status_text_view"));
		assertEquals("走行中", s.getText());
		assertFalse(driver.findElement(By.id("check_start_layout")).isEnabled());
	}

	public void xtest停留所画面で出発ボタンを押すと確認画面が表示ーいいえ() {
		test到着ボタンを押すと停車中と表示(); // 停車状態にする
		WebElement b = driver.findElement(By.id("start_cancel_button"));
		b.click();
		WebElement s = driver.findElement(By.id("status_text_view"));
		assertEquals("停車中", s.getText());
		assertFalse(driver.findElement(By.id("check_start_layout")).isEnabled());
	}
}
