package com.kogasoftware.odt.invehicledevice.view.fragment.listflow.utils.arrayadapter;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.ServiceProvider;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.ZenrinMapsAccount;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.view.fragment.modal.ChargeEditFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.modal.MapFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.modal.PassengerRecordMemoFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.FragmentUtils;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.ScheduleUtil;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.ViewDisabler;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * 運行予定一覧
 */
public class OperationScheduleArrayAdapter
        extends ArrayAdapter<List> {
    private static final String TAG = OperationScheduleArrayAdapter.class.getSimpleName();
    private static final Integer SELECTING_COLOR = Color.parseColor("#D5E9F6");
    private static final Integer DEPARTED_COLOR = Color.LTGRAY;
    private static final Integer NOT_YET_DEPARTED_COLOR = Color.parseColor("#FFFFFF");
    private static final Integer RESOURCE_ID = R.layout.operation_list_row;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm");
    private final LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    private final TreeSet<PassengerRecord> passengerRecords = new TreeSet<PassengerRecord>(PassengerRecord.DEFAULT_COMPARATOR);
    private List<OperationSchedule> originalOperationSchedules;
    private final ContentResolver contentResolver;
    private Boolean showPassengerRecords = false;


    private final Fragment fragment;

    public OperationScheduleArrayAdapter(Fragment fragment) {
        super(fragment.getActivity(), RESOURCE_ID, new LinkedList<List>());
        this.fragment = fragment;
        this.contentResolver = fragment.getActivity().getContentResolver();
    }

    protected final OnTouchListener onOperationScheduleTouchListener = new View.OnTouchListener() {
        private View operationScheduleRowView;
        private MotionEvent currentEvent;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            this.operationScheduleRowView = view;
            this.currentEvent = event;

            Object operationSchedules = operationScheduleRowView.getTag();
            if (operationSchedules instanceof List) {
                return onTouch();
            } else {
                Log.e(TAG, "\"" + view + "\".getTag() (" + operationSchedules + ") is not instanceof " + List.class);
                return false;
            }
        }

        private boolean onTouch() {
            boolean isEventComplete;

            if (isSelectingEvent()) {
                setOnOperationScheduleRowSelecting(operationScheduleRowView);
                isEventComplete = true;
            } else if (isNotTargetEvent()) {
                isEventComplete = false;
            } else {
                isEventComplete = currentEvent.getAction() == MotionEvent.ACTION_CANCEL || onTap();
                // updateを検知して画面更新はされるが、タップ時と若干のラグが出るため、手動で対象行だけの修正を入れている。
                setOperationScheduleRowBackground(operationScheduleRowView);
            }

            return isEventComplete;
        }

        private boolean isSelectingEvent() {
            return currentEvent.getAction() == MotionEvent.ACTION_DOWN;
        }

        private boolean isNotTargetEvent() {
            return (currentEvent.getAction() != MotionEvent.ACTION_UP && currentEvent.getAction() != MotionEvent.ACTION_CANCEL);
        }

        protected boolean onTap() {
            final List<OperationSchedule> operationSchedules = (List) operationScheduleRowView.getTag();

            DateTime targetDateTime = null;
            if (isNotYetDeparted(operationScheduleRowView)) {
                targetDateTime = DateTime.now();
            }

            for (OperationSchedule operationSchedule : operationSchedules) {
                operationSchedule.arrivedAt = targetDateTime;
                operationSchedule.departedAt = targetDateTime;
            }

            Thread tt = new Thread() {
                @Override
                public void run() {
                    for (OperationSchedule operationSchedule : operationSchedules) {
                        ContentValues values = operationSchedule.toContentValues();
                        contentResolver.insert(OperationSchedule.CONTENT.URI, values);
                    }
                }
            };

            tt.start();
            try {
                tt.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return false;
        }
    };

    private void setOnOperationScheduleRowSelecting(View operationScheduleRowView) {
        operationScheduleRowView.setBackgroundColor(SELECTING_COLOR);
    }

    private void setOperationScheduleRowBackground(View operationScheduleRowView) {
        TextView checkMarkTextView = operationScheduleRowView.findViewById(R.id.check_mark_text_view);

        if (isNotYetDeparted(operationScheduleRowView)) {
            operationScheduleRowView.setBackgroundColor(NOT_YET_DEPARTED_COLOR);
            checkMarkTextView.setVisibility(View.INVISIBLE);
        } else {
            operationScheduleRowView.setBackgroundColor(DEPARTED_COLOR);
            checkMarkTextView.setVisibility(View.VISIBLE);
        }
    }

    private boolean isNotYetDeparted(View operationScheduleRowView) {
        return isNotYetDeparted((List<OperationSchedule>) (operationScheduleRowView.getTag()));
    }

    public boolean isNotYetDeparted(int position) {
        return isNotYetDeparted(getItem(position));
    }

    private boolean isNotYetDeparted(List<OperationSchedule> operationSchedules) {
        boolean isDeparted = true;
        for (OperationSchedule operationSchedule : operationSchedules) {
            if (operationSchedule.departedAt == null) {
                isDeparted = false;
            }
        }
        return !isDeparted;
    }

    static class PassengerRecordRowTag {
        public final PassengerRecord passengerRecord;
        public final OperationSchedule operationSchedule;
        public final Boolean getOn;

        public PassengerRecordRowTag(PassengerRecord passengerRecord, OperationSchedule operationSchedule, Boolean getOn) {
            this.passengerRecord = passengerRecord;
            this.operationSchedule = operationSchedule;
            this.getOn = getOn;
        }

        public boolean isGetOnEvent() {
            return getOn;
        }

        public boolean isNotYetGotOn() {
            return passengerRecord.getOnTime == null;
        }

        public boolean isNotYetGotOff() {
            return passengerRecord.getOffTime == null;
        }
    }

    protected final OnTouchListener onPassengerRecordTouchListener = new View.OnTouchListener() {
        private View passengerRecordRowView;
        private MotionEvent currentEvent;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            this.passengerRecordRowView = view;
            this.currentEvent = event;

            Object passengerRecordRowTag = view.getTag();
            if (passengerRecordRowTag instanceof PassengerRecordRowTag) {
                return onTouch();
            } else {
                Log.e(TAG, "\"" + view + "\".getTag() (" + passengerRecordRowTag + ") is not instanceof " + PassengerRecordRowTag.class);
                return false;
            }
        }

        private boolean onTouch() {
            boolean isEventComplete;

            if (isSelectingEvent()) {
                setPassengerRecordsRowSelecting(passengerRecordRowView);
                isEventComplete = true;
            } else if (isNotTargetEvent()) {
                isEventComplete = false;
            } else {
                isEventComplete = currentEvent.getAction() == MotionEvent.ACTION_CANCEL || onTap();
                // updateを検知して画面更新はされるが、タップ時と若干のラグが出るため、手動で対象行だけの修正を入れている。
                setPassengerRecordsRowBackground(passengerRecordRowView);
            }

            return isEventComplete;
        }

        private boolean isSelectingEvent() {
            return currentEvent.getAction() == MotionEvent.ACTION_DOWN;
        }

        private boolean isNotTargetEvent() {
            return (currentEvent.getAction() != MotionEvent.ACTION_UP && currentEvent.getAction() != MotionEvent.ACTION_CANCEL);
        }


        protected boolean onTap() {
            PassengerRecordRowTag passengerRecordRowTag = (PassengerRecordRowTag) passengerRecordRowView.getTag();

            PassengerRecord passengerRecord = passengerRecordRowTag.passengerRecord;
            OperationSchedule operationSchedule = passengerRecordRowTag.operationSchedule;

            int defaultChargeCnt = (((InVehicleDeviceActivity) getContext()).defaultCharges).size();

            // 料金設定ページに遷移するパターン。他のケースと動きが大きく異なるのでこのパターンだけ別扱いにしている。
            // HACK: その他のパターンも整理し直して、シンプルに直すべき。
            if (defaultChargeCnt > 0 && passengerRecord.getOnTime == null && !passengerRecord.settled) {
                FragmentUtils.showModal(fragment.getFragmentManager(),
                        ChargeEditFragment.newInstance(operationSchedule.id, passengerRecord.id));
                return false;
            }

            DateTime now = DateTime.now();

            if (passengerRecordRowTag.isGetOnEvent()) {
                if (passengerRecordRowTag.isNotYetGotOn()) {
                    passengerRecord.getOnTime = now;
                } else {
                    passengerRecord.getOnTime = null;
                    passengerRecord.getOffTime = null;
                    if(!passengerRecord.settled) {
                        passengerRecord.paidCharge = null;
                    }
                }
            } else {
                if (passengerRecordRowTag.isNotYetGotOff()) {
                    if (passengerRecordRowTag.isNotYetGotOn()) {
                        passengerRecord.getOnTime = now;
                    }
                    passengerRecord.getOffTime = now;
                } else {
                    passengerRecord.getOffTime = null;
                }
            }

            final ContentValues values = passengerRecord.toContentValues();
            final String where = PassengerRecord.Columns._ID + " = ?";
            final String[] whereArgs = new String[]{passengerRecord.id.toString()};

            new Thread() {
                @Override
                public void run() {
                    contentResolver.update(PassengerRecord.CONTENT.URI, values, where, whereArgs);
                }
            }.start();

            return false;
        }
    };

    private void setPassengerRecordsRowBackground(View passengerRecordRowView) {
        passengerRecordRowView.setBackgroundColor(getPassengerRecordRowColor(passengerRecordRowView, false));
    }

    private void setPassengerRecordsRowSelecting(View passengerRecordRowView) {
        passengerRecordRowView.setBackgroundColor(getPassengerRecordRowColor(passengerRecordRowView, true));
    }

    private int getPassengerRecordRowColor(View passengerRecordRowView, boolean invert) {
        PassengerRecordRowTag passengerRecordRowTag = (PassengerRecordRowTag) passengerRecordRowView.getTag();
        PassengerRecord passengerRecord = passengerRecordRowTag.passengerRecord;

        if (passengerRecordRowTag.getOn) {
            if ((passengerRecord.getOnTime != null) ^ invert) {
                return ContextCompat.getColor(fragment.getContext(), R.color.selected_get_on_row);
            } else {
                return ContextCompat.getColor(fragment.getContext(), R.color.get_on_row);
            }
        } else {
            if ((passengerRecord.getOffTime != null) ^ invert) {
                return ContextCompat.getColor(fragment.getContext(), R.color.selected_get_off_row);
            } else {
                return ContextCompat.getColor(fragment.getContext(), R.color.get_off_row);
            }
        }
    }

    protected final OnClickListener onUserMemoButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Object tag = view.getTag();
            if (!(tag instanceof PassengerRecord)) {
                return;
            }
            PassengerRecord passengerRecord = (PassengerRecord) tag;
            if (fragment.getFragmentManager() == null) {
                return;
            }
            FragmentUtils.showModal(fragment.getFragmentManager(), PassengerRecordMemoFragment.newInstance(passengerRecord));
        }
    };

    protected final OnClickListener onMapButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewDisabler.disable(view);
            FragmentUtils.showModal(fragment.getFragmentManager(), MapFragment.newInstance());
        }
    };

    protected final OnClickListener onNaviButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Object tag = view.getTag();
            if (!(tag instanceof OperationSchedule)) {
                return;
            }
            final OperationSchedule operationSchedule = (OperationSchedule) tag;
            operationSchedule.startNavigation(getContext());
        }
    };


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(RESOURCE_ID, null);
        } else {
            convertView.setBackgroundColor(NOT_YET_DEPARTED_COLOR);
        }

        setOperationScheduleRowView(position, convertView);
        setPassengerRecordRowViews(position, convertView);

        return convertView;
    }

    private void setOperationScheduleRowView(int position, View convertView) {
        List<OperationSchedule> operationSchedules = getItem(position);
        OperationSchedule representativeOS = operationSchedules.get(0);

        convertView.setTag(operationSchedules);

        setArrivalEstimateTextView(position, convertView, operationSchedules);
        setDepartureEstimateTextView(position, convertView, operationSchedules);

        ((TextView) convertView.findViewById(R.id.platform_name)).setText(representativeOS.name);

        setPlatformAddressView(convertView, representativeOS);

        setMapButtonView(convertView);

        setNaviButtonView(convertView, representativeOS);

        setOperationScheduleRowBackground(convertView);

        setOperationRowPassengerCount(position, convertView);

        convertView.setOnTouchListener(onOperationScheduleTouchListener);
    }

    private void setArrivalEstimateTextView(int position, View convertView, List<OperationSchedule> operationSchedules) {
        TextView arrivalEstimateTextView = convertView.findViewById(R.id.operation_schedule_arrival_estimate_text_view);

        arrivalEstimateTextView.setText("");

        if (isArrivalEstimateViewEnable(position)) {
            arrivalEstimateTextView.setText(getArrivalEstimateForView(position, operationSchedules));
        }
    }

    private void setDepartureEstimateTextView(int position, View convertView, List<OperationSchedule> operationSchedules) {
        TextView departureEstimateTextView = convertView.findViewById(R.id.operation_schedule_departure_estimate_text_view);
        departureEstimateTextView.setText("");

        if (isDepartureEstimateViewEnable(position)) {
            departureEstimateTextView.setText(getDepartureEstimateForView(position, operationSchedules));
        }
    }

    private void setPlatformAddressView(View convertView, OperationSchedule representativeOS) {
        TextView platformAddressView = convertView.findViewById(R.id.platform_address);
        platformAddressView.setText(representativeOS.address);

        if (StringUtils.isBlank(platformAddressView.getText())) {
            platformAddressView.setText("(住所登録なし)");
        }
    }

    private void setMapButtonView(View convertView) {
        Button mapButton = convertView.findViewById(R.id.operation_list_map_button);
        Cursor serviceProviderCursor = getContext()
            .getContentResolver()
            .query(ServiceProvider.CONTENT.URI, null, null, null, null);

        Cursor zenrinMapsAccountCursor = getContext()
                .getContentResolver()
                .query(ZenrinMapsAccount.CONTENT.URI, null, null, null, null);

        if (serviceProviderCursor.moveToFirst() && (new ServiceProvider(serviceProviderCursor)).zenrinMaps && zenrinMapsAccountCursor.moveToFirst()) {
            mapButton.setVisibility(View.VISIBLE);
        }
        mapButton.setOnClickListener(onMapButtonClickListener);
    }

    private void setNaviButtonView(View convertView, OperationSchedule representativeOS) {
        Button naviButton = convertView.findViewById(R.id.operation_list_navi_button);
        naviButton.setTag(representativeOS);
        naviButton.setOnClickListener(onNaviButtonClickListener);
    }

    @Nullable
    private String getArrivalEstimateForView(int position, List<OperationSchedule> operationSchedules) {
        List<OperationSchedule> targetOperationSchedules = new ArrayList(operationSchedules);
        List<OperationSchedule> nextOperationSchedules = Lists.newArrayList();

        if (position != getCount() - 1) {
            nextOperationSchedules = getItem(position + 1);
        }

        if (!nextOperationSchedules.isEmpty() && nextOperationSchedules.get(0).platformId.equals(operationSchedules.get(0).platformId)) {
            targetOperationSchedules.addAll(nextOperationSchedules);
        }

        OperationSchedule arrivalOS = null;
        for (OperationSchedule operationSchedule : targetOperationSchedules) {
            if (null == arrivalOS || arrivalOS.arrivalEstimate.isAfter(operationSchedule.arrivalEstimate)) {
                arrivalOS = operationSchedule;
            }
        }
        return arrivalOS.arrivalEstimate.toString(DATE_TIME_FORMATTER) + "着";
    }

    @Nullable
    private String getDepartureEstimateForView(int position, List<OperationSchedule> operationSchedules) {
        List<OperationSchedule> targetOperationSchedules = new ArrayList(operationSchedules);
        List<OperationSchedule> prevOperationSchedules = Lists.newArrayList();

        if (position != 0) {
            prevOperationSchedules = getItem(position - 1);
        }

        if (!prevOperationSchedules.isEmpty() && prevOperationSchedules.get(0).platformId.equals(operationSchedules.get(0).platformId)) {
            targetOperationSchedules.addAll(prevOperationSchedules);
        }

        OperationSchedule departureOS = null;
        for (OperationSchedule operationSchedule : targetOperationSchedules) {
            if (null == departureOS || departureOS.departureEstimate.isBefore(operationSchedule.departureEstimate)) {
                departureOS = operationSchedule;
            }
        }
        return departureOS.departureEstimate.toString(DATE_TIME_FORMATTER) + "発";
    }


    private boolean isArrivalEstimateViewEnable(int position) {
        if (position == 0) {
            return true;
        }

        OperationSchedule previousOS = (OperationSchedule) getItem(position - 1).get(0);
        OperationSchedule currentOS = (OperationSchedule) getItem(position).get(0);

        return !currentOS.platformId.equals(previousOS.platformId);
    }

    private boolean isDepartureEstimateViewEnable(int position) {
        if (position == getCount() - 1) {
            return false;
        }

        OperationSchedule currentOS = (OperationSchedule) getItem(position).get(0);
        OperationSchedule nextOS = (OperationSchedule) getItem(position + 1).get(0);

        return !currentOS.platformId.equals(nextOS.platformId);
    }

    private void setOperationRowPassengerCount(int position, View convertView) {
        Long getOffPassengerCount = 0L;
        Long getOnPassengerCount = 0L;

        for (PassengerRecord passengerRecord : passengerRecords) {
            for (OperationSchedule operationSchedule : (List<OperationSchedule>) getItem(position)) {
                if (passengerRecord.arrivalScheduleId.equals(operationSchedule.id)) {
                    getOffPassengerCount += passengerRecord.passengerCount;
                } else if (passengerRecord.departureScheduleId.equals(operationSchedule.id)) {
                    getOnPassengerCount += passengerRecord.passengerCount;
                }
            }
        }
        TextView getOnPassengerCountTextView = convertView.findViewById(R.id.operation_schedule_get_on_passenger_count_text_view);
        getOnPassengerCountTextView.setText("乗" + String.format("%3d", getOnPassengerCount) + "名");
        getOnPassengerCountTextView.setVisibility(getOnPassengerCount > 0 ? View.VISIBLE : View.INVISIBLE);

        TextView getOffPassengerCountTextView = convertView.findViewById(R.id.operation_schedule_get_off_passenger_count_text_view);
        getOffPassengerCountTextView.setText("降" + String.format("%3d", getOffPassengerCount) + "名");
        getOffPassengerCountTextView.setVisibility(getOffPassengerCount > 0 ? View.VISIBLE : View.INVISIBLE);
    }

    private void setPassengerRecordRowViews(int position, View convertView) {
        ViewGroup passengerRecordsViewGroup = convertView.findViewById(R.id.operation_list_passenger_records);
        passengerRecordsViewGroup.removeAllViews();
        passengerRecordsViewGroup.setVisibility(showPassengerRecords ? View.VISIBLE : View.GONE);

        if (showPassengerRecords) {
            for (PassengerRecord passengerRecord : passengerRecords) {
                for (OperationSchedule operationSchedule : (List<OperationSchedule>) getItem(position)) {
                    if (passengerRecord.arrivalScheduleId.equals(operationSchedule.id)) {
                        passengerRecordsViewGroup.addView(createPassengerRecordRow(operationSchedule, passengerRecord, false));
                    } else if (passengerRecord.departureScheduleId.equals(operationSchedule.id)) {
                        passengerRecordsViewGroup.addView(createPassengerRecordRow(operationSchedule, passengerRecord, true));
                    }
                }
            }
        }
    }

    private View createPassengerRecordRow(OperationSchedule operationSchedule, PassengerRecord passengerRecord, Boolean getOn) {
        View row = layoutInflater.inflate(R.layout.small_passenger_record_list_row, null);

        // 行のデフォルト背景色
        row.setBackgroundColor(NOT_YET_DEPARTED_COLOR);

        // 乗降画像
        ImageView selectMarkImageView = row.findViewById(R.id.select_mark_image_view);
        selectMarkImageView.setImageResource(getOn ? R.drawable.get_on : R.drawable.get_off);

        // ユーザー名
        TextView userNameView = row.findViewById(R.id.user_name);
        userNameView.setText(passengerRecord.getDisplayName());

        // 行タッチ時の動作を定義
        row.setTag(new PassengerRecordRowTag(passengerRecord, operationSchedule, getOn));
        row.setOnTouchListener(onPassengerRecordTouchListener);

        //乗降人数
        TextView countView = row.findViewById(R.id.passenger_count_text_view);
        countView.setText(passengerRecord.passengerCount + "名");

        // メモボタン
        Button userMemoButton = row.findViewById(R.id.user_memo_button);
        userMemoButton.setTag(passengerRecord);
        userMemoButton.setOnClickListener(onUserMemoButtonClickListener);

        // 料金系
        TextView paidChargeView = row.findViewById(R.id.paid_charge);
        TextView expectedChargeView = row.findViewById(R.id.expected_charge);
        if (!getOn) {
            View passengerCountSpaceView = row.findViewById(R.id.passenger_count_space_view);
            passengerCountSpaceView.setVisibility(View.GONE);
        }

        int defaultChargeCnt = (((InVehicleDeviceActivity) getContext()).defaultCharges).size();
        if (defaultChargeCnt > 0) {
            paidChargeView.setVisibility(View.INVISIBLE);
            expectedChargeView.setVisibility(View.INVISIBLE);
        } else {
            paidChargeView.setVisibility(View.GONE);
            expectedChargeView.setVisibility(View.GONE);
        }

        // 支払料金
        if (passengerRecord.paidCharge != null) {
            paidChargeView.setText(passengerRecord.settled? "カード決済" : passengerRecord.paidCharge + "円");
            paidChargeView.setVisibility(View.VISIBLE);
        }

        // 予定料金
        if (getOn) {
            if (passengerRecord.expectedCharge != null) {
                expectedChargeView.setText(passengerRecord.expectedCharge + "円");
                expectedChargeView.setVisibility(View.VISIBLE);
            }
        } else {
            expectedChargeView.setVisibility(View.GONE);
        }

        // 乗降者行背景色
        setPassengerRecordsRowBackground(row);

        // 乗車行に到着乗降場名を追加
        TextView arrivalPlatformView = row.findViewById(R.id.user_arrival_platform_name);
        arrivalPlatformView.setVisibility(View.GONE);
        if (getOn) {
            OperationSchedule arrivalOperationSchedule = getOperationScheduleFromId(passengerRecord.arrivalScheduleId.longValue());
            if (null != arrivalOperationSchedule) {
                arrivalPlatformView.setVisibility(View.VISIBLE);
                arrivalPlatformView.setText("⇨" + arrivalOperationSchedule.name);
            }
        }

        return row;
    }

    private OperationSchedule getOperationScheduleFromId(Long requestId) {
        OperationSchedule operationSchedule = null;

        for (OperationSchedule tmpOperationSchedule : originalOperationSchedules) {
            if (tmpOperationSchedule.id.equals(requestId)) {
                operationSchedule = tmpOperationSchedule;
            }
        }
        return operationSchedule;
    }

    public void showPassengerRecords() {
        showPassengerRecords = true;
        notifyDataSetChanged();
    }

    public void hidePassengerRecords() {
        showPassengerRecords = false;
        notifyDataSetChanged();
    }

    public void setData(List<OperationSchedule> newOperationSchedules, List<PassengerRecord> newPassengerRecords) {
        originalOperationSchedules = newOperationSchedules;

        passengerRecords.clear();
        passengerRecords.addAll(newPassengerRecords);
        clear();

        addAll(ScheduleUtil.getOperationSchedulesSortedPerPlatform(newOperationSchedules, newPassengerRecords));

        notifyDataSetChanged();
    }
}
