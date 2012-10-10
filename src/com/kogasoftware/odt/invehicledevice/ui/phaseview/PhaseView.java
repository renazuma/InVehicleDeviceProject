package com.kogasoftware.odt.invehicledevice.ui.phaseview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;

public class PhaseView extends FrameLayout implements
		EventDispatcher.OnEnterPhaseListener {
	protected InVehicleDeviceService service;

	public PhaseView(Context context, InVehicleDeviceService service) {
		super(context);
		this.service = service;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		service.getEventDispatcher().addOnEnterPhaseListener(this);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		service.getEventDispatcher().removeOnEnterPhaseListener(this);
	}

	@Override
	public void onEnterDrivePhase() {
		setVisibility(View.GONE);
	}

	@Override
	public void onEnterFinishPhase() {
		setVisibility(View.GONE);
	}

	@Override
	public void onEnterPlatformPhase() {
		setVisibility(View.GONE);
	}

	protected void setContentView(int resourceId) {
		LayoutInflater layoutInflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		addView(layoutInflater.inflate(resourceId, null),
				new PhaseView.LayoutParams(PhaseView.LayoutParams.FILL_PARENT,
						PhaseView.LayoutParams.FILL_PARENT));

		TypedArray typedArray = getContext().obtainStyledAttributes(
				new int[] { android.R.attr.background });
		int backgroundColor = typedArray.getColor(0, Color.WHITE);
		setBackgroundColor(backgroundColor);
	}

}
