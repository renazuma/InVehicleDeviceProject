package com.kogasoftware.odt.invehicledevice.view.activity;

import android.app.Activity;
import android.os.Bundle;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.DefaultCharge;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.ServiceProvider;
import com.kogasoftware.odt.invehicledevice.presenter.HealthCheckPresenter;
import com.kogasoftware.odt.invehicledevice.presenter.InterruptUiPresenter;
import com.kogasoftware.odt.invehicledevice.presenter.LogSenderPresenter;
import com.kogasoftware.odt.invehicledevice.presenter.MainUiPresenter;
import com.kogasoftware.odt.invehicledevice.presenter.PermissionChecker;
import com.kogasoftware.odt.invehicledevice.presenter.StatusSenderPresenter;

import java.util.List;

/**
 * 全体の大枠。サインイン前はSignInFragmentを表示し、サインイン後は、自治体に依存して「運行予定一覧画面」か「順番に運行を進める画面」を表示する
 */
public class InVehicleDeviceActivity extends Activity {

    // 通知表示遅延秒数
    public static final Integer VEHICLE_NOTIFICATION_ALERT_DELAY_MILLIS = 5000;

    // インスタンス変数
    public Boolean destroyed = true; // TODO: 変数でライフサイクル管理をするのをやめたい。
    public ServiceProvider serviceProvider; // TODO: ServiceProviderの同期状態を変数で管理するのをやめたい。
    public List<DefaultCharge> defaultCharges; // HACK: DefaultChargesの同期状態を変数で管理するのをやめたい。

    private InterruptUiPresenter interruptUiPresenter;
    private StatusSenderPresenter statusSenderPresenter;
    private LogSenderPresenter logSenderPresenter;
    private MainUiPresenter mainUiPresenter;

    // TODO: 不要にしたい
    public void setServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    // HACK: 不要にしたい
    public void setDefaultCharges(List<DefaultCharge> defaultCharges) {
        this.defaultCharges = defaultCharges;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.destroyed = false;
        (new PermissionChecker(this)).check();

        // このActivityは固有の表示を持たず、コンテナのみを提供しており、具体的な表示はFragmentが行う。
        setContentView(R.layout.in_vehicle_device_activity);

        // Loaderを使用するためには、onStartより前の時点で、一度getLoaderManagerを実行しておく必要がある。
        getLoaderManager();

        statusSenderPresenter = new StatusSenderPresenter(this);
        statusSenderPresenter.onCreate();

        logSenderPresenter = new LogSenderPresenter(this);
        logSenderPresenter.onCreate();

        HealthCheckPresenter healthCheckPresenter = new HealthCheckPresenter(this);
        healthCheckPresenter.onCreate();

        // TODO: スケジュール関連のサーバとの定期同期は、画面をバックグラウンドにしても動き続けなければならないため、
        // TODO: contentProviderでジョブを開始している。
        // TODO: 本来はこちらで管理するべき情報なので、出来れば動かし続ける方法を検討した上で、こちらで開始出来る様にする。

        mainUiPresenter = new MainUiPresenter(this);
        mainUiPresenter.onCreate();

        interruptUiPresenter = new InterruptUiPresenter(this);
        interruptUiPresenter.onCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        interruptUiPresenter.onDestroy();
        mainUiPresenter.onDestroy();
        statusSenderPresenter.onDestroy();
        logSenderPresenter.onDestroy();
        destroyed = true;
    }

}
