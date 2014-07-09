package com.kogasoftware.odt.invehicledevice.contentprovider.task;

import java.util.concurrent.ScheduledExecutorService;

import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kogasoftware.odt.invehicledevice.contentprovider.table.OperationSchedules;

public class NewDateCheckTask implements Runnable {
	public static final Long INTERVAL_MILLIS = 300 * 1000L;
	private final SQLiteDatabase database;
	private final ScheduledExecutorService executorService;
	private final Context context;

	public NewDateCheckTask(Context context, SQLiteDatabase database,
			ScheduledExecutorService executorService) {
		this.context = context;
		this.database = database;
		this.executorService = executorService;
	}

	@Override
	public void run() {
		Cursor cursor = database.query(OperationSchedules.TABLE_NAME,
				new String[]{OperationSchedules.Columns.OPERATION_DATE}, null,
				null, null, null, OperationSchedules.Columns._ID, "1");
		try {
			if (cursor.moveToFirst()) {
				Long lastMillis = cursor
						.getLong(cursor
								.getColumnIndexOrThrow(OperationSchedules.Columns.OPERATION_DATE));
				LocalDate lastDate = new LocalDate(lastMillis, DateTimeZone.UTC);
				if (Days.daysBetween(lastDate, LocalDate.now()).getDays() >= 1) {
					executorService.execute(new GetOperationSchedulesTask(
							context, database, executorService));
				}
			} else {
				executorService.execute(new GetOperationSchedulesTask(context,
						database, executorService));
			}
		} finally {
			cursor.close();
		}
	}
}
