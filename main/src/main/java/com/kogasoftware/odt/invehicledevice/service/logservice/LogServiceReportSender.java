package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map.Entry;

import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.annotations.VisibleForTesting;
import com.kogasoftware.odt.invehicledevice.empty.EmptyFile;

public class LogServiceReportSender implements ReportSender {
	private static final String TAG = LogServiceReportSender.class
			.getSimpleName();
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private static ObjectMapper getObjectMapper() {
		return OBJECT_MAPPER;
	}

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
		try {
			file = File
					.createTempFile(format + "_acra_", ".log", dataDirectory);
			getObjectMapper().writeValue(file,
					getCrashReportJsonNode(crashReportData));
		} catch (IOException e) {
			throw new ReportSenderException("IOException file=" + file
					+ " dataDirectory=" + dataDirectory, e);
		}
		Log.i(TAG, "\"" + file + "\" saved");
		Intent intent = new Intent(SendLogBroadcastReceiver.ACTION_SEND_LOG);
		intent.putExtra(SendLogBroadcastReceiver.EXTRAS_KEY_LOG_FILE_NAME,
				file.getAbsolutePath());
		context.sendBroadcast(intent);
	}

	@VisibleForTesting
	public static ObjectNode getCrashReportJsonNode(
			CrashReportData crashReportData) {
		ObjectNode objectNode = getObjectMapper().createObjectNode();
		for (Entry<ReportField, String> entry : crashReportData.entrySet()) {
			objectNode.put(entry.getKey().toString()
					.toLowerCase(Locale.ENGLISH), entry.getValue());
		}
		return objectNode;
	}
}