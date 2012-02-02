package com.kogasoftware.odt.invehicledevice;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;

import com.kogasoftware.odt.invehicledevice.empty.EmptyViewOnClickListener;

public class OverlayCloseButton extends Button {
	static class DefaultOnClickListener implements OnClickListener {
		private final Integer MAX_DEPTH = 100;
		private OnClickListener userOnClickListener = new EmptyViewOnClickListener();

		public void setOnClickListener(OnClickListener userOnClickListener) {
			this.userOnClickListener = userOnClickListener;
		}

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
	}

	private final DefaultOnClickListener defaultOnClickListener = new DefaultOnClickListener();

	@Override
	public void setOnClickListener(OnClickListener userOnClickListener) {
		defaultOnClickListener.setOnClickListener(userOnClickListener);
	}

	public OverlayCloseButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		super.setOnClickListener(defaultOnClickListener);
	}
}
