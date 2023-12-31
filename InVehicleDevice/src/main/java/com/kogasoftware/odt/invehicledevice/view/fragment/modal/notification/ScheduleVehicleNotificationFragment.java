package com.kogasoftware.odt.invehicledevice.view.fragment.modal.notification;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.view.fragment.phaseflow.controlbar.ControlBarFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.listflow.modal.OperationListFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.FragmentUtils;

import org.apache.commons.lang3.SystemUtils;
import org.joda.time.DateTime;

/**
 * 運行スケジュール変更通知表示画面
 */
public class ScheduleVehicleNotificationFragment extends Fragment
        implements
        LoaderCallbacks<Cursor> {

    private static final String TAG = ScheduleVehicleNotificationFragment.class.getSimpleName();
    private static final Integer LOADER_ID = 1;
    private static final String OPERATION_LIST_BUTTON_VISIBLE_KEY = "operation_list_button_visible";
    // TODO: Activityは一つしかないので、InVehicleDeviceActivityの指定は不要では？
    private static final String FRAGMENT_TAG = InVehicleDeviceActivity.class + "/" + ScheduleVehicleNotificationFragment.class;
    private TextView detailTextView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        boolean operationListButtonVisible = args
                .getBoolean(OPERATION_LIST_BUTTON_VISIBLE_KEY);
        View view = getView();
        Button showOperationListButton = view
                .findViewById(R.id.schedule_vehicle_notification_operation_list_button);
        if (operationListButtonVisible) {
            showOperationListButton.setVisibility(View.VISIBLE);
        } else {
            showOperationListButton.setVisibility(View.GONE);
        }
        showOperationListButton.setOnClickListener(view12 -> onShowOperationScheduleListButtonClick());
        Button closeButton = view
                .findViewById(R.id.schedule_vehicle_notification_close_button);
        closeButton.setOnClickListener(view1 -> onCloseButtonClick());
        detailTextView = view
                .findViewById(R.id.schedule_vehicle_notification_detail_text_view);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    public static ScheduleVehicleNotificationFragment newInstance(
            Boolean operationListButtonVisible) {
        ScheduleVehicleNotificationFragment fragment = new ScheduleVehicleNotificationFragment();
        Bundle args = new Bundle();
        args.putBoolean(OPERATION_LIST_BUTTON_VISIBLE_KEY,
                operationListButtonVisible);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.schedule_vehicle_notification_fragment, container,
                false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                VehicleNotification.CONTENT.URI,
                null,
                VehicleNotification.WHERE_SCHEDULE_VEHICLE_NOTIFICATION_FRAGMENT_CONTENT,
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (!cursor.moveToFirst()) {
            hide(() -> {
            });
            return;
        }
        StringBuilder content = new StringBuilder();
        StringBuilder speakContent = new StringBuilder();
        do {
            VehicleNotification vehicleNotification = new VehicleNotification(
                    cursor);
            content.append(vehicleNotification.body);
            content.append(SystemUtils.LINE_SEPARATOR);
            speakContent.append(vehicleNotification.bodyRuby);
            speakContent.append(SystemUtils.LINE_SEPARATOR);
        } while (cursor.moveToNext());
        detailTextView.setText(content);

        Log.i(TAG, "Schedule Notification fragment display appended.");
    }

    private void hide(final Runnable onComplete) {
        final ContentResolver contentResolver = getActivity()
                .getContentResolver();
        final Handler handler = new Handler();
        new Thread() {
            @Override
            public void run() {
                try (Cursor cursor = getActivity()
                        .getContentResolver()
                        .query(VehicleNotification.CONTENT.URI,
                                null,
                                VehicleNotification.WHERE_SCHEDULE_VEHICLE_NOTIFICATION_FRAGMENT_CONTENT,
                                null, null)) {
                    if (cursor.moveToFirst()) {
                        do {
                            VehicleNotification vehicleNotification = new VehicleNotification(
                                    cursor);
                            vehicleNotification.response = VehicleNotification.Response.YES;
                            vehicleNotification.readAt = DateTime.now();
                            contentResolver.insert(
                                    VehicleNotification.CONTENT.URI,
                                    vehicleNotification.toContentValues());
                        } while (cursor.moveToNext());
                    }
                }
                handler.post(() -> {
                    onComplete.run();
                    if (isAdded()) {
                        FragmentUtils
                                .hideModal(ScheduleVehicleNotificationFragment.this);
                    }
                });
            }
        }.start();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void onShowOperationScheduleListButtonClick() {
        Log.i(TAG, "user operation: Operation Schedule List button clicked.");
        hide(() -> {
            if (!isAdded()) {
                return;
            }
            FragmentManager fragmentManager = getFragmentManager();
            Fragment oldFragment = fragmentManager
                    .findFragmentByTag(ControlBarFragment.OPERATION_LIST_FRAGMENT_TAG);
            if (oldFragment != null) {
                FragmentUtils.hideModal(oldFragment);
            }
            FragmentUtils.showModal(fragmentManager,
                    OperationListFragment.newInstance(true),
                    ControlBarFragment.OPERATION_LIST_FRAGMENT_TAG);
        });
    }

    private void onCloseButtonClick() {
        Log.i(TAG, "user operation: Close button clicked.");
        hide(() -> {
            if (!isAdded()) {
                return;
            }
            Fragment fragment = getFragmentManager().findFragmentByTag(OperationListFragment.FRAGMENT_TAG);
            if (getFragmentManager().findFragmentByTag(OperationListFragment.FRAGMENT_TAG) == null) {
                return;
            }
            ((OperationListFragment) fragment).scrollToUnhandledOperationSchedule();
        });
    }

    // TODO: 既存に合わせるためにstaticにしている。出来れば変えたい。
    public static void showModal(InVehicleDeviceActivity inVehicleDeviceActivity) {
        if (inVehicleDeviceActivity.destroyed || inVehicleDeviceActivity.serviceProvider == null) {
            return;
        }

        FragmentManager fragmentManager = inVehicleDeviceActivity.getFragmentManager();

        if (fragmentManager.findFragmentByTag(FRAGMENT_TAG) != null) {
            return;
        }

        FragmentUtils.showModal(fragmentManager, ScheduleVehicleNotificationFragment.newInstance(true), FRAGMENT_TAG);

        Log.i(TAG, "Schedule Notification fragment displayed.");
    }
}
