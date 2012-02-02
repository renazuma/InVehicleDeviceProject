package com.kogasoftware.odt.invehicledevice;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.common.collect.Lists;

public class OverlayLinearLayout extends LinearLayout implements
		OnTouchListener {

	// staticなメンバにViewを持つので、メモリリークを未然に防ぐためWeakReferenceを使う
	private static final Queue<WeakReference<OverlayLinearLayout>> attachedInstances = new ConcurrentLinkedQueue<WeakReference<OverlayLinearLayout>>();
	private final WeakReference<OverlayLinearLayout> thisWeakReference;

	public static List<WeakReference<OverlayLinearLayout>> getAttachedInstances() {
		return Lists.newLinkedList(attachedInstances);
	}

	public OverlayLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		thisWeakReference = new WeakReference<OverlayLinearLayout>(this);
		setOnTouchListener(this);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		attachedInstances.add(thisWeakReference);
	}

	@Override
	protected void onDetachedFromWindow() {
		attachedInstances.remove(thisWeakReference);
		super.onDetachedFromWindow();
	}

	public void hide() {
		setVisibility(View.GONE);
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
