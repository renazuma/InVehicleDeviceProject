package com.kogasoftware.odt.invehicledevice;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.kogasoftware.odt.invehicledevice.empty.EmptyViewOnClickListener;

public class AllOverlaysCloseButton extends Button {
	static class DefaultOnClickListener implements OnClickListener {
		private OnClickListener userOnClickListener = new EmptyViewOnClickListener();

		public void setOnClickListener(OnClickListener userOnClickListener) {
			this.userOnClickListener = userOnClickListener;
		}

		@Override
		public void onClick(View view) {
			for (WeakReference<OverlayLinearLayout> r : OverlayLinearLayout
					.getAttachedInstances()) {
				OverlayLinearLayout l = r.get();
				if (l == null) {
					continue;
				}
				l.hide();
			}
			userOnClickListener.onClick(view);
		}
	}

	DefaultOnClickListener defaultOnClickListener = new DefaultOnClickListener();

	@Override
	public void setOnClickListener(OnClickListener userOnClickListener) {
		defaultOnClickListener.setOnClickListener(userOnClickListener);
	}

	public AllOverlaysCloseButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		super.setOnClickListener(defaultOnClickListener);
	}
}