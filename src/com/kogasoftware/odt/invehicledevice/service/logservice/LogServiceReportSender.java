package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;

import org.acra.CrashReportData;
import org.acra.ReportField;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.google.common.base.Charsets;
import com.google.common.io.Closeables;

public class LogServiceReportSender implements ReportSender {
	private static final String TAG = LogServiceReportSender.class.getSimpleName();
	private final File dataDirectory;
	private final Context context;

	public LogServiceReportSender(Context context) {
		String s = File.separator;
		this.context = context;
		dataDirectory = new File(Environment.getExternalStorageDirectory() + s
				+ ".odt" + s + "log");
	}

	@Override
	public void send(CrashReportData crashReportData)
			throws ReportSenderException {
		String format = (new SimpleDateFormat("yyyyMMddHHmmss.SSS"))
				.format(new Date());
		File file = new File(dataDirectory, format + "_crashreport.log");
		if (!dataDirectory.canWrite()) {
			String message = "!\"" + dataDirectory + "\".canWrite()";
			throw new ReportSenderException(message, new Throwable(message));
		}
		JSONObject jsonObject = new JSONObject();
		for (Entry<ReportField, String> entry : crashReportData.entrySet()) {
			try {
				jsonObject.put(entry.getKey().toString(), entry.getValue());
			} catch (JSONException e) {
				Log.w(TAG, e);
			}
		}
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(jsonObject.toString().getBytes(Charsets.UTF_8));
		} catch (IOException e) {
			throw new ReportSenderException("IOException file=" + file, e);
		} finally {
			Closeables.closeQuietly(fileOutputStream);
		}
		Log.i(TAG, "\"" + file + "\" saved");
		Intent intent = new Intent(LogService.ACTION_SEND_LOG);
		intent.putExtra("file", file.getAbsolutePath());
		context.sendBroadcast(intent);
	}
}
