package com.kogasoftware.odt.invehicledevice.view.fragment.phaseflow.utils.arrayadapter;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule.Phase;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.view.fragment.modal.ChargeEditFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.modal.PassengerRecordMemoFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.FragmentUtils;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.ViewDisabler;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Map.Entry;
import java.util.WeakHashMap;

/**
 * 乗客一覧
 */
public class PassengerRecordArrayAdapter extends ArrayAdapter<PassengerRecord> {
    private static final String TAG = PassengerRecordArrayAdapter.class.getSimpleName();
    private static final Integer RESOURCE_ID = R.layout.passenger_record_list_row;
    private final LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    private final Phase phase;
    private final List<OperationSchedule> currentOperationSchedules;
    private final WeakHashMap<View, Boolean> memoButtons = new WeakHashMap<>();
    private final ContentResolver contentResolver;
    private Boolean memoButtonsVisible = true;
    private final Fragment fragment;

    public PassengerRecordArrayAdapter(Fragment fragment, Phase phase, List<OperationSchedule> operationSchedules) {
        super(fragment.getActivity(), RESOURCE_ID);
        this.fragment = fragment;
        this.contentResolver = fragment.getActivity().getContentResolver();
        this.phase = phase;
        this.currentOperationSchedules = operationSchedules;
    }

    private final OnClickListener onClickListenerForPassengerRecord = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Object tag = view.getTag();
            if (!(tag instanceof PassengerRecord)) {
                Log.e(TAG, "\"" + view + "\".getTag() (" + tag + ") is not instanceof PassengerRecord");
            }

            PassengerRecord passengerRecord = (PassengerRecord) tag;

            OperationSchedule operationSchedule = getTargetOperationSchedule(passengerRecord);

            Log.i(TAG, "user operation: Passenger Record Row clicked. { "
                    + "status: " + FragmentUtils.getPassengerStatus(passengerRecord, operationSchedule).toString() + ","
                    + " PassengerRecordId: " + passengerRecord.id + ","
                    + " userId: " + passengerRecord.userId + " }");

            // 料金設定ページに遷移するパターン。他のケースと動きが大きく異なるのでこのパターンだけ別扱いにしている。
            // HACK: その他のパターンも整理し直して、シンプルに直すべき。
            if (isChargeEditPattern(passengerRecord)) {
                FragmentUtils.showModal(fragment.getFragmentManager(),
                        ChargeEditFragment.newInstance(operationSchedule.id, passengerRecord.id));
                return;
            }

            setPassengerRecordOperateTime(passengerRecord);

            updatePassengerRecordOperateTime(passengerRecord);
        }

        private void updatePassengerRecordOperateTime(PassengerRecord passengerRecord) {
            final ContentValues values = passengerRecord.toContentValues();
            final String where = PassengerRecord.Columns._ID + " = ?";
            final String[] whereArgs = new String[]{passengerRecord.id.toString()};
            new Thread() {
                @Override
                public void run() {
                    contentResolver.update(PassengerRecord.CONTENT.URI, values, where, whereArgs);
                }
            }.start();
            notifyDataSetChanged();
        }

        private void setPassengerRecordOperateTime(PassengerRecord passengerRecord) {
            OperationSchedule operationSchedule = getTargetOperationSchedule(passengerRecord);

            DateTime now = DateTime.now();
            if (operationSchedule.id.equals(passengerRecord.arrivalScheduleId)) {
                passengerRecord.ignoreGetOffMiss = false;
                if (passengerRecord.getOffTime != null) {
                    passengerRecord.getOffTime = null;
                } else {
                    if (passengerRecord.getOnTime == null) {
                        passengerRecord.getOnTime = now;
                    }
                    passengerRecord.getOffTime = now;
                }
            } else if (operationSchedule.id.equals(passengerRecord.departureScheduleId)) {
                passengerRecord.ignoreGetOnMiss = false;
                if (passengerRecord.getOnTime != null) {
                    passengerRecord.getOnTime = null;
                    passengerRecord.getOffTime = null;
                    if(!passengerRecord.settled) {
                        passengerRecord.paidCharge = null;
                    }
                } else {
                    passengerRecord.getOnTime = now;
                }
            }
        }

        private boolean isChargeEditPattern(PassengerRecord passengerRecord) {
            int defaultChargeCnt = ((InVehicleDeviceActivity) getContext()).defaultCharges.size();
            return defaultChargeCnt > 0 && passengerRecord.getOnTime == null && !passengerRecord.settled;
        }
    };

    @Nullable
    private OperationSchedule getTargetOperationSchedule(PassengerRecord passengerRecord) {
        OperationSchedule operationSchedule = null;

        if (phase.equals(Phase.PLATFORM_GET_OFF)) {
            for (OperationSchedule tmpOperationSchedule : currentOperationSchedules) {
                if (tmpOperationSchedule.id.equals(passengerRecord.arrivalScheduleId)) {
                    operationSchedule = tmpOperationSchedule;
                    break;
                }
            }
        } else {
            for (OperationSchedule tmpOperationSchedule : currentOperationSchedules) {
                if (tmpOperationSchedule.id.equals(passengerRecord.departureScheduleId)) {
                    operationSchedule = tmpOperationSchedule;
                    break;
                }
            }
        }
        return operationSchedule;
    }

    protected final OnClickListener onClickMemoButtonListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewDisabler.disable(view);
            Object tag = view.getTag();

            if (!(tag instanceof PassengerRecord)) {
                Log.e(TAG, "\"" + view + "\".getTag() (" + tag + ") is not instanceof PassengerRecord");
                return;
            }

            PassengerRecord passengerRecord = (PassengerRecord) tag;

            Log.i(TAG, "user operation: Passenger Record Row Memo button clicked. { "
                    + "PassengerRecordId: " + passengerRecord.id + ","
                    + " userId: " + passengerRecord.userId + " }");

            FragmentUtils.showModal(fragment.getFragmentManager(), PassengerRecordMemoFragment.newInstance(passengerRecord));
        }
    };


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(RESOURCE_ID, null);
        }

        PassengerRecord passengerRecord = getItem(position);

        TextView passengerCountTextView = convertView.findViewById(R.id.passenger_count_text_view);
        passengerCountTextView.setText(passengerRecord.passengerCount + "名");

        setMemoButtonView(convertView, passengerRecord);

        convertView.setTag(passengerRecord);
        convertView.setOnClickListener(onClickListenerForPassengerRecord);

        setMarkImageView(convertView, passengerRecord);

        TextView userNameView = convertView.findViewById(R.id.user_name);
        userNameView.setText(passengerRecord.getDisplayName());

        setRowDefaultBackgroundColor(convertView, passengerRecord);

        setChargeTextView(convertView, passengerRecord);

        return convertView;
    }

    private void setChargeTextView(View convertView, PassengerRecord passengerRecord) {
        // 料金表示
        TextView chargeText = convertView.findViewById(R.id.charge_edit_text_view);
        if (passengerRecord.paidCharge != null) {
            chargeText.setText(passengerRecord.settled? "カード決済" : passengerRecord.paidCharge + "円");
        } else {
            chargeText.setText("");
        }

        TextView expectedChargeText = convertView.findViewById(R.id.expected_charge_text_view);
        if (passengerRecord.expectedCharge != null) {
            expectedChargeText.setText(passengerRecord.expectedCharge + "円");
        } else {
            expectedChargeText.setText("");
        }
    }

    private void setMarkImageView(View convertView, PassengerRecord passengerRecord) {
        OperationSchedule operationSchedule = getTargetOperationSchedule(passengerRecord);
        ImageView selectMarkImageView = convertView.findViewById(R.id.select_mark_image_view);
        if (operationSchedule.id.equals(passengerRecord.arrivalScheduleId)) {
            selectMarkImageView.setImageResource(R.drawable.get_off);
        } else if (operationSchedule.id.equals(passengerRecord.departureScheduleId)) {
            selectMarkImageView.setImageResource(R.drawable.get_on);
        }
    }

    private void setRowDefaultBackgroundColor(View convertView, PassengerRecord passengerRecord) {
        int colorCode = 0;
        switch (FragmentUtils.getPassengerStatus(passengerRecord, getTargetOperationSchedule(passengerRecord))) {
            case SELECTED_GET_OFF:
                colorCode = ContextCompat.getColor(fragment.getContext(), R.color.selected_get_off_row);
                break;
            case GET_OFF:
                colorCode = ContextCompat.getColor(fragment.getContext(), R.color.get_off_row);
                break;
            case SELECTED_GET_ON:
                colorCode = ContextCompat.getColor(fragment.getContext(), R.color.selected_get_on_row);
                break;
            case GET_ON:
                colorCode = ContextCompat.getColor(fragment.getContext(), R.color.get_on_row);
                break;
            default:
                Log.e(TAG, "unexpected PassengerRecord: " + passengerRecord);
        }
        convertView.setBackgroundColor(colorCode);
    }

    private void setMemoButtonView(View convertView, PassengerRecord passengerRecord) {
        View memoButton = convertView.findViewById(R.id.memo_button);
        View memoButtonLayout = convertView.findViewById(R.id.memo_button_layout);
        memoButton.setTag(passengerRecord);
        memoButtonLayout.setTag(passengerRecord);
        memoButton.setOnClickListener(onClickMemoButtonListener);
        memoButtonLayout.setOnClickListener(onClickMemoButtonListener);
        memoButtons.put(memoButton, true);
        if (!passengerRecord.reservationMemo.isEmpty() || !passengerRecord.userMemo.isEmpty()) {
            memoButtonLayout.setVisibility(View.VISIBLE);
        } else {
            memoButtonLayout.setVisibility(View.GONE);
        }
    }

    public void toggleBlink() {
        memoButtonsVisible = !memoButtonsVisible;
        for (Entry<View, Boolean> entry : memoButtons.entrySet()) {
            View memoButton = entry.getKey();
            if (memoButton == null) {
                continue;
            }
            if (memoButtonsVisible) {
                memoButton.setVisibility(View.VISIBLE);
            } else {
                memoButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void update(List<PassengerRecord> passengerRecords) {
        clear();
        List<PassengerRecord> sortedPassengerRecords = Lists.newArrayList(passengerRecords);
        sortedPassengerRecords.sort(PassengerRecord.DEFAULT_COMPARATOR);
        for (PassengerRecord passengerRecord : sortedPassengerRecords) {
            add(passengerRecord);
        }
        notifyDataSetChanged();
    }
}
