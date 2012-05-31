package com.kogasoftware.odt.invehicledevice.ui.modalview;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.AsyncTask;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.ui.BigToast;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.ReservationCandidateArrayAdapter;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.Demand;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.ReservationCandidate;
import com.kogasoftware.odt.webapi.model.User;

public class ReturnPathModalView extends ModalView {
	public static class ShowEvent {
		public final Reservation reservation;

		public ShowEvent(Reservation reservation) {
			Preconditions.checkNotNull(reservation);
			this.reservation = reservation;
		}
	}

	private static final String TAG = ReturnPathModalView.class.getSimpleName();

	private Reservation currentReservation = new Reservation();
	private ReservationCandidate currentReservationCandidate = new ReservationCandidate();
	// private static final String TAG =
	// ReturnPathModalView.class.getSimpleName();
	private final ProgressDialog searchingDialog;
	private final ProgressDialog sendingDialog;
	private final ListView reservationCandidateListView;
	private final Button doReservationButton;
	private final Button reservationCandidateScrollUpButton;
	private final Button reservationCandidateScrollDownButton;
	private final Spinner hourSpinner;
	private final Spinner minuteSpinner;
	private final Spinner inOrOutSpinner;

	public ReturnPathModalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.return_path_modal_view);
		setCloseOnClick(R.id.return_path_close_button);

		reservationCandidateListView = (ListView) findViewById(R.id.reservation_candidates_list_view);
		reservationCandidateListView.scrollTo(0, 0);
		doReservationButton = (Button) findViewById(R.id.do_reservation_button);
		reservationCandidateScrollUpButton = (Button) findViewById(R.id.reservation_candidate_scroll_up_button);
		reservationCandidateScrollDownButton = (Button) findViewById(R.id.reservation_candidate_scroll_down_button);
		hourSpinner = (Spinner) findViewById(R.id.reservation_candidate_hour_spinner);
		minuteSpinner = (Spinner) findViewById(R.id.reservation_candidate_minute_spinner);
		inOrOutSpinner = (Spinner) findViewById(R.id.reservation_candidate_in_or_out_spinner);

		searchingDialog = new ProgressDialog(getContext());
		searchingDialog.setMessage(Html.fromHtml("<big>予約情報を受信しています</big>"));
		searchingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		searchingDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
			}
		});

		sendingDialog = new ProgressDialog(getContext());
		sendingDialog.setMessage(Html.fromHtml("<big>予約情報を送信しています</big>"));
		sendingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		sendingDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
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
		String title = "";
		for (User user : currentReservation.getUser().asSet()) {
			title += user.getLastName() + " " + user.getFirstName() + " 様 ";
		}
		title += "予約番号:" + currentReservation.getId();
		title += " 復路の予約";
		returnPathTitleTextView.setText(title);

		Date now = CommonLogic.getDate();

		List<String> hours = new LinkedList<String>();
		for (Integer hour = now.getHours(); hour < 24; ++hour) {
			hours.add(hour.toString());
		}
		ArrayAdapter<String> hourAdapter = new ArrayAdapter<String>(
				getContext(), android.R.layout.simple_spinner_item, hours);
		hourAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		hourSpinner.setAdapter(hourAdapter);
		hourSpinner.setSelection(0); // 必ず一つは要素が入る

		List<String> minutes = new LinkedList<String>();
		for (Integer minute = 0; minute < 60; ++minute) {
			minutes.add(minute.toString());
		}
		ArrayAdapter<String> minuteAdapter = new ArrayAdapter<String>(
				getContext(), android.R.layout.simple_spinner_item, minutes);
		minuteAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		minuteSpinner.setAdapter(minuteAdapter);
		minuteSpinner.setSelection(now.getMinutes());

		String[] inOrOut = { "乗車", "降車" };
		ArrayAdapter<String> inOrOutAdapter = new ArrayAdapter<String>(
				getContext(), android.R.layout.simple_spinner_item, inOrOut);
		inOrOutAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		inOrOutSpinner.setAdapter(inOrOutAdapter);

		reservationCandidateScrollUpButton.setVisibility(View.INVISIBLE);
		reservationCandidateScrollDownButton.setVisibility(View.INVISIBLE);
		reservationCandidateListView.setVisibility(View.INVISIBLE);

		doReservationButton.setEnabled(false);
		reservationCandidateListView
				.setAdapter(new ReservationCandidateArrayAdapter(getContext(),
						new LinkedList<ReservationCandidate>()));

		doReservationButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				onDoReservationButtonClick();
			}
		});

		Button searchReturnPathButton = (Button) findViewById(R.id.search_return_path_button);
		searchReturnPathButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				onSearchReturnPathButtonClick();
			}
		});

		super.show();
	}

	protected void onSearchReturnPathButtonClick() {
		Integer hour = 0;
		Object hourObject = hourSpinner.getSelectedItem();
		if (!(hourObject instanceof String)) {
			Log.w(TAG, "hourObject is not String");
			return;
		}
		try {
			hour = Integer.parseInt((String) hourObject);
		} catch (NumberFormatException e) {
			Log.w(TAG, e);
			return;
		}

		Integer minute = 0;
		Object minuteObject = minuteSpinner.getSelectedItem();
		if (!(minuteObject instanceof String)) {
			Log.w(TAG, "minuteObject is not String");
			return;
		}
		try {
			minute = Integer.parseInt((String) minuteObject);
		} catch (NumberFormatException e) {
			Log.w(TAG, e);
			return;
		}

		searchingDialog.show();
		Calendar now = Calendar.getInstance();
		now.setTime(CommonLogic.getDate());
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH),
				now.get(Calendar.DAY_OF_MONTH), hour, minute);

		final Demand demand = new Demand();
		if (inOrOutSpinner.getSelectedItemPosition() == 0) {
			demand.setDepartureTime(calendar.getTime());
		} else {
			demand.setArrivalTime(calendar.getTime());
		}
		demand.setUserId(currentReservation.getUserId().or(0));
		demand.setDeparturePlatformId(currentReservation.getArrivalPlatformId());
		demand.setArrivalPlatformId(currentReservation.getDeparturePlatformId());

		AsyncTask<Void, Void, List<ReservationCandidate>> task = new AsyncTask<Void, Void, List<ReservationCandidate>>() {
			@Override
			protected List<ReservationCandidate> doInBackground(Void... params) {
				final CountDownLatch countDownLatch = new CountDownLatch(1);
				final List<ReservationCandidate> reservationCandidates = new LinkedList<ReservationCandidate>();
				getCommonLogic().getDataSource().searchReservationCandidate(
						demand,
						new WebAPICallback<List<ReservationCandidate>>() {
							@Override
							public void onException(int reqkey,
									WebAPIException ex) {
								countDownLatch.countDown();
							}

							@Override
							public void onFailed(int reqkey, int statusCode,
									String response) {
								countDownLatch.countDown();
							}

							@Override
							public void onSucceed(int reqkey, int statusCode,
									List<ReservationCandidate> result) {
								reservationCandidates.addAll(result);
								countDownLatch.countDown();
							}
						});
				try {
					countDownLatch.await();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				return reservationCandidates;
			}

			@Override
			protected void onPostExecute(List<ReservationCandidate> result) {
				searchingDialog.dismiss();
				if (isCancelled()) {
					return;
				}
				setReservationCandidates(result);
			}
		};
		task.execute();
	}

	protected void onDoReservationButtonClick() {
		sendingDialog.show();
		AsyncTask<Void, Void, Optional<Reservation>> task = new AsyncTask<Void, Void, Optional<Reservation>>() {
			@Override
			protected Optional<Reservation> doInBackground(Void... params) {
				final AtomicReference<Reservation> outputReservation = new AtomicReference<Reservation>();
				final CountDownLatch countDownLatch = new CountDownLatch(1);
				getCommonLogic().getDataSource().createReservation(
						currentReservationCandidate,
						new WebAPICallback<Reservation>() {
							@Override
							public void onException(int reqkey,
									WebAPIException ex) {
								countDownLatch.countDown();
							}

							@Override
							public void onFailed(int reqkey, int statusCode,
									String response) {
								countDownLatch.countDown();
							}

							@Override
							public void onSucceed(int reqkey, int statusCode,
									Reservation result) {
								outputReservation.set(result);
								countDownLatch.countDown();
							}
						});
				try {
					countDownLatch.await();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				return Optional.fromNullable(outputReservation.get());
			}

			@Override
			protected void onPostExecute(Optional<Reservation> result) {
				sendingDialog.dismiss();
				if (isCancelled()) {
					return;
				}
				if (!result.isPresent()) {
					BigToast.makeText(
							getContext(),
							getResources()
									.getString(R.string.an_error_occurred),
							Toast.LENGTH_LONG).show();
					return;
				}
				hide();
			}
		};
		task.execute();
	}

	protected void setReservationCandidates(
			List<ReservationCandidate> reservationCandidates) {
		if (reservationCandidates.isEmpty()) {
			BigToast.makeText(
					getContext(),
					getResources().getString(
							R.string.reservation_candidate_not_found),
					Toast.LENGTH_LONG).show();
			return;
		}
		reservationCandidates = Lists.partition(reservationCandidates, 10).get(0);

		final ReservationCandidateArrayAdapter adapter = new ReservationCandidateArrayAdapter(
				getContext(), reservationCandidates);
		reservationCandidateListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						adapter.setSelectedPosition(Optional.of(position));
						currentReservationCandidate = adapter.getItem(position);
						doReservationButton.setEnabled(true);
					}
				});
		reservationCandidateListView.setAdapter(adapter);
		reservationCandidateListView.setVisibility(View.VISIBLE);
		reservationCandidateScrollUpButton.setVisibility(View.VISIBLE);
		reservationCandidateScrollDownButton.setVisibility(View.VISIBLE);
	}
}
