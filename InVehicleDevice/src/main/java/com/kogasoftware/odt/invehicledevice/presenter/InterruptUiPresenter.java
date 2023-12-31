package com.kogasoftware.odt.invehicledevice.presenter;

import com.kogasoftware.odt.invehicledevice.infra.broadcastReceiver.AirPlaneModeOnReceiver;
import com.kogasoftware.odt.invehicledevice.infra.broadcastReceiver.SignInErrorReceiver;
import com.kogasoftware.odt.invehicledevice.infra.loader.AdminNotificationLoader;
import com.kogasoftware.odt.invehicledevice.infra.loader.CreditPaidChargeChangedNotificationLoader;
import com.kogasoftware.odt.invehicledevice.infra.loader.ExpectedChargeChangedNotificationLoader;
import com.kogasoftware.odt.invehicledevice.infra.loader.MemoChangedNotificationLoader;
import com.kogasoftware.odt.invehicledevice.infra.loader.ScheduleNotificationLoader;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;

/**
 * 割り込み画面表示
 */

public class InterruptUiPresenter {

    private final AdminNotificationLoader adminNotificationLoader;
    private final ScheduleNotificationLoader scheduleNotificationLoader;
    private final SignInErrorReceiver signInErrorReceiver;
    private final AirPlaneModeOnReceiver airplaneModeOnReceiver;

    // HACK: 予定料金やメモ、カード支払い情報の同期は割り込みUIではないが、他の割り込み機能と合わせてここで呼び出している。このクラス名自体からUIを取ってしまってもいいかもしれない。
    private final ExpectedChargeChangedNotificationLoader expectedChargeChangedNotificationLoader;
    private final MemoChangedNotificationLoader memoChangedNotificationLoader;
    private final CreditPaidChargeChangedNotificationLoader creditPaidChargeChangedNotificationLoader;

    public InterruptUiPresenter(InVehicleDeviceActivity inVehicleDeviceActivity) {
        this.adminNotificationLoader = new AdminNotificationLoader(inVehicleDeviceActivity);
        this.scheduleNotificationLoader = new ScheduleNotificationLoader(inVehicleDeviceActivity);
        this.signInErrorReceiver = new SignInErrorReceiver(inVehicleDeviceActivity);
        this.airplaneModeOnReceiver = new AirPlaneModeOnReceiver(inVehicleDeviceActivity);
        this.expectedChargeChangedNotificationLoader = new ExpectedChargeChangedNotificationLoader(inVehicleDeviceActivity);
        this.memoChangedNotificationLoader = new MemoChangedNotificationLoader(inVehicleDeviceActivity);
        this.creditPaidChargeChangedNotificationLoader = new CreditPaidChargeChangedNotificationLoader(inVehicleDeviceActivity);
    }

    public void onCreate() {
        adminNotificationLoader.initLoader();
        scheduleNotificationLoader.initLoader();
        signInErrorReceiver.registerReceiver();
        airplaneModeOnReceiver.registerReceiver();
        expectedChargeChangedNotificationLoader.initLoader();
        memoChangedNotificationLoader.initLoader();
        creditPaidChargeChangedNotificationLoader.initLoader();
    }

    public void onDestroy() {
        adminNotificationLoader.destroyLoader();
        scheduleNotificationLoader.destroyLoader();
        signInErrorReceiver.unregisterReceiver();
        airplaneModeOnReceiver.unregisterReceiver();
        expectedChargeChangedNotificationLoader.destroyLoader();
        memoChangedNotificationLoader.destroyLoader();
        creditPaidChargeChangedNotificationLoader.destroyLoader();
    }
}
