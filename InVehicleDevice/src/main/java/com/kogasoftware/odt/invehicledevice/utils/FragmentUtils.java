package com.kogasoftware.odt.invehicledevice.utils;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Handler;

import com.kogasoftware.odt.invehicledevice.R;

public class FragmentUtils {
	public static FragmentTransaction setCustomAnimations(
			FragmentTransaction fragmentTransaction) {
		fragmentTransaction.setCustomAnimations(R.animator.show_full_fragment,
				R.animator.hide_full_fragment, R.animator.show_full_fragment,
				R.animator.hide_full_fragment);
		return fragmentTransaction;
	}

	public static void hide(final Fragment fragment) {
		(new Handler()).post(new Runnable() {
			@Override
			public void run() {
				if (!fragment.isAdded()) {
					return;
				}
				setCustomAnimations(
						fragment.getActivity().getFragmentManager()
								.beginTransaction()).remove(fragment)
						.commitAllowingStateLoss();
			}
		});
	}
}
