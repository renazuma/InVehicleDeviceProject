package com.kogasoftware.odt.invehicledevice.modalview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;

import com.kogasoftware.odt.invehicledevice.empty.EmptyViewOnClickListener;

public class HideModalButton extends Button {
	static class DefaultOnClickListener implements OnClickListener {
		private OnClickListener userOnClickListener = new EmptyViewOnClickListener();

		@Override
		public void onClick(View view) {
			ViewParent parent = view.getParent();
			for (Integer depth = 0; parent != null && depth < MAX_VIEW_DEPTH; parent = parent
					.getParent(), ++depth) {
				if (parent instanceof ModalView) {
					((ModalView) parent).hide();
					break;
				}
			}
			userOnClickListener.onClick(view);
		}

		public void setUserOnClickListener(OnClickListener userOnClickListener) {
			this.userOnClickListener = userOnClickListener;
		}
	}

	private static final Integer MAX_VIEW_DEPTH = 100;

	private final DefaultOnClickListener defaultOnClickListener = new DefaultOnClickListener();

	public HideModalButton(Context context) {
		super(context);
		super.setOnClickListener(defaultOnClickListener);
	}

	public HideModalButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		super.setOnClickListener(defaultOnClickListener);
	}

	@Override
	public void setOnClickListener(OnClickListener userOnClickListener) {
		defaultOnClickListener.setUserOnClickListener(userOnClickListener);
	}
}
