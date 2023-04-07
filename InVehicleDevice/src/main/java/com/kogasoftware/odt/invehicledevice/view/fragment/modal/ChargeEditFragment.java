package com.kogasoftware.odt.invehicledevice.view.fragment.modal;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.DefaultCharge;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.FragmentUtils;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.OperationSchedulesSyncFragmentAbstract;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 料金設定画面
 */
public class ChargeEditFragment extends OperationSchedulesSyncFragmentAbstract {
    private static final String TAG = ChargeEditFragment.class.getSimpleName();
    private static final String OPERATION_SCHEDULE_ID_KEY = "operation_schedule_id";
    private static final String PASSENGER_RECORD_ID_KEY = "passenger_record_id";

    public static ChargeEditFragment newInstance(Long operationScheduleId, Long passengerRecordId) {
        ChargeEditFragment fragment = new ChargeEditFragment();
        Bundle args = new Bundle();
        args.putLong(OPERATION_SCHEDULE_ID_KEY, operationScheduleId);
        args.putLong(PASSENGER_RECORD_ID_KEY, passengerRecordId);
        fragment.setArguments(args);
        return fragment;
    }

    private ContentResolver contentResolver;
    private Long operationScheduleId;
    private Long passengerRecordId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.charge_edit_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        contentResolver = getActivity().getContentResolver();
        Bundle args = getArguments();
        operationScheduleId = args.getLong(OPERATION_SCHEDULE_ID_KEY);
        passengerRecordId = args.getLong(PASSENGER_RECORD_ID_KEY);
    }

    @Override
    protected void onSchedulesRefresh(
            LinkedList<OperationSchedule> operationSchedules,
            LinkedList<PassengerRecord> passengerRecords) {

        final PassengerRecord passengerRecord = getById(passengerRecords, passengerRecordId);
        if (passengerRecord == null) {
            FragmentUtils.hideModal(this);
            return;
        }

        View view = getView();

        final TextView chargerNameView = view.findViewById(R.id.charger_name);
        final Button firstDefaultChargeButtonView = view.findViewById(R.id.first_default_charge_button);
        final Button secondDefaultChargeButtonView = view.findViewById(R.id.second_default_charge_button);
        final Button thirdDefaultChargeButtonView = view.findViewById(R.id.third_default_charge_button);
        final EditText chargeEditTextView = view.findViewById(R.id.charge_edit_text);
        final Button chargeAndGetOnButtonView = view.findViewById(R.id.charge_and_get_on_button);
        final Button quitChargeButtonView = view.findViewById(R.id.quit_charge_button);
        final TextView expectedChargeTextView = view.findViewById(R.id.expected_charge_text);
        final TextView expectedChargeMonetaryUnitView = view.findViewById(R.id.expected_charge_monetary_unit);

        final ArrayList<DefaultCharge> defaultCharges = (ArrayList) (((InVehicleDeviceActivity) getContext()).defaultCharges);

        chargerNameView.setText(passengerRecord.getDisplayName() + " 様");

        chargeEditTextView.setOnFocusChangeListener((view1, hasFocus) -> {
            if (!hasFocus) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view1.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        if (defaultCharges.size() >= 1) {
            firstDefaultChargeButtonView.setText(defaultCharges.get(0).value.toString());
            firstDefaultChargeButtonView.setEnabled(true);
            // expectedChargeがある場合は、あとで上書きされる
            chargeEditTextView.setText(defaultCharges.get(0).value.toString());
        }
        if (defaultCharges.size() >= 2) {
            secondDefaultChargeButtonView.setText(defaultCharges.get(1).value.toString());
            secondDefaultChargeButtonView.setEnabled(true);
        }
        if (defaultCharges.size() >= 3) {
            thirdDefaultChargeButtonView.setText(defaultCharges.get(2).value.toString());
            thirdDefaultChargeButtonView.setEnabled(true);
        }

        firstDefaultChargeButtonView.setOnClickListener(v -> chargeEditTextView.setText(defaultCharges.get(0).value.toString()));

        secondDefaultChargeButtonView.setOnClickListener(v -> chargeEditTextView.setText(defaultCharges.get(1).value.toString()));

        thirdDefaultChargeButtonView.setOnClickListener(v -> chargeEditTextView.setText(defaultCharges.get(2).value.toString()));

        if (passengerRecord.expectedCharge == null) {
            expectedChargeTextView.setText("登録なし");
            expectedChargeMonetaryUnitView.setText("");
        } else {
            chargeEditTextView.setText(passengerRecord.expectedCharge.toString());
            expectedChargeTextView.setText(passengerRecord.expectedCharge.toString());
        }

        chargeAndGetOnButtonView.setOnClickListener(v -> {
            passengerRecord.getOnTime = DateTime.now();
            if (operationScheduleId.equals(passengerRecord.departureScheduleId)) {
                passengerRecord.ignoreGetOnMiss = false;
            } else {
                passengerRecord.ignoreGetOffMiss = false;
                passengerRecord.getOffTime = DateTime.now();
            }

            if (!StringUtils.isEmpty(chargeEditTextView.getText().toString())) {
                passengerRecord.paidCharge = (long) Integer.parseInt(chargeEditTextView.getText().toString());
            }

            Log.i(TAG, "user operation: Charge And GetOn button clicked. { "
                    + "passengerRecordId: " + passengerRecord.id + ","
                    + " charge: " + passengerRecord.paidCharge + " }");

            final ContentValues values = passengerRecord.toContentValues();
            final String where = PassengerRecord.Columns._ID + " = ?";
            final String[] whereArgs = new String[]{passengerRecord.id.toString()};
            new Thread() {
                @Override
                public void run() {
                    contentResolver.update(PassengerRecord.CONTENT.URI, values, where, whereArgs);
                }
            }.start();
            contentResolver.notifyChange(PassengerRecord.CONTENT.URI, null);
            FragmentUtils.hideModal(ChargeEditFragment.this);
        });

        quitChargeButtonView.setOnClickListener(v -> {
            Log.i(TAG, "user operation: Quit Charge button clicked.");
            FragmentUtils.hideModal(ChargeEditFragment.this);
        });
    }

    private static PassengerRecord getById(List<PassengerRecord> passengerRecords, Long id) {
        for (PassengerRecord p : passengerRecords) {
            if (id.equals(p.id)) {
                return p;
            }
        }
        return null;
    }
}
