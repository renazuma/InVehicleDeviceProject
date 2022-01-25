package com.kogasoftware.odt.invehicledevice.infra.contentprovider.task;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.ServiceUnitStatusLog;

import org.joda.time.DateTime;

/**
 * ServiceUnitStatusLogのデータをローカルのDBに事前に用意する
 * <p>
 * TODO: insertは値の継続のために、post時の削除対象に入らない最新データの複製を作ってるイメージっぽい。
 * TODO: であれば、定期的にコピーするのではなく、post時最新のデータだけ範囲に入れない様にする方が良いのでは。
 */
public class InsertServiceUnitStatusLogTask implements Runnable {
    public static final Integer INTERVAL_MILLIS = 30 * 1000;
    private final SQLiteDatabase database;

    public InsertServiceUnitStatusLogTask(SQLiteDatabase database) {
        this.database = database;
    }

    @Override
    public void run() {
        ContentValues values = new ContentValues();
        // 全データを取得しているが、同期済みのものは既に削除されている。
        // 同期対象のデータはこのクラスのINTERVAL_MILLIS時間分だけ同期対象に入らないため、必ず1件は残っている想定
        Cursor cursor = database.query(ServiceUnitStatusLog.TABLE_NAME, null,
                null, null, null, null,
                ServiceUnitStatusLog.Columns.CREATED_AT + " DESC");
        try {
            if (cursor.moveToFirst()) {
                DatabaseUtils.cursorRowToContentValues(cursor, values);
                values.remove(ServiceUnitStatusLog.Columns._ID);
            }
        } finally {
            cursor.close();
        }
        values.put(ServiceUnitStatusLog.Columns.CREATED_AT, DateTime.now().getMillis() + INTERVAL_MILLIS);
        database.insert(ServiceUnitStatusLog.TABLE_NAME, null, values);
    }
}
