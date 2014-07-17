package com.kogasoftware.odt.invehicledevice.utils;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Handler;

import com.kogasoftware.odt.invehicledevice.R;

public class FragmentUtils {
	private static FragmentTransaction setCustomAnimations(
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
						fragment.getFragmentManager().beginTransaction())
						.remove(fragment).commitAllowingStateLoss();
			}
		});
	}

	public static void showModalFragment(FragmentManager fragmentManager,
			Fragment fragment, String tag) {
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		setCustomAnimations(fragmentTransaction);
		fragmentTransaction.add(R.id.modal_fragment_container, fragment, tag);
		fragmentTransaction.commitAllowingStateLoss();
	}

	public static void showModalFragment(FragmentManager fragmentManager,
			Fragment fragment) {
		showModalFragment(fragmentManager, fragment, null);
	}
}
