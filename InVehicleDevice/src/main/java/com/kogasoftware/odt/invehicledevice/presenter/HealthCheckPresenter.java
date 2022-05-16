package com.kogasoftware.odt.invehicledevice.presenter;

import android.content.Context;
import android.content.Intent;

import com.kogasoftware.odt.invehicledevice.service.healthcheckservice.HealthCheckService;

/**
 * Created by ksc on 2019/02/22.
 */

public class HealthCheckPresenter {

    private final Context context;

    public HealthCheckPresenter(Context context) {
        this.context = context;
    }

    public void onCreate() {
        // TODO: HealthCheckServiceについては、バックグラウンドで動き続けるべきかの判断が出来なかったため、据え置きとしている
        // ※8.0以降はバックグラウンドではない
        context.startService(new Intent(context, HealthCheckService.class));
    }

}
