package com.kogasoftware.odt.invehicledevice;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.kogasoftware.odt.invehicledevice.empty.EmptyViewOnClickListener;

public class AllOverlaysCloseButton extends Button {
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

	OnClickListener userOnClickListener = new EmptyViewOnClickListener();

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