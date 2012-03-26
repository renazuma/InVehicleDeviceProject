package com.kogasoftware.odt.invehicledevice.modal;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class HideModalButton extends HandleModalButton {
	public HideModalButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HideModalButton(Context context) {
		super(context);
	}

	@Override
	protected void onHandleModalButtonClick(View view, Modal modal) {
		modal.hide();
	}

}
