package com.kogasoftware.odt.invehicledevice.modal;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;

import com.kogasoftware.odt.invehicledevice.empty.EmptyViewOnClickListener;

public class HandleModalButton extends Button {
	class DefaultOnClickListener implements OnClickListener {
		private final Integer MAX_DEPTH = 100;
		private OnClickListener userOnClickListener = new EmptyViewOnClickListener();

		public DefaultOnClickListener() {
		}

		public void setUserOnClickListener(OnClickListener userOnClickListener) {
			this.userOnClickListener = userOnClickListener;
		}

		@Override
		public void onClick(View view) {
			ViewParent parent = view.getParent();
			for (Integer depth = 0; parent != null && depth < MAX_DEPTH; parent = parent
					.getParent(), ++depth) {
				if (parent instanceof Modal) {
					HandleModalButton.this.onHandleModalButtonClick(view, (Modal) parent);
					break;
				}
			}
			userOnClickListener.onClick(view);
		}
	}

	private final DefaultOnClickListener defaultOnClickListener = new DefaultOnClickListener();

	protected void onHandleModalButtonClick(View view, Modal modal) {
	}

	@Override
	public void setOnClickListener(OnClickListener userOnClickListener) {
		defaultOnClickListener.setUserOnClickListener(userOnClickListener);
	}

	private void init() {
		super.setOnClickListener(defaultOnClickListener);
	}

	public HandleModalButton(Context context) {
		super(context);
		init();
	}

	public HandleModalButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
}
