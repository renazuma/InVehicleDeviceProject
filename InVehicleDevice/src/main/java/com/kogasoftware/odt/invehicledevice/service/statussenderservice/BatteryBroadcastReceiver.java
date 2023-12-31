package com.kogasoftware.odt.invehicledevice.service.statussenderservice;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

import com.google.common.base.Strings;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.ServiceUnitStatusLog;

/**
 * バッテリーの状況をログ
 */
public class BatteryBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = BatteryBroadcastReceiver.class
            .getSimpleName();
    private final Boolean useBatteryTemperature;
    private final ContentResolver contentResolver;

    public BatteryBroadcastReceiver(ContentResolver contentResolver,
                                    Boolean useBatteryTemperature) {
        this.useBatteryTemperature = useBatteryTemperature;
        this.contentResolver = contentResolver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return;
        } else if (!action.equals(Intent.ACTION_BATTERY_CHANGED)) {
            return;
        }

        int status = intent.getIntExtra("status", 0);
        int health = intent.getIntExtra("health", 0);
        boolean present = intent.getBooleanExtra("present", false);
        int level = intent.getIntExtra("level", 0);
        int scale = intent.getIntExtra("scale", 0);
        int iconSmall = intent.getIntExtra("icon-small", 0);
        int plugged = intent.getIntExtra("plugged", 0);
        int voltage = intent.getIntExtra("voltage", 0);
        double temperature = intent.getIntExtra("temperature", 0) / 10d;
        String technology = Strings.nullToEmpty(intent
                .getStringExtra("technology"));

        String statusString;
        switch (status) {
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                statusString = "unknown";
                break;
            case BatteryManager.BATTERY_STATUS_CHARGING:
                statusString = "charging";
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                statusString = "discharging";
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                statusString = "not charging";
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                statusString = "full";
                break;
            default:
                statusString = "(default:" + status + ")";
                break;
        }

        String healthString;
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                healthString = "unknown";
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                healthString = "good";
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                healthString = "overheat";
                break;
            case BatteryManager.BATTERY_HEALTH_DEAD:
                healthString = "dead";
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                healthString = "voltage";
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                healthString = "unspecified failure";
                break;
            default:
                healthString = "(default:" + health + ")";
                break;
        }

        String acString;
        switch (plugged) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                acString = "plugged ac";
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                acString = "plugged usb";
                break;
            default:
                acString = "(default:" + plugged + ")";
                break;
        }

        String message = "status=" + statusString;
        message += ", health=" + healthString;
        message += ", present=" + present;
        message += ", level=" + level;
        message += ", scale=" + scale;
        message += ", icon-small=" + iconSmall;
        message += ", plugged=" + acString;
        message += ", voltage=" + voltage;
        message += ", temperature=" + temperature;
        message += ", technology=" + technology;

        Log.i(TAG, message);
        if (!useBatteryTemperature) {
            return;
        }
        final ContentValues contentValues = new ContentValues();
        contentValues.put(ServiceUnitStatusLog.Columns.TEMPERATURE,
                Math.round(temperature));
        new Thread() {
            @Override
            public void run() {
                contentResolver.update(ServiceUnitStatusLog.CONTENT.URI,
                        contentValues, null, null);
            }
        }.start();
    }
}
