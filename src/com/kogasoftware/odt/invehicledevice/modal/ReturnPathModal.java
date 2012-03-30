package com.kogasoftware.odt.invehicledevice.modal;

import java.util.LinkedList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.arrayadapter.ReservationCandidateArrayAdapter;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.ReservationCandidate;
import com.kogasoftware.odt.webapi.model.User;

class MyAsyncTask extends AsyncTask<Void, Void, String> {
	final String TAG = MyAsyncTask.class.getSimpleName();

	@Override
	protected String doInBackground(Void... args) {
		return "";
	}
}

public class ReturnPathModal extends Modal {
	private static final String TAG = ReturnPathModal.class.getSimpleName();
	private final InVehicleDeviceActivity inVehicleDeviceActivity;
	private ProgressDialog searchingDialog;
	private ProgressDialog sendingDialog;
	private Reservation currentReservation = new Reservation();

	public ReturnPathModal(InVehicleDeviceActivity inVehicleDeviceActivity) {
		super(inVehicleDeviceActivity, R.layout.return_path_modal);
		setId(R.id.return_path_modal);
		this.inVehicleDeviceActivity = inVehicleDeviceActivity;

		searchingDialog = new ProgressDialog(getContext());
		searchingDialog.setMessage("予約情報を受信しています");
		searchingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		searchingDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
			}
		});

		sendingDialog = new ProgressDialog(getContext());
		sendingDialog.setMessage("予約情報を送信しています");
		sendingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		sendingDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
			}
		});
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (searchingDialog.isShowing()) {
			searchingDialog.cancel();
		}
		if (sendingDialog.isShowing()) {
			sendingDialog.dismiss();
		}
	}

	public void show(Reservation reservation) {
		if (isShown()) {
			return;
		}

		currentReservation = reservation;
		TextView returnPathTitleTextView = (TextView) findViewById(R.id.return_path_title_text_view);
		String title = "予約番号 " + currentReservation.getId();
		Optional<User> user = currentReservation.getUser();
		if (user.isPresent()) {
			title += " " + user.get().getLastName() + " "
					+ user.get().getFirstName() + "様";
		}
		title += " 復路の予約";
		returnPathTitleTextView.setText(title);

		Spinner hourSpinner = (Spinner) findViewById(R.id.reservation_candidate_hour_spinner);
		String[] hours = new String[] { "9", "10", "11", "12", "13", "14", "20" };
		ArrayAdapter<String> hourAdapter = new ArrayAdapter<String>(
				getContext(), android.R.layout.simple_spinner_item, hours);
		hourSpinner.setAdapter(hourAdapter);

		String[] minutes = new String[] { "0", "1", "2", "50", "51", "53", "59" };
		Spinner minuteSpinner = (Spinner) findViewById(R.id.reservation_candidate_minute_spinner);
		ArrayAdapter<String> minuteAdapter = new ArrayAdapter<String>(
				getContext(), android.R.layout.simple_spinner_item, minutes);
		minuteSpinner.setAdapter(minuteAdapter);

		String[] inOrOut = { "乗車", "降車" };
		Spinner inOrOutSpinner = (Spinner) findViewById(R.id.reservation_candidate_in_or_out_spinner);
		ArrayAdapter<String> inOrOutAdapter = new ArrayAdapter<String>(
				getContext(), android.R.layout.simple_spinner_item, inOrOut);
		inOrOutSpinner.setAdapter(inOrOutAdapter);

		final ListView reservationCandidateListView = (ListView) findViewById(R.id.reservation_candidates_list_view);
		ReservationCandidateArrayAdapter adapter = new ReservationCandidateArrayAdapter(
				inVehicleDeviceActivity,
				R.layout.reservation_candidate_list_row, new LinkedList<ReservationCandidate>());
		reservationCandidateListView.setAdapter(adapter);

		final Button doReservationButton = (Button) findViewById(R.id.do_reservation_button);
		doReservationButton.setVisibility(View.INVISIBLE);
		doReservationButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				sendingDialog.show();
				AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... params) {
						try {
							inVehicleDeviceActivity.getDataSource()
							.postReservation(0);
							return null;
						} catch (WebAPIException e) {
							e.printStackTrace();
						}
						cancel(true);
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						sendingDialog.dismiss();
						if (this.isCancelled()) {
							return;
						}
						hide();
					}
				};
				task.execute();
			}
		});

		Button searchReturnPathButton = (Button) findViewById(R.id.search_return_path_button);
		searchReturnPathButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				searchingDialog.show();
				AsyncTask<Void, Void, List<ReservationCandidate>> task = new AsyncTask<Void, Void, List<ReservationCandidate>>() {
					@Override
					protected List<ReservationCandidate> doInBackground(
							Void... params) {
						try {
							return inVehicleDeviceActivity.getDataSource()
									.postReservationCandidates(0, 0, 0);
						} catch (WebAPIException e) {
							e.printStackTrace();
						}
						cancel(true);
						return new LinkedList<ReservationCandidate>();
					}

					@Override
					protected void onPostExecute(
							List<ReservationCandidate> result) {
						searchingDialog.dismiss();
						if (this.isCancelled()) {
							return;
						}
						ReservationCandidateArrayAdapter adapter = new ReservationCandidateArrayAdapter(
								inVehicleDeviceActivity,
								R.layout.reservation_candidate_list_row, result);
						reservationCandidateListView.setAdapter(adapter);
						doReservationButton.setVisibility(View.VISIBLE);
					}
				};
				task.execute();
			}
		});

		super.show();
	}
}
