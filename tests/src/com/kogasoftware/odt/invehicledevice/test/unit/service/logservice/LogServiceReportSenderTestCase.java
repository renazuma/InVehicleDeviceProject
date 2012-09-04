package com.kogasoftware.odt.invehicledevice.test.unit.service.logservice;

import java.io.File;
import java.io.FileReader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.acra.CrashReportData;
import org.acra.ReportField;
import org.acra.sender.ReportSenderException;
import org.json.JSONObject;

import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.service.logservice.LogService;
import com.kogasoftware.odt.invehicledevice.service.logservice.LogServiceReportSender;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.test.AndroidTestCase;

public class LogServiceReportSenderTestCase extends AndroidTestCase {
	LogServiceReportSender lsrs;
	BroadcastReceiver br;

	public void tearDown() throws Exception {
		try {
			if (br != null) {
				getContext().unregisterReceiver(br);
			}
		} finally {
			super.tearDown();
		}
	}

	public void testSend_DirectoryNotFound() {
		CrashReportData crd = new CrashReportData();
		File d = new File("存在しないディレクトリ名");
		assertFalse(d.exists());
		lsrs = new LogServiceReportSender(getContext(), d);
		try {
			lsrs.send(crd);
			fail();
		} catch (ReportSenderException e) {
		}
	}

	public void testSend_Success() throws Exception {
		lsrs = new LogServiceReportSender(getContext());
		CrashReportData crd = new CrashReportData();
		String avc = "51234";
		crd.put(ReportField.APP_VERSION_CODE, avc);
		String cd = "iasoj";
		crd.put(ReportField.CUSTOM_DATA, cd);
		
		final CountDownLatch cdl = new CountDownLatch(1);
		final AtomicReference<Intent> outputIntent = new AtomicReference<Intent>();
		br = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				outputIntent.set(intent);
				cdl.countDown();
			}
		};
		getContext().registerReceiver(br, new IntentFilter(LogService.ACTION_SEND_LOG));
		
		lsrs.send(crd);
		
		assertTrue(cdl.await(1, TimeUnit.SECONDS));
		String f = outputIntent.get().getStringExtra(LogService.EXTRAS_KEY_LOG_FILE_NAME);
		assertNotNull(f);
		FileReader fr = null;
		JSONObject jo = new JSONObject();
		try {
			fr = new FileReader(new File(f));
			jo = new JSONObject(CharStreams.toString(fr));
		} finally {
			Closeables.closeQuietly(fr);
		}
		
		assertEquals(LogServiceReportSender.getCrashReportJSONObject(crd).toString(), jo.toString());
	}

	public void testGetCrashReportJSONObject() throws Exception {
		CrashReportData crd = new CrashReportData();
		String avc = "12345";
		crd.put(ReportField.APP_VERSION_CODE, avc);
		String cd = "abcde";
		crd.put(ReportField.CUSTOM_DATA, cd);

		JSONObject jo = LogServiceReportSender.getCrashReportJSONObject(crd);

		assertEquals(avc, jo.getString(ReportField.APP_VERSION_CODE.toString()));
		assertEquals(cd, jo.getString(ReportField.CUSTOM_DATA.toString()));
	}
}
