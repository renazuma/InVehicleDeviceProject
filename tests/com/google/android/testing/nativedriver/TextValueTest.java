/*
Copyright 2011 NativeDriver committers
Copyright 2011 Google Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.google.android.testing.nativedriver;

import junit.framework.TestCase;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.google.android.testing.nativedriver.client.AndroidNativeDriver;
import com.google.android.testing.nativedriver.client.AndroidNativeDriverBuilder;

/**
 * An integration test which sets and gets the text of a {@code TextView}.
 * 
 * @author Matt DeVore
 */
public class TextValueTest extends TestCase {
	private AndroidNativeDriver driver;

	@Override
	protected void setUp() {
		driver = getDriver();
	}

	@Override
	protected void tearDown() {
		driver.quit();
	}

	protected AndroidNativeDriver getDriver() {
		return new AndroidNativeDriverBuilder().withDefaultServer().build();
	}

	public void testTextValue2() {

	}

	public void testTextValue() {
		driver.startActivity("com.kogasoftware.odt.invehicledevice"
				+ ".InVehicleDeviceActivity");

		WebElement textView = driver.findElement(By.id("changeStatusButton"));
		assertEquals("Go", textView.getText());
	}

}
