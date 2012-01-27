package com.kogasoftware.odt.invehicledevice;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;

import com.kogasoftware.odt.invehicledevice.empty.EmptyViewOnClickListener;

public class OverlayCloseButton extends Button {
	OnClickListener userOnClickListener = new EmptyViewOnClickListener();

	final OnClickListener defaultOnClickListener = new OnClickListener() {
		final Integer MAX_DEPTH = 100;

		@Override
		public void onClick(View view) {
			ViewParent parent = view.getParent();
			for (Integer depth = 0; parent != null && depth < MAX_DEPTH; parent = parent
					.getParent(), ++depth) {
				if (parent instanceof OverlayLinearLayout) {
					((OverlayLinearLayout) parent).hide();
					break;
				}
			}
			userOnClickListener.onClick(view);
		}
	};

	@Override
	public void setOnClickListener(OnClickListener onClickListener) {
		userOnClickListener = onClickListener;
		super.setOnClickListener(defaultOnClickListener);
	}

	public OverlayCloseButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		super.setOnClickListener(defaultOnClickListener);
	}
}
