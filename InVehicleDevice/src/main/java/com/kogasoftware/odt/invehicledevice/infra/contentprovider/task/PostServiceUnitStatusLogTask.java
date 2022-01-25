package com.kogasoftware.odt.invehicledevice.infra.contentprovider.task;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.kogasoftware.android.CursorReader;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.ServiceUnitStatusLog;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.ServiceUnitStatusLog.Columns;

import org.apache.http.HttpResponse;
import org.joda.time.DateTime;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * ServiceUnitStatusLogの新規作成APIとの通信
 */
public class PostServiceUnitStatusLogTask extends SynchronizationTask {
    static final String TAG = PostServiceUnitStatusLogTask.class
            .getSimpleName();
    public static final Integer INTERVAL_MILLIS = 30 * 1000;

    public PostServiceUnitStatusLogTask(Context context,
                                        SQLiteDatabase database, ScheduledExecutorService executorService) {
        super(context, database, executorService);
    }

    // InsertServiceStatusLogTaskのinterval秒以前よりも前のデータのリストを取得
    // InsertServiceStatusLogTaskのinterval秒内のデータ（最新データ）は、過去データをDBで所持し続けるために削除出来ないっぽい。。
    // TODO: 仕様が複雑すぎるので変えたい。
    List<ObjectNode> getServiceUnitStatusLogs() throws IllegalArgumentException {
        List<ObjectNode> nodes = Lists.newLinkedList();
        Long millis = DateTime.now().minusMillis(InsertServiceUnitStatusLogTask.INTERVAL_MILLIS).getMillis();
        String where = ServiceUnitStatusLog.Columns.CREATED_AT + " < " + millis;
        Cursor cursor = database.query(ServiceUnitStatusLog.TABLE_NAME, null,
                where, null, null, null, null);
        try {
            if (!cursor.moveToFirst()) {
                return nodes;
            }
            do {
                ObjectNode node = JSON.createObjectNode();
                CursorReader reader = new CursorReader(cursor);
                node.put("id", reader.readLong(Columns._ID));
                node.put(Columns.ORIENTATION, reader.readLong(Columns.ORIENTATION));
                node.put(Columns.TEMPERATURE, reader.readLong(Columns.TEMPERATURE));
                String createdAt = reader.readDateTime(Columns.CREATED_AT).toString();
                node.put(Columns.CREATED_AT, createdAt);
                node.put("offline_time", createdAt);
                node.put(Columns.LATITUDE, reader.readString(Columns.LATITUDE));
                node.put(Columns.LONGITUDE, reader.readBigDecimal(Columns.LONGITUDE));
                node.put(Columns.SIGNAL_STRENGTH, reader.readLong(Columns.SIGNAL_STRENGTH));
                nodes.add(node);
            } while (cursor.moveToNext());
        } finally {
            cursor.close();
        }
        return nodes;
    }

    @Override
    protected void runSession(URI baseUri, String authenticationToken) {
        for (ObjectNode node : getServiceUnitStatusLogs()) {
            ObjectNode rootNode = JSON.createObjectNode();

            // TODO: サーバー側を修正
            if (node.path("latitude").isNull()) {
                node.put("latitude", 0);
            }
            if (node.path("longitude").isNull()) {
                node.put("longitude", 0);
            }
            node.put("offline_time", node.path("created_at").asText());

            final Long id = node.get("id").asLong();
            rootNode.set("service_unit_status_log", node);
            doHttpPost(baseUri, "service_unit_status_logs",
                    authenticationToken, rootNode, new LogCallback(TAG) {
                        @Override
                        public void onSuccess(HttpResponse response,
                                              byte[] entity) {
                            database.delete(ServiceUnitStatusLog.TABLE_NAME,
                                    ServiceUnitStatusLog.Columns._ID + " = ?",
                                    new String[]{id.toString()});
                        }
                    });
        }
    }
}
