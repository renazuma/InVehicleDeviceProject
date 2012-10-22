package com.kogasoftware.odt.apiclient.test;

import java.io.File;
import java.util.HashMap;

import com.kogasoftware.odt.apiclient.ApiClient.ResponseConverter;
import com.kogasoftware.odt.apiclient.ApiClientCallback;
import com.kogasoftware.odt.apiclient.ApiClientRequest;
import com.kogasoftware.odt.apiclient.ApiClientRequestConfig;
import com.kogasoftware.odt.apiclient.ApiClientRequestQueue;
import com.kogasoftware.odt.apiclient.serializablerequestloader.SerializableGetLoader;

import android.test.AndroidTestCase;

public class ApiClientRequestQueueTestCase extends
		AndroidTestCase {

	public void testSetSaveOnClose() throws InterruptedException {
		final Thread mainThread = Thread.currentThread();
		File backupFile = getContext().getFileStreamPath(
				"foo");
		ApiClientRequestConfig saveOnCloseConfig = new ApiClientRequestConfig();
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
			ApiClientRequest<Object> r1 = new ApiClientRequest<Object>(c, rc, sgl);
			ApiClientRequest<Object> r2 = new ApiClientRequest<Object>(c, rc, sgl);
			ApiClientRequest<Object> r3 = new ApiClientRequest<Object>(c, rc, sgl);
			{
				ApiClientRequestQueue rq = new ApiClientRequestQueue(backupFile);
				r1.setConfig(saveOnCloseConfig);
				rq.add(r1);
				rq.add(r2);
				rq.add(r3);
			}
			{
				ApiClientRequestQueue rq = new ApiClientRequestQueue(backupFile);
				ApiClientRequest<?> r1x = rq.take();
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
			}
			backupFile.delete();
		}
		
		{
			ApiClientRequest<Object> r1 = new ApiClientRequest<Object>(c, rc, sgl);
			ApiClientRequest<Object> r2 = new ApiClientRequest<Object>(c, rc, sgl);
			ApiClientRequest<Object> r3 = new ApiClientRequest<Object>(c, rc, sgl);
			{
				ApiClientRequestQueue rq = new ApiClientRequestQueue(backupFile);
				r2.setConfig(saveOnCloseConfig);
				rq.add(r1, "x");
				rq.add(r2, "x");
				rq.add(r3);
			}
			{
				ApiClientRequestQueue rq = new ApiClientRequestQueue(backupFile);
				ApiClientRequest<?> r2x = rq.take();
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
			}
			backupFile.delete();
		}
		
		{
			ApiClientRequest<Object> r1 = new ApiClientRequest<Object>(c, rc, sgl);
			ApiClientRequest<Object> r2 = new ApiClientRequest<Object>(c, rc, sgl);
			ApiClientRequest<Object> r3 = new ApiClientRequest<Object>(c, rc, sgl);
			{
				ApiClientRequestQueue rq = new ApiClientRequestQueue(backupFile);
				r1.setConfig(saveOnCloseConfig);
				r3.setConfig(saveOnCloseConfig);
				rq.add(r1, "y");
				rq.add(r2);
				rq.add(r3);
			}
			{
				ApiClientRequestQueue rq = new ApiClientRequestQueue(backupFile);
				ApiClientRequest<?> r3x = rq.take(); // グループが違う場合後勝ち
				ApiClientRequest<?> r1x = rq.take();
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
			}
			backupFile.delete();
		}
		
		{
			ApiClientRequest<Object> r1 = new ApiClientRequest<Object>(c, rc, sgl);
			ApiClientRequest<Object> r2 = new ApiClientRequest<Object>(c, rc, sgl);
			ApiClientRequest<Object> r3 = new ApiClientRequest<Object>(c, rc, sgl);
			{
				ApiClientRequestQueue rq = new ApiClientRequestQueue(backupFile);
				r1.setConfig(saveOnCloseConfig);
				r3.setConfig(saveOnCloseConfig);
				rq.add(r1, "z");
				rq.add(r2);
				rq.add(r3, "z");
			}
			{
				ApiClientRequestQueue rq = new ApiClientRequestQueue(backupFile);
				ApiClientRequest<?> r1x = rq.take(); // グループが同じ場合先勝ち
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
				ApiClientRequest<?> r3x = rq.take();
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
			}
			backupFile.delete();
		}
	}
}
