package com.kogasoftware.odt.invehicledevice.map;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * サイズが変更されたら設定したListenerに通知するLinearLayout
 * 
 * @author ksc
 * 
 */
public class OnMeasureDetectableLinerLayout extends LinearLayout {
	public interface OnMeasureListener {
		public void onMeasure(int widthMeasureSpec, int heightMeasureSpec);
	};

	private OnMeasureListener onMeasureListener = new OnMeasureListener() {
		@Override
		public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		}
	};

	public OnMeasureDetectableLinerLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		onMeasureListener.onMeasure(widthMeasureSpec, heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void setOnMeasureListener(OnMeasureListener onMeasureListener) {
		this.onMeasureListener = onMeasureListener;
	}
}
