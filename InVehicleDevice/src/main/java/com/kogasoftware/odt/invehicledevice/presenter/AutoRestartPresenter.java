package com.kogasoftware.odt.invehicledevice.presenter;

import android.content.Context;
import android.content.Intent;

import com.kogasoftware.odt.invehicledevice.service.startupservice.StartupService;

/**
 * Created by ksc on 2019/02/22.
 */

public class AutoRestartPresenter {

    private final Context context;

    public AutoRestartPresenter(Context context) {
        this.context = context;
    }

    public void onCreate() {
        // TODO: StartupServiceについては、バックグラウンドで動き続けるべきかの判断が出来なかったため、据え置きとしている
        // ※8.0以降はバックグラウンドではない
        context.startService(new Intent(context, StartupService.class));
    }

    public void onDestroy() {
        // 停止させないためのサービスなので、停止処理は無い
    }
}
