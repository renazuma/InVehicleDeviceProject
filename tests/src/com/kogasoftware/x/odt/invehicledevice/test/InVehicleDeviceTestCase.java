package com.kogasoftware.x.odt.invehicledevice.test;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.google.android.testing.nativedriver.client.AndroidNativeDriver;

public class InVehicleDeviceTestCase extends NativeDriverTestCase {

	private AndroidNativeDriver driver = null;

	@Override
	protected void setUp() throws InterruptedException, IOException {
		driver = getDriver();
	}

	@Override
	protected void tearDown() {
		driver.quit();
	}

	public void testTextValue() {
		driver.startActivity("com.kogasoftware.odt.invehicledevice"
				+ ".InVehicleDeviceActivity");

		WebElement textView = driver.findElement(By.id("changeStatusButton"));
		assertEquals("Go", textView.getText());
	}

	public void testTextValue2() {
		driver.startActivity("com.kogasoftware.odt.invehicledevice"
				+ ".InVehicleDeviceActivity");

		WebElement textView = driver.findElement(By.id("changeStatusButton"));
		assertEquals("Go", textView.getText());
	}
}
