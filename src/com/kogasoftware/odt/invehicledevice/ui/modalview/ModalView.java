package com.kogasoftware.odt.invehicledevice.ui.modalview;

import java.util.concurrent.atomic.AtomicReference;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;

public class ModalView extends FrameLayout implements AnimationListener {
	private enum AnimationTarget {
		NONE, SHOW, HIDE
	}

	protected final AtomicReference<AnimationTarget> animationTarget = new AtomicReference<AnimationTarget>(
			AnimationTarget.NONE);
	protected final Animation showAnimation;
	protected final Animation hideAnimation;
	protected final InVehicleDeviceService service;

	public ModalView(Context context, InVehicleDeviceService service) {
		super(context);
		this.service = service;
		showAnimation = AnimationUtils.loadAnimation(getContext(),
				R.anim.show_modal_view);
		showAnimation.setAnimationListener(this);
		hideAnimation = AnimationUtils.loadAnimation(getContext(),
				R.anim.hide_modal_view);
		hideAnimation.setAnimationListener(this);
		setClickable(true);
		setVisibility(GONE);
	}

	public void hide() {
		if (getVisibility() != VISIBLE
				&& animationTarget.get() != AnimationTarget.SHOW) {
			return;
		}
		if (animationTarget.getAndSet(AnimationTarget.HIDE) == AnimationTarget.NONE) {
			startAnimation(hideAnimation);
		}
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if (animation == hideAnimation) {
			if (animationTarget.get() == AnimationTarget.SHOW) {
				startAnimation(showAnimation);
			} else {
				setVisibility(GONE);
				animationTarget.set(AnimationTarget.NONE);
			}
		} else if (animation == showAnimation) {
			if (animationTarget.get() == AnimationTarget.HIDE) {
				startAnimation(hideAnimation);
			} else {
				animationTarget.set(AnimationTarget.NONE);
			}
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	@Override
	public void onAnimationStart(Animation animation) {
		if (animation == showAnimation || animation == hideAnimation) {
			setVisibility(VISIBLE);
		}
	}

	/**
	 * 指定したリソースに、ModalViewを閉じるOnClickListenerをセット
	 *
	 * @param resourceId
	 */
	protected void setCloseOnClick(Integer resourceId) {
		findViewById(resourceId).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				hide();
			}
		});
	}

	protected void setContentView(int resourceId) {
		LayoutInflater layoutInflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.addView(layoutInflater.inflate(resourceId, null),
				new ModalView.LayoutParams(ModalView.LayoutParams.FILL_PARENT,
						ModalView.LayoutParams.FILL_PARENT));

		TypedArray typedArray = getContext().obtainStyledAttributes(
				new int[] { android.R.attr.background });
		int backgroundColor = typedArray.getColor(0, Color.WHITE);
		setBackgroundColor(backgroundColor);
	}

	protected void show() {
		if (getVisibility() == VISIBLE
				&& animationTarget.get() != AnimationTarget.HIDE) {
			return;
		}
		if (animationTarget.getAndSet(AnimationTarget.SHOW) == AnimationTarget.NONE) {
			startAnimation(showAnimation);
			setVisibility(VISIBLE); // GONEやINVISIBLE状態だとアニメーションが始まらないことがあるため、明示的にVISIBLEとする
		}
	}
}
