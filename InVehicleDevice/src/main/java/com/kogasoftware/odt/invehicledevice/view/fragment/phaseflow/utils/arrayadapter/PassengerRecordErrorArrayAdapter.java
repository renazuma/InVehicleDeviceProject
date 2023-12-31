package com.kogasoftware.odt.invehicledevice.view.fragment.phaseflow.utils.arrayadapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.PassengerRecord;

import java.util.List;

/**
 * 乗車予定なのに乗ってないor降車予定なのに降りてない乗客一覧
 */
public class PassengerRecordErrorArrayAdapter extends ArrayAdapter<PassengerRecord> {

    public interface OnPassengerRecordChangeListener {
        void onPassengerRecordChange(PassengerRecordErrorArrayAdapter adapter);
    }

    private static final String TAG = PassengerRecordErrorArrayAdapter.class.getSimpleName();
    protected static final Integer RESOURCE_ID = R.layout.passenger_record_error_list_row;
    protected final FragmentManager fragmentManager;
    protected final LayoutInflater layoutInflater = (LayoutInflater) getContext()
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    protected final List<OperationSchedule> operationSchedules;
    protected final ContentResolver contentResolver;

    protected final OnClickListener onClickIgnoreButtonListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Object tag = view.getTag();
            if (!(tag instanceof PassengerRecord)) {
                Log.e(TAG, "\"" + view + "\".getTag() (" + tag + ") is not instanceof PassengerRecord");
                return;
            }
            final PassengerRecord passengerRecord = (PassengerRecord) tag;

            // phaseのoperationSchedulesとpassengerRecordのidを比較する事で過不足無く対応出来ているかを調べる
            OperationSchedule operationSchedule = null;
            for (OperationSchedule tmpOS : operationSchedules) {
                if (tmpOS.id.equals(passengerRecord.arrivalScheduleId) || tmpOS.id.equals(passengerRecord.departureScheduleId)) {
                    operationSchedule = tmpOS;
                    break;
                }
            }

            if (operationSchedule.id.equals(passengerRecord.arrivalScheduleId)) {
                passengerRecord.ignoreGetOffMiss = !passengerRecord.ignoreGetOffMiss;
            } else {
                passengerRecord.ignoreGetOnMiss = !passengerRecord.ignoreGetOnMiss;
            }

            final String where = PassengerRecord.Columns._ID + " = ?";
            final String[] whereArgs = new String[]{passengerRecord.id.toString()};
            new Thread() {
                @Override
                public void run() {
                    contentResolver.update(PassengerRecord.CONTENT.URI,
                            passengerRecord.toContentValues(),
                            where,
                            whereArgs);
                }
            }.start();
            notifyDataSetChanged();
        }
    };

    public PassengerRecordErrorArrayAdapter(Fragment fragment, List<OperationSchedule> operationSchedules) {
        super(fragment.getActivity(), RESOURCE_ID);
        this.fragmentManager = fragment.getFragmentManager();
        this.contentResolver = fragment.getActivity().getContentResolver();
        this.operationSchedules = operationSchedules;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(RESOURCE_ID, null);
        }

        PassengerRecord passengerRecord = getItem(position);

        // 無視ボタン
        CheckBox ignoreButton = convertView.findViewById(R.id.passenger_record_error_ignore_button);
        ignoreButton.setTag(passengerRecord);
        ignoreButton.setOnClickListener(onClickIgnoreButtonListener);
        ignoreButton.setTextColor(Color.RED);


        OperationSchedule operationSchedule = null;
        for (OperationSchedule tmpOS : operationSchedules) {
            if (passengerRecord.arrivalScheduleId.equals(tmpOS.id) || passengerRecord.departureScheduleId.equals(tmpOS.id)) {
                operationSchedule = tmpOS;
            }
        }

        //出発／到着差分
        String errorMessage = passengerRecord.getDisplayName() + " 様が";
        if (operationSchedule.id.equals(passengerRecord.arrivalScheduleId) && passengerRecord.getOffTime == null) {
            ignoreButton.setChecked(passengerRecord.ignoreGetOffMiss);
            ignoreButton.setText("未降車でよい");
            errorMessage += "未降車です";
            if (passengerRecord.ignoreGetOffMiss) {
                ignoreButton.setBackgroundResource(R.drawable.ignore_button_pressed);
            } else {
                ignoreButton.setBackgroundResource(R.drawable.ignore_button);
            }
        } else if (operationSchedule.id.equals(passengerRecord.departureScheduleId)
                && passengerRecord.getOnTime == null) {
            ignoreButton.setChecked(passengerRecord.ignoreGetOnMiss);
            ignoreButton.setText("未乗車でよい");
            errorMessage += "未乗車です";
            if (passengerRecord.ignoreGetOnMiss) {
                ignoreButton.setBackgroundResource(R.drawable.ignore_button_pressed);
            } else {
                ignoreButton.setBackgroundResource(R.drawable.ignore_button);
            }
        } else {
            Log.e(TAG, "unexpected PassengerRecord: " + passengerRecord);
        }

        // エラーメッセージ
        TextView errorMessageTextView = convertView.findViewById(R.id.passenger_record_error_message);
        errorMessageTextView.setText(errorMessage);

        return convertView;
    }

    public void update(List<PassengerRecord> passengerRecords) {
        clear();
        List<PassengerRecord> sortedPassengerRecord = Lists.newArrayList(passengerRecords);
        sortedPassengerRecord.sort(PassengerRecord.DEFAULT_COMPARATOR);
        for (PassengerRecord passengerRecord : sortedPassengerRecord) {
            add(passengerRecord);
        }
        notifyDataSetChanged();
    }

    public Boolean hasError() {
        // phaseのoperationSchedulesとpassengerRecordのidを比較する事で過不足無く対応出来ているかを調べる
        for (int count = 0; count < getCount(); ++count) {
            PassengerRecord passengerRecord = getItem(count);

            for (OperationSchedule operationSchedule : operationSchedules) {
                if (operationSchedule.id.equals(passengerRecord.arrivalScheduleId)
                        && passengerRecord.getOffTime == null
                        && !passengerRecord.ignoreGetOffMiss) {
                    return true;
                }
            }

            for (OperationSchedule operationSchedule : operationSchedules) {
                if (operationSchedule.id.equals(passengerRecord.departureScheduleId)
                        && passengerRecord.getOnTime == null
                        && !passengerRecord.ignoreGetOnMiss) {
                    return true;
                }
            }
        }
        return false;
    }
}
