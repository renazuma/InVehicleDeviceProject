package com.kogasoftware.odt.invehicledevice.service.healthcheckservice;

import android.content.Intent;

/**
 * 機内モードがONになった際の通知
 */
public class AirplaneModeOnBroadcastIntent extends Intent {
    public static final String ACTION = AirplaneModeOnBroadcastIntent.class
            .getName();

    public AirplaneModeOnBroadcastIntent() {
        super(ACTION);
    }
}
