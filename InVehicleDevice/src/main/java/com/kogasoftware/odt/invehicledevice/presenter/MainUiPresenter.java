package com.kogasoftware.odt.invehicledevice.presenter;

import com.kogasoftware.odt.invehicledevice.infra.loader.DefaultChargeLoader;
import com.kogasoftware.odt.invehicledevice.infra.loader.TopViewLoader;
import com.kogasoftware.odt.invehicledevice.infra.loader.onCreateSignInLoader;
import com.kogasoftware.odt.invehicledevice.presenter.mainui.OnCreateDataSync;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;

/**
 * Viewの制御
 */

public class MainUiPresenter {

    private final onCreateSignInLoader onCreateSignInLoader;
    private final TopViewLoader topViewLoader;
    private final DefaultChargeLoader defaultChargeLoader;
    private final OnCreateDataSync onCreateDataSync;

    public MainUiPresenter(InVehicleDeviceActivity inVehicleDeviceActivity) {
        this.onCreateSignInLoader = new onCreateSignInLoader(inVehicleDeviceActivity);
        this.topViewLoader = new TopViewLoader(inVehicleDeviceActivity);
        this.defaultChargeLoader = new DefaultChargeLoader(inVehicleDeviceActivity);
        this.onCreateDataSync = new OnCreateDataSync(inVehicleDeviceActivity);
    }

    public void onCreate() {
        onCreateDataSync.execute();

        // loaderによるアプリ起動時の画面表示のロジックは以下
        // * アプリ起動時にDB内の車載器データが無い場合、SignIn画面を表示（この場合SPも無いため、mainViewLoaderは画面を描画しない）
        //   * SignIn情報をDBに登録後、それらの情報を元にSPを取得。SPのDBへの登録を契機に、mainViewLoaderが画面表示。
        // * アプリ起動時にDB内にSPがある場合、そのままメイン画面を表示（この場合既に車載器データはある前提）
        onCreateSignInLoader.initLoader();
        topViewLoader.initLoader();
        defaultChargeLoader.initLoader();
    }

    public void onDestroy() {
        onCreateSignInLoader.destroyLoader();
        topViewLoader.destroyLoader();
        defaultChargeLoader.destroyLoader();
    }
}
