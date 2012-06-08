package com.kogasoftware.odt.invehicledevice.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.kogasoftware.odt.invehicledevice.R;

public class FlickUnneededListView extends FrameLayout {
	private static final String TAG = FlickUnneededListView.class
			.getSimpleName();
	protected static final Integer DISABLED_TEXT_COLOR = Color.GRAY;
	protected static final Integer ENABLED_TEXT_COLOR = Color.BLACK;
	protected final ListView listView;
	protected final Button scrollUpButton;
	protected final Button scrollDownButton;

	public FlickUnneededListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater layoutInflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		addView(layoutInflater.inflate(R.layout.flick_unneeded_list_view, null),
				new FlickUnneededListView.LayoutParams(
						FlickUnneededListView.LayoutParams.FILL_PARENT,
						FlickUnneededListView.LayoutParams.FILL_PARENT));
		listView = (ListView) findViewById(R.id.flick_unneeded_list_view_inner_list_view);
		scrollUpButton = (Button) findViewById(R.id.flick_unneeded_list_view_scroll_up_button);
		scrollDownButton = (Button) findViewById(R.id.flick_unneeded_list_view_scroll_down_button);
		listView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				Boolean scrollUpButtonEnabled = false;
				if (firstVisibleItem > 0) {
					scrollUpButtonEnabled = true;
				} else {
					View firstVisibleView = listView.getChildAt(0);
					if (firstVisibleView != null
							&& firstVisibleView.getTop() < 0) {
						scrollUpButtonEnabled = true;
					}
				}
				int lastVisibleItem = listView.getLastVisiblePosition();
				Boolean scrollDownButtonEnabled = false;
				if (lastVisibleItem < listView.getCount() - 1) {
					scrollDownButtonEnabled = true;
				} else {
					View lastVisibleView = listView.getChildAt(listView
							.getChildCount() - 1);
					if (lastVisibleView != null
							&& lastVisibleView.getBottom() > listView
									.getHeight()) {
						scrollDownButtonEnabled = true;
					}
				}

				scrollUpButton
						.setTextColor(scrollUpButtonEnabled ? ENABLED_TEXT_COLOR
								: DISABLED_TEXT_COLOR);
				scrollDownButton
						.setTextColor(scrollDownButtonEnabled ? ENABLED_TEXT_COLOR
								: DISABLED_TEXT_COLOR);
				scrollUpButton.setEnabled(scrollUpButtonEnabled);
				scrollDownButton.setEnabled(scrollDownButtonEnabled);
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
		});
		scrollUpButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Integer position = listView.getFirstVisiblePosition();
				listView.smoothScrollToPosition(position);
			}
		});
		scrollDownButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Integer position = listView.getLastVisiblePosition();
				listView.smoothScrollToPosition(position);
			}
		});
	}

	public ListView getListView() {
		return listView;
	}
}
