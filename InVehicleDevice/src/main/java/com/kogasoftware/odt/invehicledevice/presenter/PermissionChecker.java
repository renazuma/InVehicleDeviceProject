package com.kogasoftware.odt.invehicledevice.presenter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;

/**
 * Created by ksc on 2019/02/22.
 */

public class PermissionChecker {

    private final InVehicleDeviceActivity inVehicleDeviceActivity;

    public PermissionChecker(InVehicleDeviceActivity inVehicleDeviceActivity) {
        this.inVehicleDeviceActivity = inVehicleDeviceActivity;
    }

    public void check() {
        if (!this.isGrantedPermissions()) {
            if (ContextCompat.checkSelfPermission(inVehicleDeviceActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(inVehicleDeviceActivity);
                builder.setMessage(inVehicleDeviceActivity.getString(R.string.location_description))
                        .setPositiveButton("次へ", (dialog, which) -> ActivityCompat.requestPermissions(inVehicleDeviceActivity, MUST_GRANT_PERMISSIONS, 1000));
                builder.show();
            } else {
                ActivityCompat.requestPermissions(inVehicleDeviceActivity, MUST_GRANT_PERMISSIONS, 1000);
            }
        }
    }

    private static final String[] MUST_GRANT_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,   // GPS
            Manifest.permission.WRITE_EXTERNAL_STORAGE, // SDカードへの書き込み
            Manifest.permission.READ_PHONE_STATE
    };

    private boolean isGrantedPermissions() {
        for (String permission : MUST_GRANT_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(inVehicleDeviceActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
