package com.kogasoftware.odt.invehicledevice.test.unit.service.logservice;

import java.io.File;
import java.io.FileReader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSenderException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.service.logservice.LogServiceReportSender;
import com.kogasoftware.odt.invehicledevice.service.logservice.SendLogBroadcastReceiver;

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
		getContext().registerReceiver(br, new IntentFilter(SendLogBroadcastReceiver.ACTION_SEND_LOG));
		
		lsrs.send(crd);
		
		assertTrue(cdl.await(1, TimeUnit.SECONDS));
		String f = outputIntent.get().getStringExtra(SendLogBroadcastReceiver.EXTRAS_KEY_LOG_FILE_NAME);
		assertNotNull(f);
		FileReader fr = null;
		ObjectNode jo;
		try {
			fr = new FileReader(new File(f));
			jo = (new ObjectMapper()).readValue(fr, ObjectNode.class);
		} finally {
			Closeables.closeQuietly(fr);
		}
		
		ObjectNode got = LogServiceReportSender.getCrashReportJsonNode(crd);
		assertEquals(jo.get("app_version_code"), got.get("app_version_code"));
		assertEquals(jo.get("custom_data"), got.get("custom_data"));
	}

	public void testGetCrashReportJsonNode() throws Exception {
		CrashReportData crd = new CrashReportData();
		String avc = "12345";
		crd.put(ReportField.APP_VERSION_CODE, avc);
		String cd = "abcde";
		crd.put(ReportField.CUSTOM_DATA, cd);

		ObjectNode jo = LogServiceReportSender.getCrashReportJsonNode(crd);

		assertEquals(avc, jo.get("app_version_code").asText());
		assertEquals(cd, jo.get("custom_data").asText());
	}
}
