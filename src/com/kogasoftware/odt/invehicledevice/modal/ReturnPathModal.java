package com.kogasoftware.odt.invehicledevice.modal;

import java.util.LinkedList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.arrayadapter.ReservationCandidateArrayAdapter;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.ReservationCandidate;
import com.kogasoftware.odt.webapi.model.User;

public class ReturnPathModal extends Modal {
	public static class ShowEvent {
		public final Reservation reservation;

		public ShowEvent(Reservation reservation) {
			Preconditions.checkNotNull(reservation);
			this.reservation = reservation;
		}
	}

	// private static final String TAG = ReturnPathModal.class.getSimpleName();
	private ProgressDialog searchingDialog;
	private ProgressDialog sendingDialog;
	private Reservation currentReservation = new Reservation();

	public ReturnPathModal(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.return_path_modal);

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

	@Subscribe
	public void show(ShowEvent event) {
		if (isShown()) {
			return;
		}
		currentReservation = event.reservation;
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

		final Button reservationCandidateScrollUpButton = (Button) findViewById(R.id.reservation_candidate_scroll_up_button);
		final Button reservationCandidateScrollDownButton = (Button) findViewById(R.id.reservation_candidate_scroll_down_button);
		reservationCandidateScrollUpButton.setVisibility(View.INVISIBLE);
		reservationCandidateScrollDownButton.setVisibility(View.INVISIBLE);
		final Button doReservationButton = (Button) findViewById(R.id.do_reservation_button);
		doReservationButton.setEnabled(false);
		final ListView reservationCandidateListView = (ListView) findViewById(R.id.reservation_candidates_list_view);
		reservationCandidateListView
		.setAdapter(new ReservationCandidateArrayAdapter(getContext(),
				R.layout.reservation_candidate_list_row,
				new LinkedList<ReservationCandidate>()));

		doReservationButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				sendingDialog.show();
				AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... params) {
						try {
							getLogic().getDataSource().postReservation(0);
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
							return getLogic().getDataSource()
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
						final ReservationCandidateArrayAdapter adapter = new ReservationCandidateArrayAdapter(
								getContext(),
								R.layout.reservation_candidate_list_row, result);
						reservationCandidateListView
						.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							@Override
							public void onItemClick(
									AdapterView<?> parent, View view,
									int position, long id) {
								adapter.setSelectedPosition(Optional
										.<Integer> of(position));
								doReservationButton.setEnabled(true);
							}
						});

						reservationCandidateListView.setAdapter(adapter);
						reservationCandidateScrollUpButton
						.setVisibility(View.VISIBLE);
						reservationCandidateScrollDownButton
						.setVisibility(View.VISIBLE);
					}
				};
				task.execute();
			}
		});

		reservationCandidateScrollUpButton
		.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Integer position = reservationCandidateListView
						.getFirstVisiblePosition();
				reservationCandidateListView
				.smoothScrollToPosition(position);
			}
		});

		reservationCandidateScrollDownButton
		.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Integer position = reservationCandidateListView
						.getLastVisiblePosition();
				reservationCandidateListView
				.smoothScrollToPosition(position);
			}
		});

		super.show();
	}
}
