package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher.OnUpdateOperationListener;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Operation;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Operation.Phase;
import com.kogasoftware.odt.invehicledevice.ui.activity.EmptyActivity;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class ApplicationFragment<S extends Serializable> extends Fragment {
	/**
	 * インスタンス状態を使わずここへ変数に保存する。onSaveInstanceStateで自動保存され、onCreateで自動読み出しされる。
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
	protected String getSavedInstanceStateKey() {
		return "SavedInstanceStateKey:" + ApplicationFragment.class.getName()
				+ "/" + getClass().getName();
	}

	private static class PrivateState implements Serializable {
		private static final long serialVersionUID = 2820665608614198512L;

		/**
		 * PrivateStateをBundleに保存する際のキーを取得
		 */
		private static String getSavedInstanceStateKey() {
			return "SavedInstanceStateKey:" + PrivateState.class.getName()
					+ "/" + PrivateState.class.getName();
		}

		/**
		 * OnUpdateOperation時にFragmentを閉じるかどうか
		 */
		private Boolean removeOnUpdateOperation = false;
	}

	private PrivateState privateState = new PrivateState();
	private static final AtomicLong OBJECT_COUNTER = new AtomicLong(0);
	private final Long objectCount = OBJECT_COUNTER.incrementAndGet(); // 診断用。onCreateからonDestroyの間は同じ値が保持されるはず。

	/**
	 * OnUpdateOperation時にFragmentを閉じる
	 */
	private final OnUpdateOperationListener removeOnUpdateOperationListener = new OnUpdateOperationListener() {
		@Override
		public void onUpdateOperation(Operation operation) {
			if (!isRemoving()) {
				hide();
			}
		}
	};

	protected void setRemoveOnUpdateOperation(Boolean removeOnUpdateOperation) {
		this.privateState.removeOnUpdateOperation = removeOnUpdateOperation;
	}

	private void restoreState(Bundle savedInstanceState) {
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

	private void restorePrivateState(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			return;
		}
		Object object = savedInstanceState.getSerializable(PrivateState
				.getSavedInstanceStateKey());
		if (object instanceof PrivateState) {
			privateState = (PrivateState) object;
			return;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(getClass().getSimpleName(), "onCreate(" +
				Objects.firstNonNull(savedInstanceState, "") + ") " + objectCount);
		restoreState(savedInstanceState);
		restorePrivateState(savedInstanceState);
		if (privateState.removeOnUpdateOperation) {
			getService().getEventDispatcher().addOnUpdateOperationListener(
					removeOnUpdateOperationListener);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(getSavedInstanceStateKey(), getState());
		outState.putSerializable(PrivateState.getSavedInstanceStateKey(), privateState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i(getClass().getSimpleName(), "onActivityCreated() " + objectCount);
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
		} else if (activity instanceof EmptyActivity) {
			return ((EmptyActivity) activity).getService();
		} else {
			throw new ClassCastException("!(" + activity
					+ " instanceof InVehicleDeviceActivity or EmptyActivity)");
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
		for (FragmentManager fragmentManager : getOptionalFragmentManager().asSet()) {
			setCustomAnimation(fragmentManager.beginTransaction())
				.remove(this).commitAllowingStateLoss();
		}
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

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.i(getClass().getSimpleName(), "onDestroyView() " + objectCount);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(getClass().getSimpleName(), "onDestroy() " + objectCount);
		getService().getEventDispatcher().removeOnUpdateOperationListener(
				removeOnUpdateOperationListener);
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i(getClass().getSimpleName(), "onResume() " + objectCount);
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i(getClass().getSimpleName(), "onPause() " + objectCount);
	}

	public Optional<FragmentManager> getOptionalFragmentManager() {
		return Optional.fromNullable(getFragmentManager());
	}
}
