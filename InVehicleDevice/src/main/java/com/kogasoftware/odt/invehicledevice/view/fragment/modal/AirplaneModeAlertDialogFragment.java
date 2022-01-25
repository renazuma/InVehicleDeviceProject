package com.kogasoftware.odt.invehicledevice.view.fragment.modal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.text.Html;

import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;

/**
 * Created by ksc on 2019/02/22.
 */

public class AirplaneModeAlertDialogFragment extends DialogFragment {

    // TODO: Activityは一つしかないので、InVehicleDeviceActivityの指定は不要では？
    private static final String FRAGMENT_TAG = InVehicleDeviceActivity.class + "/" + AirplaneModeAlertDialogFragment.class;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setMessage(Html.fromHtml("<big><big>機内モードをOFFにしてください</big></big>"));
        builder.setPositiveButton(Html.fromHtml("<big><big>確認</big></big>"), null);
        return builder.create();
    }

    // TODO: 既存に合わせるためにstaticにしている。出来れば変えたい。
    public static void showDialog(InVehicleDeviceActivity inVehicleDeviceActivity) {
        // TODO: Activityの状態確認をここで行うべき？
        if (inVehicleDeviceActivity.destroyed) {
            return;
        }

        FragmentManager fragmentManager = inVehicleDeviceActivity.getFragmentManager();

        if (fragmentManager.findFragmentByTag(FRAGMENT_TAG) != null) {
            return;
        }

        (new AirplaneModeAlertDialogFragment()).show(fragmentManager, FRAGMENT_TAG);
    }
}
