package com.kogasoftware.odt.invehicledevice.infra.contentprovider.task;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.VehicleNotification;

import org.apache.http.HttpResponse;
import org.joda.time.format.ISODateTimeFormat;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 車載器通知の更新APIとの通信
 */
public class PatchVehicleNotificationTask extends SynchronizationTask {
    private static final String TAG = PatchVehicleNotificationTask.class
            .getSimpleName();
    public static final Long INTERVAL_MILLIS = 60 * 1000L;

    public PatchVehicleNotificationTask(Context context,
                                        SQLiteDatabase database, ScheduledExecutorService executorService) {
        super(context, database, executorService);
    }

    List<ObjectNode> getNotRepliedVehicleNotifications()
            throws IllegalArgumentException {
        List<ObjectNode> nodes = Lists.newLinkedList();
        Cursor cursor = database.query(VehicleNotification.TABLE_NAME,
                new String[]{VehicleNotification.Columns._ID,
                        VehicleNotification.Columns.RESPONSE,
                        VehicleNotification.Columns.READ_AT},
                "response IS NOT NULL AND read_at IS NOT NULL", null, null,
                null, null);
        try {
            if (!cursor.moveToFirst()) {
                return nodes;
            }
            do {
                ObjectNode node = JSON.createObjectNode();
                node.put(
                        "id",
                        cursor.getLong(cursor
                                .getColumnIndexOrThrow(VehicleNotification.Columns._ID)));
                node.put(
                        "response",
                        cursor.getLong(cursor
                                .getColumnIndexOrThrow(VehicleNotification.Columns.RESPONSE)));
                Long readAt = cursor
                        .getLong(cursor
                                .getColumnIndexOrThrow(VehicleNotification.Columns.READ_AT));
                node.put("read_at", ISODateTimeFormat.dateTime().print(readAt));
                nodes.add(node);
            } while (cursor.moveToNext());
        } finally {
            cursor.close();
        }
        return nodes;
    }

    @Override
    protected void runSession(URI baseUri, String authenticationToken) {
        List<ObjectNode> nodes = getNotRepliedVehicleNotifications();
        for (ObjectNode node : nodes) {
            ObjectNode rootNode = JSON.createObjectNode();
            final Long id = node.get("id").asLong();
            rootNode.set("vehicle_notification", node);
            doHttpPatch(baseUri, "vehicle_notifications/" + id,
                    authenticationToken, rootNode, new LogCallback(TAG) {
                        @Override
                        public void onSuccess(HttpResponse response,
                                              byte[] entity) {
                            database.delete(VehicleNotification.TABLE_NAME,
                                    VehicleNotification.Columns._ID + " = ?",
                                    new String[]{id.toString()});
                        }
                    });
        }
    }
}
