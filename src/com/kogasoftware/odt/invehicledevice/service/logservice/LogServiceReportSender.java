package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.empty.EmptyFile;

public class LogServiceReportSender implements ReportSender {
	private static final String TAG = LogServiceReportSender.class
			.getSimpleName();
	private final File dataDirectory;
	private final Context context;

	public LogServiceReportSender(Context context) {
		this(context, new File(Environment.getExternalStorageDirectory()
				+ File.separator + ".odt" + File.separator + "log"));
	}

	@VisibleForTesting
	public LogServiceReportSender(Context context, File dataDirectory) {
		this.context = context;
		this.dataDirectory = dataDirectory;
	}

	@Override
	public void send(CrashReportData crashReportData)
			throws ReportSenderException {
		String format = (new SimpleDateFormat("yyyyMMddHHmmss.SSS"))
				.format(new Date());
		File file = new EmptyFile();
		OutputStream fileOutputStream = null;
		try {
			file = File
					.createTempFile(format + "_acra_", ".log", dataDirectory);
			fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(getCrashReportJSONObject(crashReportData)
					.toString().getBytes(Charsets.UTF_8));
		} catch (IOException e) {
			throw new ReportSenderException("IOException file=" + file
					+ " dataDirectory=" + dataDirectory, e);
		} finally {
			Closeables.closeQuietly(fileOutputStream);
		}

		Log.i(TAG, "\"" + file + "\" saved");
		Intent intent = new Intent(SendLogBroadcastReceiver.ACTION_SEND_LOG);
		intent.putExtra(SendLogBroadcastReceiver.EXTRAS_KEY_LOG_FILE_NAME,
				file.getAbsolutePath());
		context.sendBroadcast(intent);
	}

	@VisibleForTesting
	public static JSONObject getCrashReportJSONObject(
			CrashReportData crashReportData) {
		JSONObject jsonObject = new JSONObject();
		for (Entry<ReportField, String> entry : crashReportData.entrySet()) {
			try {
				jsonObject.put(entry.getKey().toString().toLowerCase(),
						entry.getValue());
			} catch (JSONException e) {
				Log.w(TAG, e);
			}
		}
		return jsonObject;
	}
}
