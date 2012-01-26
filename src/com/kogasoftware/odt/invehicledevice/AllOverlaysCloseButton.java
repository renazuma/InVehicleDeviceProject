package com.kogasoftware.odt.invehicledevice;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

public class AllOverlaysCloseButton extends Button {
	OnClickListener userOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
		}
	};

	final OnClickListener defaultOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			for (OverlayLinearLayout l : OverlayLinearLayout
					.getAttachedInstances()) {
				l.hide();
			}
			userOnClickListener.onClick(view);
		}
	};

	@Override
	public void setOnClickListener(OnClickListener onClickListener) {
		userOnClickListener = onClickListener;
		super.setOnClickListener(defaultOnClickListener);
	}

	public AllOverlaysCloseButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		super.setOnClickListener(defaultOnClickListener);
	}
}