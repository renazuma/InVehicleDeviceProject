package com.kogasoftware.odt.invehicledevice.view.fragment.modal;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.Fragments;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

/**
 * ユーザーメモと予約メモ表示画面
 */
public class PassengerRecordMemoFragment extends Fragment {
    private static final String PASSENGER_RECORD_KEY = "passenger_record";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        PassengerRecord passengerRecord = (PassengerRecord) getArguments()
                .getSerializable(PASSENGER_RECORD_KEY);
        Button closeButton = (Button) view
                .findViewById(R.id.passenger_record_memo_close_button);
        closeButton.setOnClickListener(v -> Fragments.hide(PassengerRecordMemoFragment.this));
        TextView titleTextView = (TextView) view
                .findViewById(R.id.memo_title_text_view);
        StringBuilder title = new StringBuilder();
        title.append(passengerRecord.getDisplayName());
        title.append("  予約番号：" + passengerRecord.reservationId);
        titleTextView.setText(title);

        TextView userMemoTextView = (TextView) view
                .findViewById(R.id.user_memo_text_view);
        userMemoTextView.setText(StringUtils.join(
                passengerRecord.getUserNotes(), SystemUtils.LINE_SEPARATOR));
        TextView reservationMemoTextView = (TextView) view
                .findViewById(R.id.reservation_memo_text_view);
        reservationMemoTextView.setText(passengerRecord.reservationMemo);
    }

    public static PassengerRecordMemoFragment newInstance(
            PassengerRecord passengerRecord) {
        PassengerRecordMemoFragment fragment = new PassengerRecordMemoFragment();
        Bundle args = new Bundle();
        args.putSerializable(PASSENGER_RECORD_KEY, passengerRecord);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.passenger_record_memo_fragment,
                container, false);
    }
}
