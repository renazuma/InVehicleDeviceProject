package com.kogasoftware.odt.invehicledevice.view.fragment.utils;

import android.os.Handler;
import android.view.View;

/**
 * ボタンなどの多重操作の防止
 */
public class ViewDisabler {
    public static void disable(final View view) {
        disable(view, 400);
    }

    public static void disable(final View view, Integer timeoutMillis) {
        view.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, timeoutMillis);
    }
}
