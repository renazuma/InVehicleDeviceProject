package com.kogasoftware.odt.webapi.test;

import java.io.File;
import java.util.HashMap;

import com.kogasoftware.odt.webapi.WebAPI.ResponseConverter;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIRequest;
import com.kogasoftware.odt.webapi.WebAPIRequestQueue;
import com.kogasoftware.odt.webapi.serializablerequestloader.SerializableGetLoader;

import android.test.AndroidTestCase;

public class WebAPIRequestQueueTestCase extends
		AndroidTestCase {
	private static final String TAG = WebAPITestCase.class.getSimpleName();

	public void testSetSaveOnClose() throws InterruptedException {
		final Thread mainThread = Thread.currentThread();
		File backupFile = getContext().getFileStreamPath(
				"foo");
		backupFile.deleteOnExit();
		backupFile.delete();
		SerializableGetLoader sgl = new SerializableGetLoader("", "",
				new HashMap<String, String>(), "");
		WebAPICallback<Object> c = new EmptyWebAPICallback<Object>();
		ResponseConverter<Object> rc = new ResponseConverter<Object>() {
			@Override
			public Object convert(byte[] rawResponse) throws Exception {
				return new Object();
			}
		};

		{
			WebAPIRequest<Object> r1 = new WebAPIRequest<Object>(c, rc, sgl);
			WebAPIRequest<Object> r2 = new WebAPIRequest<Object>(c, rc, sgl);
			WebAPIRequest<Object> r3 = new WebAPIRequest<Object>(c, rc, sgl);
			{
				WebAPIRequestQueue rq = new WebAPIRequestQueue(backupFile);
				rq.add(r1);
				rq.add(r2);
				rq.add(r3);
				rq.setSaveOnClose(r1.getReqKey(), true);
			}
			{
				WebAPIRequestQueue rq = new WebAPIRequestQueue(backupFile);
				WebAPIRequest<?> r1x = rq.take();
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
			WebAPIRequest<Object> r1 = new WebAPIRequest<Object>(c, rc, sgl);
			WebAPIRequest<Object> r2 = new WebAPIRequest<Object>(c, rc, sgl);
			WebAPIRequest<Object> r3 = new WebAPIRequest<Object>(c, rc, sgl);
			{
				WebAPIRequestQueue rq = new WebAPIRequestQueue(backupFile);
				rq.add(r1, "x");
				rq.add(r2, "x");
				rq.add(r3);
				rq.setSaveOnClose(r2.getReqKey(), true);
			}
			{
				WebAPIRequestQueue rq = new WebAPIRequestQueue(backupFile);
				WebAPIRequest<?> r2x = rq.take();
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
			WebAPIRequest<Object> r1 = new WebAPIRequest<Object>(c, rc, sgl);
			WebAPIRequest<Object> r2 = new WebAPIRequest<Object>(c, rc, sgl);
			WebAPIRequest<Object> r3 = new WebAPIRequest<Object>(c, rc, sgl);
			{
				WebAPIRequestQueue rq = new WebAPIRequestQueue(backupFile);
				rq.add(r1, "y");
				rq.add(r2);
				rq.add(r3);
				rq.setSaveOnClose(r1.getReqKey(), true);
				rq.setSaveOnClose(r3.getReqKey(), true);
			}
			{
				WebAPIRequestQueue rq = new WebAPIRequestQueue(backupFile);
				WebAPIRequest<?> r3x = rq.take(); // グループが違う場合後勝ち
				WebAPIRequest<?> r1x = rq.take();
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
			WebAPIRequest<Object> r1 = new WebAPIRequest<Object>(c, rc, sgl);
			WebAPIRequest<Object> r2 = new WebAPIRequest<Object>(c, rc, sgl);
			WebAPIRequest<Object> r3 = new WebAPIRequest<Object>(c, rc, sgl);
			{
				WebAPIRequestQueue rq = new WebAPIRequestQueue(backupFile);
				rq.add(r1, "z");
				rq.add(r2);
				rq.add(r3, "z");
				rq.setSaveOnClose(r1.getReqKey(), true);
				rq.setSaveOnClose(r3.getReqKey(), true);
			}
			{
				WebAPIRequestQueue rq = new WebAPIRequestQueue(backupFile);
				WebAPIRequest<?> r1x = rq.take(); // グループが同じ場合先勝ち
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
				WebAPIRequest<?> r3x = rq.take();
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
