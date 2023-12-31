package com.kogasoftware.odt.invehicledevice.view.fragment.listflow.modal;

import android.app.Fragment;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.common.util.concurrent.Uninterruptibles;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.view.fragment.listflow.utils.FlickUnneededListView;
import com.kogasoftware.odt.invehicledevice.view.fragment.listflow.utils.arrayadapter.OperationScheduleArrayAdapter;
import com.kogasoftware.odt.invehicledevice.view.fragment.modal.ChargeEditFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.FragmentUtils;

import java.util.concurrent.TimeUnit;

/**
 * 運行予定一覧画面
 */
public class OperationListFragment extends Fragment {
    private static final String TAG = OperationListFragment.class.getSimpleName();
    private static final String CLOSEABLE_KEY = "closeable";
    private static final Integer OPERATION_SCHEDULE_LOADER_ID = 1;
    private static final Integer PASSENGER_RECORD_LOADER_ID = 2;
    // TODO: Activityは一つしかないので、InVehicleDeviceActivityの指定は不要では？
    public static final String FRAGMENT_TAG = InVehicleDeviceActivity.class + "/" + OperationListFragment.class;

    private boolean isEnablePassengerRecordSync = false;

    public static OperationListFragment newInstance(Boolean closeable) {
        OperationListFragment fragment = new OperationListFragment();
        Bundle args = new Bundle();
        args.putBoolean(CLOSEABLE_KEY, closeable);
        fragment.setArguments(args);
        return fragment;
    }

    private LoaderManager loaderManager;
    private OperationScheduleArrayAdapter adapter;
    private ListView listView;

    private final LoaderCallbacks<Cursor> operationScheduleLoaderCallbacks = new LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(),
                    OperationSchedule.CONTENT.URI, null, null, null, null) {
                @Override
                public Cursor loadInBackground() {
                    // 運行予定変更の通知がある場合、「運行予定変更」フラグメントが表示されるまで更新を遅らせる
                    Cursor cursor = getActivity()
                            .getContentResolver()
                            .query(VehicleNotification.CONTENT.URI,
                                    null,
                                    VehicleNotification.WHERE_SCHEDULE_VEHICLE_NOTIFICATION_FRAGMENT_CONTENT,
                                    null, null);
                    boolean delayRequired;
                    try {
                        delayRequired = cursor.getCount() > 0;
                    } finally {
                        cursor.close();
                    }
                    if (delayRequired) {
                        int delayMillis = InVehicleDeviceActivity.VEHICLE_NOTIFICATION_ALERT_DELAY_MILLIS + 1000;
                        Uninterruptibles.sleepUninterruptibly(delayMillis,
                                TimeUnit.MILLISECONDS);
                    }
                    return super.loadInBackground();
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            boolean scroll = (adapter.getCount() == 0);

            Cursor passengerRecordsCursor = getActivity()
                    .getContentResolver()
                    .query(PassengerRecord.CONTENT.URI, null, null, null, null);

            adapter.setData(OperationSchedule.getAll(cursor), PassengerRecord.getAll(passengerRecordsCursor));

            if (scroll) {
                scrollToUnhandledOperationSchedule();
            }
            isEnablePassengerRecordSync = true;
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    };

    private final LoaderCallbacks<Cursor> passengerRecordLoaderCallbacks = new LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getActivity(),
                    PassengerRecord.CONTENT.URI, null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            if (!isEnablePassengerRecordSync) {
                return;
            }

            Cursor operationScheduleCursor = getActivity()
                    .getContentResolver()
                    .query(OperationSchedule.CONTENT.URI, null, null, null, null);

            adapter.setData(OperationSchedule.getAll(operationScheduleCursor), PassengerRecord.getAll(cursor));
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.operation_list_fragment, container,
                false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final boolean closeable = getArguments().getBoolean(CLOSEABLE_KEY);
        loaderManager = getLoaderManager();

        // リアルタイム同期
        loaderManager.initLoader(OPERATION_SCHEDULE_LOADER_ID, null, operationScheduleLoaderCallbacks);
        loaderManager.initLoader(PASSENGER_RECORD_LOADER_ID, null, passengerRecordLoaderCallbacks);

        View view = getView();

        // 戻るボタン（スケジュールのみ）
        final Button closeButton = view.findViewById(R.id.operation_list_close_button);
        closeButton.setOnClickListener(v -> {
            Log.i(TAG, "user operation: Close button clicked.");
            FragmentUtils.hideModal(OperationListFragment.this);
        });

        if (closeable) {
            closeButton.setVisibility(View.VISIBLE);
        } else {
            closeButton.setVisibility(View.GONE);
        }

        // 各行
        adapter = new OperationScheduleArrayAdapter(this);
        listView = ((FlickUnneededListView) view.findViewById(R.id.operation_list_view)).getListView();
        listView.setAdapter(adapter);

        // 乗客も見るボタン
        final Button showPassengerButton = view.findViewById(R.id.operation_list_show_passengers_button);

        // 戻るボタン（ユーザー表示時）
        final Button hidePassengerButton = view.findViewById(R.id.operation_list_hide_passengers_button);

        showPassengerButton.setOnClickListener(view1 -> {
            Log.i(TAG, "user operation: Show Passenger button clicked.");
            adapter.showPassengerRecords();
            showPassengerButton.setVisibility(View.GONE);
            hidePassengerButton.setVisibility(View.VISIBLE);
            closeButton.setVisibility(View.GONE);
        });
        hidePassengerButton.setOnClickListener(v -> {
            Log.i(TAG, "user operation: Hide Passenger button clicked.");
            adapter.hidePassengerRecords();
            hidePassengerButton.setVisibility(View.GONE);
            showPassengerButton.setVisibility(View.VISIBLE);
            if (closeable) {
                closeButton.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        loaderManager.destroyLoader(OPERATION_SCHEDULE_LOADER_ID);
        loaderManager.destroyLoader(PASSENGER_RECORD_LOADER_ID);
    }

    public void scrollToUnhandledOperationSchedule() {
        // 未運行の運行スケジュールまでスクロールする
        int count = adapter.getCount();
        for (int i = 0; i < count; ++i) {
            if (adapter.isNotYetDeparted(i)) {
                listView.setSelection(i);
                if (i >= 1) {
                    listView.scrollBy(0, -1);
                }
                return;
            }
        }
        if (count >= 1) {
            listView.setSelectionFromTop(count - 1, 0);
        }
    }
}
