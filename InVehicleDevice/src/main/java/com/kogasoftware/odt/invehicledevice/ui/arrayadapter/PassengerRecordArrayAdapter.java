package com.kogasoftware.odt.invehicledevice.ui.arrayadapter;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import org.joda.time.DateTime;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.ui.fragment.PassengerRecordMemoFragment;
import com.kogasoftware.odt.invehicledevice.utils.Fragments;
import com.kogasoftware.odt.invehicledevice.utils.ViewDisabler;

/**
 * 乗客一覧
 */
public class PassengerRecordArrayAdapter extends ArrayAdapter<PassengerRecord> {
	private static final String TAG = PassengerRecordArrayAdapter.class
			.getSimpleName();
	private static final Integer RESOURCE_ID = R.layout.passenger_record_list_row;
	private final FragmentManager fragmentManager;
	private final LayoutInflater layoutInflater = (LayoutInflater) getContext()
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	private final OperationSchedule operationSchedule;
	private final WeakHashMap<View, Boolean> memoButtons = new WeakHashMap<View, Boolean>();
	private final ContentResolver contentResolver;
	private Boolean memoButtonsVisible = true;
	private final OnTouchListener onTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			Object tag = view.getTag();
			if (!(tag instanceof PassengerRecord)) {
				Log.e(TAG, "\"" + view + "\".getTag() (" + tag
						+ ") is not instanceof PassengerRecord");
				return false;
			}

			PassengerRecord passengerRecord = (PassengerRecord) tag;
			// 色を反転する
			Boolean invertColor = event.getAction() == MotionEvent.ACTION_DOWN;
			// 色を元に戻す
			Boolean restoreColor = event.getAction() == MotionEvent.ACTION_CANCEL;
			// 乗降を実行する
			Boolean execute = event.getAction() == MotionEvent.ACTION_UP;

			if (invertColor || restoreColor) {
				if (operationSchedule.id
						.equals(passengerRecord.arrivalScheduleId)) {
					if ((passengerRecord.getOffTime != null) ^ invertColor) {
						view.setBackgroundColor(PassengerRecord.SELECTED_GET_OFF_COLOR);
					} else {
						view.setBackgroundColor(PassengerRecord.GET_OFF_COLOR);
					}
				} else if (operationSchedule.id
						.equals(passengerRecord.departureScheduleId)) {
					if (passengerRecord.getOnTime != null ^ invertColor) {
						view.setBackgroundColor(PassengerRecord.SELECTED_GET_ON_COLOR);
					} else {
						view.setBackgroundColor(PassengerRecord.GET_ON_COLOR);
					}
				}
			} else if (execute) {
				DateTime now = DateTime.now();
				if (operationSchedule.id
						.equals(passengerRecord.arrivalScheduleId)) {
					passengerRecord.ignoreGetOffMiss = false;
					if (passengerRecord.getOffTime == null) {
						if (passengerRecord.getOnTime == null) {
							passengerRecord.getOnTime = now;
						}
						passengerRecord.getOffTime = now;
					} else {
						passengerRecord.getOffTime = null;
					}
				} else if (operationSchedule.id
						.equals(passengerRecord.departureScheduleId)) {
					passengerRecord.ignoreGetOnMiss = false;
					if (passengerRecord.getOnTime == null) {
						passengerRecord.getOnTime = now;
					} else {
						passengerRecord.getOnTime = null;
						passengerRecord.getOffTime = null;
					}
				}
				final ContentValues values = passengerRecord.toContentValues();
				final String where = PassengerRecord.Columns._ID + " = ?";
				final String[] whereArgs = new String[]{passengerRecord.id
						.toString()};
				new Thread() {
					@Override
					public void run() {
						contentResolver.update(PassengerRecord.CONTENT.URI,
								values, where, whereArgs);
					}
				}.start();
				notifyDataSetChanged();
			}
			return true;
		}
	};

	protected final OnClickListener onClickMemoButtonListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			ViewDisabler.disable(view);
			Object tag = view.getTag();
			if (!(tag instanceof PassengerRecord)) {
				Log.e(TAG, "\"" + view + "\".getTag() (" + tag
						+ ") is not instanceof PassengerRecord");
				return;
			}
			PassengerRecord passengerRecord = (PassengerRecord) tag;
			Fragments.showModalFragment(fragmentManager,
					PassengerRecordMemoFragment.newInstance(passengerRecord));
		}
	};

	public PassengerRecordArrayAdapter(Fragment fragment,
			OperationSchedule operationSchedule) {
		super(fragment.getActivity(), RESOURCE_ID);
		this.fragmentManager = fragment.getFragmentManager();
		this.contentResolver = fragment.getActivity().getContentResolver();
		this.operationSchedule = operationSchedule;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(RESOURCE_ID, null);
		}

		PassengerRecord passengerRecord = getItem(position);
		TextView passengerCountTextView = (TextView) convertView
				.findViewById(R.id.passenger_count_text_view);
		passengerCountTextView.setText(passengerRecord.passengerCount + "名");

		// メモボタン
		View memoButton = convertView.findViewById(R.id.memo_button);
		View memoButtonLayout = convertView
				.findViewById(R.id.memo_button_layout);
		memoButton.setTag(passengerRecord);
		memoButtonLayout.setTag(passengerRecord);
		memoButton.setOnClickListener(onClickMemoButtonListener);
		memoButtonLayout.setOnClickListener(onClickMemoButtonListener);
		memoButtons.put(memoButton, true);
		if (!passengerRecord.reservationMemo.isEmpty()
				|| !passengerRecord.userMemo.isEmpty()) {
			memoButtonLayout.setVisibility(View.VISIBLE);
		} else {
			memoButtonLayout.setVisibility(View.GONE);
		}

		// 行の表示
		convertView.setTag(passengerRecord);
		convertView.setOnTouchListener(onTouchListener);

		ImageView selectMarkImageView = (ImageView) convertView
				.findViewById(R.id.select_mark_image_view);

		if (operationSchedule.id.equals(passengerRecord.arrivalScheduleId)) {
			selectMarkImageView.setImageResource(R.drawable.get_off);
			if (passengerRecord.getOffTime != null) {
				convertView
						.setBackgroundColor(PassengerRecord.SELECTED_GET_OFF_COLOR);
			} else {
				convertView.setBackgroundColor(PassengerRecord.GET_OFF_COLOR);
			}
		} else if (operationSchedule.id
				.equals(passengerRecord.departureScheduleId)) {
			selectMarkImageView.setImageResource(R.drawable.get_on);
			if (passengerRecord.getOnTime != null) {
				convertView
						.setBackgroundColor(PassengerRecord.SELECTED_GET_ON_COLOR);
			} else {
				convertView.setBackgroundColor(PassengerRecord.GET_ON_COLOR);
			}
		} else {
			Log.e(TAG, "unexpected PassengerRecord: " + passengerRecord);
		}

		TextView userNameView = (TextView) convertView
				.findViewById(R.id.user_name);
		userNameView.setText(passengerRecord.getDisplayName());

		return convertView;
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
		List<PassengerRecord> sortedPassengerRecords = Lists
				.newArrayList(passengerRecords);
		Collections.sort(sortedPassengerRecords,
				PassengerRecord.DEFAULT_COMPARATOR);
		for (PassengerRecord passengerRecord : sortedPassengerRecords) {
			add(passengerRecord);
		}
		notifyDataSetChanged();
	}
}
