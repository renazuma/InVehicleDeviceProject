package com.kogasoftware.odt.invehicledevice.infra.contentprovider.task;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Charsets;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.DefaultCharge;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.ServiceProvider;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.ZenrinMapsAccount;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ScheduledExecutorService;

/**
 * サービスプロバイダーの取得APIとの通信
 */
public class GetServiceProviderTask extends SynchronizationTask {
    private static final String TAG = GetServiceProviderTask.class.getSimpleName();

    public GetServiceProviderTask(Context context, SQLiteDatabase database,
                                  ScheduledExecutorService executorService) {
        super(context, database, executorService);
    }

    @Override
    protected void runSession(URI baseUri, String authenticationToken) {
        doHttpGet(baseUri, "service_provider", authenticationToken,
                new LogCallback(TAG) {
                    @Override
                    public void onSuccess(HttpResponse response, byte[] entity) {
                        save(new String(entity, Charsets.UTF_8));
                    }

                    @Override
                    public void onException(IOException e) {
                        super.onException(e);
                        submitRetry();
                    }

                    @Override
                    public void onFailure(HttpResponse response, byte[] entity) {
                        super.onFailure(response, entity);
                        submitRetry();
                    }
                });
    }

    private void save(String entity) {
        JsonNode node;
        try {
            node = JSON.readTree(entity);
        } catch (IOException e) {
            Log.e(TAG, "IOException while parsing entity: " + entity, e);
            submitRetry();
            return;
        }
        long id;
        try {
            database.beginTransaction();
            database.delete(ServiceProvider.TABLE_NAME, null, null);
            ContentValues values = new ContentValues();
            values.put(ServiceProvider.Columns._ID, node.path("id").asInt());
            values.put(ServiceProvider.Columns.NAME, node.path("name").asText());
            JsonNode accessKey = node.path("log_access_key_id_aws");
            if (accessKey.isTextual()) {
                values.put(ServiceProvider.Columns.LOG_ACCESS_KEY_ID_AWS, accessKey.asText());
            }
            JsonNode secretAccessKey = node.path("log_secret_access_key_aws");
            if (secretAccessKey.isTextual()) {
                values.put(ServiceProvider.Columns.LOG_SECRET_ACCESS_KEY_AWS, secretAccessKey.asText());
            }
            values.put(ServiceProvider.Columns.CAR_NAVIGATION_APP, node.path("car_navigation_app").asText());
            values.put(ServiceProvider.Columns.ZENRIN_MAPS, node.path("zenrin_maps").asInt());
            id = database.insertOrThrow(ServiceProvider.TABLE_NAME, null, values);

            database.delete(DefaultCharge.TABLE_NAME, null, null);
            JsonNode default_charges_node = node.path("default_charges");
            if (default_charges_node.isArray()) {
                for (JsonNode default_charge_node : node.path("default_charges")) {
                    ContentValues default_charge_values = new ContentValues();
                    default_charge_values.put(DefaultCharge.Columns._ID, default_charge_node.path("id").asInt());
                    default_charge_values.put(DefaultCharge.Columns.VALUE, default_charge_node.path("value").asInt());
                    database.insertOrThrow(DefaultCharge.TABLE_NAME, null, default_charge_values);
                }
            }

            database.delete(ZenrinMapsAccount.TABLE_NAME, null, null);
            JsonNode zenrinMapsAccountNode = node.path("zenrin_maps_account");
            if (!zenrinMapsAccountNode.isNull()) {
                ContentValues zenrinMapsAccountValues = new ContentValues();
                zenrinMapsAccountValues.put(ZenrinMapsAccount.Columns._ID, zenrinMapsAccountNode.path("id").asInt());
                zenrinMapsAccountValues.put(ZenrinMapsAccount.Columns.USER_ID, zenrinMapsAccountNode.path("user_id").asText());
                zenrinMapsAccountValues.put(ZenrinMapsAccount.Columns.PASSWORD, zenrinMapsAccountNode.path("password").asText());
                zenrinMapsAccountValues.put(ZenrinMapsAccount.Columns.SERVICE_ID, zenrinMapsAccountNode.path("service_id").asText());
                zenrinMapsAccountValues.put(ZenrinMapsAccount.Columns.ZENRIN_MAPS_API_HOST, zenrinMapsAccountNode.path("zenrin_maps_api_host").asText());
                database.insertOrThrow(ZenrinMapsAccount.TABLE_NAME, null, zenrinMapsAccountValues);
            }

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
        contentResolver.notifyChange(
                ContentUris.withAppendedId(ServiceProvider.CONTENT.URI, id), null);
        contentResolver.notifyChange(
                ContentUris.withAppendedId(DefaultCharge.CONTENT.URI, id), null);
    }
}
