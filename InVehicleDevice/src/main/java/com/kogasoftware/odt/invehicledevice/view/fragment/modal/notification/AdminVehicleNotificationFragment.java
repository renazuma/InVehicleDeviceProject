package com.kogasoftware.odt.invehicledevice.view.fragment.modal.notification;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.Fragments;

import org.joda.time.DateTime;

import java.util.List;

/**
 * 車載器への通知表示画面
 */
public class AdminVehicleNotificationFragment extends Fragment {
    private static final String VEHICLE_NOTIFICATION_KEY = "vehicle_notification";
    private VehicleNotification vehicleNotification;

    // TODO: Activityは一つしかないので、InVehicleDeviceActivityの指定は不要では？
    private static final String FRAGMENT_TAG = InVehicleDeviceActivity.class + "/" + AdminVehicleNotificationFragment.class + "/%d";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vehicleNotification = (VehicleNotification) getArguments()
                .getSerializable(VEHICLE_NOTIFICATION_KEY);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        TextView bodyTextView = view
                .findViewById(R.id.notification_text_view);
        bodyTextView.setText(vehicleNotification.body);
        view.findViewById(R.id.reply_yes_button).setOnClickListener(
                view12 -> submit(VehicleNotification.Response.YES));
        view.findViewById(R.id.reply_no_button).setOnClickListener(
                view1 -> submit(VehicleNotification.Response.NO));
    }

    public static AdminVehicleNotificationFragment newInstance(
            VehicleNotification vehicleNotification) {
        AdminVehicleNotificationFragment fragment = new AdminVehicleNotificationFragment();
        Bundle args = new Bundle();
        args.putSerializable(VEHICLE_NOTIFICATION_KEY, vehicleNotification);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.vehicle_notification_fragment,
                container, false);
    }

    private void submit(final Long response) {
        vehicleNotification.response = response;
        vehicleNotification.readAt = DateTime.now();
        if (!isAdded()) {
            return;
        }
        final ContentResolver contentResolver = getActivity()
                .getContentResolver();
        final Handler handler = new Handler();
        new Thread() {
            @Override
            public void run() {
                contentResolver.insert(VehicleNotification.CONTENT.URI,
                        vehicleNotification.toContentValues());
                Fragments.hide(AdminVehicleNotificationFragment.this,
                        handler);
            }
        }.start();
    }

    // TODO: 既存に合わせるためにstaticにしている。出来れば変えたい。
    public static void showModal(InVehicleDeviceActivity inVehicleDeviceActivity, List<VehicleNotification> vehicleNotifications) {
        if (inVehicleDeviceActivity.destroyed) {
            return;
        }
        FragmentManager fragmentManager = inVehicleDeviceActivity.getFragmentManager();
        for (final VehicleNotification vehicleNotification : vehicleNotifications) {
            final String tag = String.format(FRAGMENT_TAG, vehicleNotification.id);

            if (fragmentManager.findFragmentByTag(tag) != null) {
                return;
            }

            Fragments.showModal(
                    fragmentManager,
                    AdminVehicleNotificationFragment.newInstance(vehicleNotification),
                    tag);
        }
    }
}
