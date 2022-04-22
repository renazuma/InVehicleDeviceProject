package com.kogasoftware.odt.invehicledevice.service.startupservice;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.kogasoftware.odt.invehicledevice.BuildConfig;
import com.kogasoftware.odt.invehicledevice.view.BigToast;

/**
 * 自動起動処理
 */
public class StartupService extends Service {
    private static final String TAG = StartupService.class.getSimpleName();
    public static final long CHECK_DEVICE_INTERVAL_MILLIS = 10 * 1000;
    private final Handler handler = new Handler();
    private final BroadcastReceiver screenOnBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "screen on");
            handler.post(startActivityIfReadyCallback);
        }
    };

    private final Runnable startActivityIfReadyCallback = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks(this); // 重複を削除
            if (startActivityIfReady()) {
                handler.postDelayed(checkDeviceAndAlertCallback,
                        CHECK_DEVICE_INTERVAL_MILLIS);
            } else {
                handler.postDelayed(this, CHECK_DEVICE_INTERVAL_MILLIS);
            }
        }
    };

    private final Runnable checkDeviceAndAlertCallback = new Runnable() {
        @Override
        public void run() {
            isDeviceReady();
            handler.postDelayed(this, CHECK_DEVICE_INTERVAL_MILLIS);
        }
    };

    public Boolean startActivityIfReady() {
        if (!isDeviceReady()) {
            return false;
        }
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "testing");
            return true;
        }
        return true;
    }

    public Boolean isDeviceReady() {
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (!powerManager.isScreenOn()) {
            Log.i(TAG, "screen off");
            return false;
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (isGpsRequired()
                && !locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.e(TAG,
                    "!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)");
            BigToast.makeText(this, "GPSの設定を有効にしてください", Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        return true;
    }

    public static Boolean isGpsRequired(Boolean isDebug, String buildModel) {
        if (!isDebug) {
            return true;
        }
        if (buildModel.startsWith("androVM for VirtualBox")) {
            return false;
        } else return !buildModel.startsWith("Buildroid for VirtualBox");
    }

    public static Boolean isGpsRequired() {
        return isGpsRequired(BuildConfig.DEBUG, Build.MODEL);
    }

    public static class StartupBroadcastReceiver extends BroadcastReceiver {
        private static final String TAG = StartupBroadcastReceiver.class
                .getSimpleName();

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "broadcast received " + intent);
            if (intent == null) {
                Log.w(TAG, "intent is null");
                return;
            }
            if (intent.getAction() == null) {
                Log.w(TAG, "intent.getAction() is null");
                return;
            }
            context.startService(new Intent(context, StartupService.class));
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate()");
        getApplicationContext().registerReceiver(screenOnBroadcastReceiver,
                new IntentFilter(Intent.ACTION_SCREEN_ON));
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy()");
        getApplicationContext().unregisterReceiver(screenOnBroadcastReceiver);
        handler.removeCallbacks(startActivityIfReadyCallback);
        handler.removeCallbacks(checkDeviceAndAlertCallback);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "onStartCommand(" + intent + ", " + flags + ", " + startId
                + ")");
        handler.post(startActivityIfReadyCallback);
        return Service.START_STICKY;
    }
}
