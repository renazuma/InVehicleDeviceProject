package com.kogasoftware.odt.invehicledevice.ui.modalview;

import java.util.concurrent.atomic.AtomicReference;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.CommonLogicLoadCompleteEvent;

public class ModalView extends FrameLayout implements AnimationListener {
	private enum AnimationTarget {
		NONE, SHOW, HIDE
	}

	private final AtomicReference<AnimationTarget> animationTarget = new AtomicReference<AnimationTarget>(
			AnimationTarget.NONE);
	private final Animation showAnimation;
	private final Animation hideAnimation;

	private CommonLogic commonLogic = new CommonLogic();

	public ModalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setVisibility(View.GONE);
		showAnimation = AnimationUtils.loadAnimation(getContext(),
				R.anim.show_modal_view);
		showAnimation.setAnimationListener(this);
		hideAnimation = AnimationUtils.loadAnimation(getContext(),
				R.anim.hide_modal_view);
		hideAnimation.setAnimationListener(this);
	}

	protected CommonLogic getCommonLogic() {
		return commonLogic;
	}

	public void hide() {
		if (getVisibility() != VISIBLE) {
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

	@Subscribe
	public void setCommonLogic(CommonLogicLoadCompleteEvent event) {
		this.commonLogic = event.commonLogic;
	}

	/**
	 * 表示時にパラメーターを渡す必要がある場合、直接呼ばれると不都合がある場合もあるため protectedとしサブクラスでオーバーライドさせる
	 */
	protected void show() {
		if (getVisibility() == VISIBLE) {
			return;
		}
		if (animationTarget.getAndSet(AnimationTarget.SHOW) == AnimationTarget.NONE) {
			startAnimation(showAnimation);
		}
	}
}
