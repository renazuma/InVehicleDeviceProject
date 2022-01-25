package com.kogasoftware.odt.invehicledevice.service.serviceunitstatuslogservice;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.ServiceUnitStatusLog;

/**
 * 方向センサー。古い実装。
 * TODO: 現在センサーのデータは使われていないので、削除しても良いかも。
 */
public class OrientationSensorEventListener implements SensorEventListener {
    private static final Long SAVE_PERIOD_MILLIS = 500L;
    private static final String TAG = OrientationSensorEventListener.class
            .getSimpleName();
    private final ContentResolver contentResolver;
    private final WindowManager windowManager;

    private Long lastSavedMillis = System.currentTimeMillis();

    public OrientationSensorEventListener(ContentResolver contentResolver,
                                          WindowManager windowManager) {
        this.contentResolver = contentResolver;
        this.windowManager = windowManager;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ORIENTATION) {
            return;
        }

        // 方位から回転角を設定
        float[] values = event.values;
        float degree = values[SensorManager.DATA_X];
        long now = System.currentTimeMillis();
        if (lastSavedMillis + SAVE_PERIOD_MILLIS > now) {
            return;
        }
        lastSavedMillis = now;

        Display display = windowManager.getDefaultDisplay();
        int displayRotation = display.getRotation();
        switch (displayRotation) {
            case Surface.ROTATION_0:
                break;
            case Surface.ROTATION_90:
                degree += 90f;
                break;
            case Surface.ROTATION_180:
                degree += 180f;
                break;
            case Surface.ROTATION_270:
                degree += 270f;
                break;
            default:
                Log.w(TAG, "unexpected display.getRotation() "
                        + displayRotation);
                break;
        }
        final ContentValues contentValues = new ContentValues();
        contentValues.put(ServiceUnitStatusLog.Columns.ORIENTATION,
                360.0 - degree);
        new Thread() {
            @Override
            public void run() {
                // 全データを最新状態に更新しているが、サーバと同期済みデータは削除されているので、これで良い想定っぽい。
                contentResolver.update(ServiceUnitStatusLog.CONTENT.URI,
                        contentValues, null, null);
            }
        }.start();
    }
}
