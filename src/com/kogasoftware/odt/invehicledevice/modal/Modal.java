package com.kogasoftware.odt.invehicledevice.modal;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kogasoftware.odt.invehicledevice.R;

public class Modal extends LinearLayout implements OnTouchListener {
	private final FrameLayout parent;

	public Modal(Context context, View rootView, int resourceId) {
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.addView(layoutInflater.inflate(resourceId, null),
				new Modal.LayoutParams(Modal.LayoutParams.FILL_PARENT,
						Modal.LayoutParams.FILL_PARENT));
		parent = (FrameLayout) rootView.findViewById(R.id.modal_layout);
		setVisibility(View.GONE);
		parent.addView(this);
	}

	public Modal(Activity activity, int resourceId) {
		this(activity, activity.getWindow().getDecorView(), resourceId);
	}

	/**
	 * 表示時にパラメーターを渡す必要がある場合、直接呼ばれると不都合がある場合もあるため protectedとしサブクラスでオーバーライドさせる
	 */
	protected void show() {
		setVisibility(View.VISIBLE);
	}

	public void hide() {
		setVisibility(View.GONE);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	private Float lastMotionEventX = 0f;

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		Toast.makeText(getContext(), "Hello", Toast.LENGTH_LONG).show();

		switch (motionEvent.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastMotionEventX = motionEvent.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			layout((int) (getLeft() - lastMotionEventX + motionEvent.getX()),
					getTop(), getWidth(), getBottom());
			lastMotionEventX = motionEvent.getX();
			break;
		case MotionEvent.ACTION_UP:
			// layout(0, getTop(), getWidth(), getBottom());
			break;
		case MotionEvent.ACTION_CANCEL:
			// layout(0, getTop(), getWidth(), getBottom());
			break;
		}
		return true;
	}
}
