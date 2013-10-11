package com.kogasoftware.odt.apiclient;

import java.io.File;
import java.util.HashMap;

import com.kogasoftware.odt.apiclient.ApiClient.ResponseConverter;
import com.kogasoftware.odt.apiclient.ApiClientCallback;
import com.kogasoftware.odt.apiclient.DefaultApiClientRequest;
import com.kogasoftware.odt.apiclient.DefaultApiClientRequestConfig;
import com.kogasoftware.odt.apiclient.DefaultApiClientRequestQueue;
import com.kogasoftware.odt.apiclient.serializablerequestloader.SerializableGetLoader;

import android.test.AndroidTestCase;

public class DefaultApiClientRequestQueueTestCase extends
		AndroidTestCase {

	public void testSetSaveOnClose() throws InterruptedException {
		final Thread mainThread = Thread.currentThread();
		File backupFile = getContext().getFileStreamPath(
				"foo");
		DefaultApiClientRequestConfig saveOnCloseConfig = new DefaultApiClientRequestConfig();
		saveOnCloseConfig.setSaveOnClose(true);
		backupFile.deleteOnExit();
		backupFile.delete();
		SerializableGetLoader sgl = new SerializableGetLoader("", "",
				new HashMap<String, String>(), "");
		ApiClientCallback<Object> c = new EmptyApiClientCallback<Object>();
		ResponseConverter<Object> rc = new ResponseConverter<Object>() {
			@Override
			public Object convert(byte[] rawResponse) throws Exception {
				return new Object();
			}
		};

		{
			DefaultApiClientRequest<Object> r1 = new DefaultApiClientRequest<Object>(c, rc, sgl);
			DefaultApiClientRequest<Object> r2 = new DefaultApiClientRequest<Object>(c, rc, sgl);
			DefaultApiClientRequest<Object> r3 = new DefaultApiClientRequest<Object>(c, rc, sgl);
			{
				DefaultApiClientRequestQueue rq = new DefaultApiClientRequestQueue(backupFile);
				r1.setConfig(saveOnCloseConfig);
				rq.add(r1);
				rq.add(r2);
				rq.add(r3);
				rq.close();
				rq.join();
			}
			{
				DefaultApiClientRequestQueue rq = new DefaultApiClientRequestQueue(backupFile);
				DefaultApiClientRequest<?> r1x = rq.take();
				assertEquals(r1.getReqKey(), r1x.getReqKey());
				try {
					new Thread() {
						public void run() {
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
							}
							mainThread.interrupt();
						}
					}.start();
					rq.take();
					fail();
				} catch (InterruptedException e) {
				}
				rq.close();
				rq.join();
			}
			backupFile.delete();
		}
		
		{
			DefaultApiClientRequest<Object> r1 = new DefaultApiClientRequest<Object>(c, rc, sgl);
			DefaultApiClientRequest<Object> r2 = new DefaultApiClientRequest<Object>(c, rc, sgl);
			DefaultApiClientRequest<Object> r3 = new DefaultApiClientRequest<Object>(c, rc, sgl);
			{
				DefaultApiClientRequestQueue rq = new DefaultApiClientRequestQueue(backupFile);
				r2.setConfig(saveOnCloseConfig);
				rq.add(r1, "x");
				rq.add(r2, "x");
				rq.add(r3);
				rq.close();
				rq.join();
			}
			{
				DefaultApiClientRequestQueue rq = new DefaultApiClientRequestQueue(backupFile);
				DefaultApiClientRequest<?> r2x = rq.take();
				assertEquals(r2.getReqKey(), r2x.getReqKey());
				try {
					new Thread() {
						public void run() {
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
							}
							mainThread.interrupt();
						}
					}.start();
					rq.take();
					fail();
				} catch (InterruptedException e) {
				}
				rq.close();
				rq.join();
			}
			backupFile.delete();
		}
		
		{
			DefaultApiClientRequest<Object> r1 = new DefaultApiClientRequest<Object>(c, rc, sgl);
			DefaultApiClientRequest<Object> r2 = new DefaultApiClientRequest<Object>(c, rc, sgl);
			DefaultApiClientRequest<Object> r3 = new DefaultApiClientRequest<Object>(c, rc, sgl);
			{
				DefaultApiClientRequestQueue rq = new DefaultApiClientRequestQueue(backupFile);
				r1.setConfig(saveOnCloseConfig);
				r3.setConfig(saveOnCloseConfig);
				rq.add(r1, "y");
				rq.add(r2);
				rq.add(r3);
				rq.close();
				rq.join();
			}
			{
				DefaultApiClientRequestQueue rq = new DefaultApiClientRequestQueue(backupFile);
				DefaultApiClientRequest<?> r3x = rq.take(); // グループが違う場合後勝ち
				DefaultApiClientRequest<?> r1x = rq.take();
				assertEquals(r1.getReqKey(), r1x.getReqKey());
				assertEquals(r3.getReqKey(), r3x.getReqKey());
				try {
					new Thread() {
						public void run() {
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
							}
							mainThread.interrupt();
						}
					}.start();
					rq.take();
					fail();
				} catch (InterruptedException e) {
				}
				rq.close();
				rq.join();
			}
			backupFile.delete();
		}
		
		{
			DefaultApiClientRequest<Object> r1 = new DefaultApiClientRequest<Object>(c, rc, sgl);
			DefaultApiClientRequest<Object> r2 = new DefaultApiClientRequest<Object>(c, rc, sgl);
			DefaultApiClientRequest<Object> r3 = new DefaultApiClientRequest<Object>(c, rc, sgl);
			{
				DefaultApiClientRequestQueue rq = new DefaultApiClientRequestQueue(backupFile);
				r1.setConfig(saveOnCloseConfig);
				r3.setConfig(saveOnCloseConfig);
				rq.add(r1, "z");
				rq.add(r2);
				rq.add(r3, "z");
				rq.close();
				rq.join();
			}
			{
				DefaultApiClientRequestQueue rq = new DefaultApiClientRequestQueue(backupFile);
				DefaultApiClientRequest<?> r1x = rq.take(); // グループが同じ場合先勝ち
				assertEquals(r1.getReqKey(), r1x.getReqKey());
				
				try {
					new Thread() {
						public void run() {
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
							}
							mainThread.interrupt();
						}
					}.start();
					rq.take();
					fail();
				} catch (InterruptedException e) {
				}
				rq.remove(r1x);
				DefaultApiClientRequest<?> r3x = rq.take();
				assertEquals(r3.getReqKey(), r3x.getReqKey());
				
				try {
					new Thread() {
						public void run() {
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
							}
							mainThread.interrupt();
						}
					}.start();
					rq.take();
					fail();
				} catch (InterruptedException e) {
				}
				rq.close();
				rq.join();
			}
			backupFile.delete();
		}
	}
}
