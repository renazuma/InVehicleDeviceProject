package com.kogasoftware.odt.invehicledevice.modal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceLogic;

public class Modal extends FrameLayout implements OnTouchListener {
	private Float lastMotionEventX = 0f;

	private InVehicleDeviceLogic logic = new InVehicleDeviceLogic();

	public Modal(Context context, AttributeSet attrs) {
		super(context, attrs);
		setVisibility(View.GONE);
	}

	protected InVehicleDeviceLogic getLogic() {
		return logic;
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

	protected void setContentView(int resourceId) {
		LayoutInflater layoutInflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.addView(layoutInflater.inflate(resourceId, null),
				new Modal.LayoutParams(Modal.LayoutParams.FILL_PARENT,
						Modal.LayoutParams.FILL_PARENT));

		TypedArray typedArray = getContext().obtainStyledAttributes(
				new int[] { android.R.attr.background });
		int backgroundColor = typedArray.getColor(0, Color.WHITE);
		setBackgroundColor(backgroundColor);
	}

	@Subscribe
	public void setLogic(InVehicleDeviceLogic.LoadThread.CompleteEvent event) {
		this.logic = event.logic;
	}

	/**
	 * 表示時にパラメーターを渡す必要がある場合、直接呼ばれると不都合がある場合もあるため protectedとしサブクラスでオーバーライドさせる
	 */
	protected void show() {
		setVisibility(View.VISIBLE);
	}
}
