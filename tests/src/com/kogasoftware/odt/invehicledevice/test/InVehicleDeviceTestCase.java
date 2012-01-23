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
		WebElement s = driver.findElement(By.id("statusTextView"));
		assertEquals("走行中", s.getText());
	}

	public void test到着ボタンを押すと停車中と表示() {
		WebElement b = driver.findElement(By.id("changeStatusButton"));
		b.click();
		WebElement s = driver.findElement(By.id("statusTextView"));
		assertEquals("停車中", s.getText());
	}

	public void test停留所画面で出発ボタンを押すと確認画面が表示() {
		test到着ボタンを押すと停車中と表示(); // 停車状態にする

		WebElement s = driver.findElement(By.id("checkStartOverlay"));
		assertFalse(s.isEnabled());
		WebElement b = driver.findElement(By.id("changeStatusButton"));
		b.click();
		assertTrue(s.isEnabled());
	}

	public void test停留所画面で出発ボタンを押すと確認画面が表示ーはい() {
		test到着ボタンを押すと停車中と表示(); // 停車状態にする
		WebElement b = driver.findElement(By.id("startButton"));
		b.click();
		WebElement s = driver.findElement(By.id("statusTextView"));
		assertEquals("走行中", s.getText());
		assertFalse(driver.findElement(By.id("checkStartLayout")).isEnabled());
	}

	public void test停留所画面で出発ボタンを押すと確認画面が表示ーいいえ() {
		test到着ボタンを押すと停車中と表示(); // 停車状態にする
		WebElement b = driver.findElement(By.id("startCancelButton"));
		b.click();
		WebElement s = driver.findElement(By.id("statusTextView"));
		assertEquals("停車中", s.getText());
		assertFalse(driver.findElement(By.id("checkStartLayout")).isEnabled());
	}
}
