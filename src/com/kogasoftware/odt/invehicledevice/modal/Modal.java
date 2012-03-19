package com.kogasoftware.odt.invehicledevice.modal;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.common.collect.Lists;

public class Modal extends LinearLayout implements
		OnTouchListener {

	// staticなメンバにViewを持つので、メモリリークを未然に防ぐためWeakReferenceを使う
	private static final Queue<WeakReference<Modal>> attachedInstances = new ConcurrentLinkedQueue<WeakReference<Modal>>();

	private static void removeNullAttachedInstances() {
		for (WeakReference<Modal> attachedInstance : getAttachedInstances()) {
			if (attachedInstance.get() == null) {
				attachedInstances.remove(attachedInstance);
			}
		}
	}

	private final WeakReference<Modal> thisWeakReference;
	private final int resourceId;
	private final LayoutInflater layoutInflater;

	public static List<WeakReference<Modal>> getAttachedInstances() {
		return Lists.newLinkedList(attachedInstances);
	}

	public Modal(Context context, AttributeSet attrs, int resourceId) {
		super(context, attrs);
		this.resourceId = resourceId;
		this.layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		thisWeakReference = new WeakReference<Modal>(this);
		// setOnTouchListener(this);
	}

	@Override
	protected void onAttachedToWindow() {		
		super.onAttachedToWindow();
		this.addView(layoutInflater.inflate(resourceId, null));
		attachedInstances.add(thisWeakReference);
	}

	@Override
	protected void onDetachedFromWindow() {
		attachedInstances.remove(thisWeakReference);
		removeNullAttachedInstances();
		this.removeAllViews();
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
