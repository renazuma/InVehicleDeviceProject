package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.Serializable;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Phase;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class ApplicationFragment<S extends Serializable> extends Fragment {
	/**
	 * インスタンス状態をここへ変数に保存する。onSaveInstanceStateで自動保存され、onCreateで自動読み出しされる。
	 */
	private S state;

	/**
	 * インスタンスの状態を取得。onSaveInstanceStateで保存されたデータがない場合、setArgumentsで保存されたデータを使う。
	 */
	protected S getState() {
		return state;
	}

	/**
	 * インスタンスの状態を設定
	 */
	protected void setState(S state) {
		this.state = state;
	}

	/**
	 * stateをBundleに保存する際のキーを取得
	 */
	private String getSavedInstanceStateKey() {
		return "SavedInstanceStateKey:" + ApplicationFragment.class.getName()
				+ "/" + getClass().getName();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// stateメンバを復活させる
		String key = getSavedInstanceStateKey();
		if (savedInstanceState != null) {
			Object rawState = savedInstanceState.getSerializable(key);
			if (rawState != null) {
				@SuppressWarnings("unchecked")
				S castState = (S) rawState;
				setState(castState);
				return;
			}
		}
		Bundle arguments = getArguments();
		if (arguments == null) {
			throw new IllegalStateException("\"" + this
					+ "\" doesn't have arguments");
		}
		@SuppressWarnings("unchecked")
		S castState = (S) arguments.getSerializable(key);
		setState(castState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// stateメンバを保存
		outState.putSerializable(getSavedInstanceStateKey(), getState());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = getView();
		if (view != null) {
			view.setClickable(true);
		}
	}

	/**
	 * 指定した引数のインスタンスを生成
	 */
	protected static <T extends ApplicationFragment<U>, U extends Serializable> T newInstance(
			T fragment, U state) {
		return newInstance(fragment, new Bundle(), state);
	}

	/**
	 * 指定した引数のインスタンスを生成
	 */
	protected static <T extends ApplicationFragment<U>, U extends Serializable> T newInstance(
			T fragment, Bundle args, U state) {
		args.putSerializable(fragment.getSavedInstanceStateKey(), state);
		fragment.setArguments(args);
		return fragment;
	}

	protected InVehicleDeviceService getService() {
		Activity activity = getActivity();
		if (activity instanceof InVehicleDeviceActivity) {
			Optional<InVehicleDeviceService> service = ((InVehicleDeviceActivity) activity)
					.getService();
			if (service.isPresent()) {
				return service.get();
			}
			throw new IllegalStateException(
					"getActivity().getService() value is absent");
		} else {
			throw new ClassCastException("!(" + activity
					+ " instanceof InVehicleDeviceActivity)");
		}
	}

	protected View onCreateViewHelper(LayoutInflater inflater,
			ViewGroup container, int layoutResourceId, int closeButtonResourceId) {
		View view = inflater.inflate(layoutResourceId, container, false);
		Button hideButton = (Button) view.findViewById(closeButtonResourceId);
		hideButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				hide();
			}
		});
		return view;
	}

	protected void hide() {
		setCustomAnimation(getFragmentManager().beginTransaction())
				.remove(this).commitAllowingStateLoss();
	}

	protected static Integer getPhaseColor(Phase phase) {
		switch (phase) {
		case DRIVE:
			return Color.rgb(0xAA, 0xFF, 0xAA);
		case FINISH:
			return Color.rgb(0xAA, 0xAA, 0xAA);
		case PLATFORM_GET_ON:
			return Color.rgb(0xAA, 0xAA, 0xFF);
		case PLATFORM_GET_OFF:
			return Color.rgb(0xAA, 0xAA, 0xFF);
		default:
			break;
		}
		return Color.WHITE;
	}

	public static FragmentTransaction setCustomAnimation(
			FragmentTransaction fragmentTransaction) {
		fragmentTransaction.setCustomAnimations(R.anim.show_modal_view,
				R.anim.hide_modal_view, R.anim.show_modal_view,
				R.anim.hide_modal_view);
		return fragmentTransaction;
	}
}
