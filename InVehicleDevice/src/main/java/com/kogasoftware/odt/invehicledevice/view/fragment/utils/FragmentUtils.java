package com.kogasoftware.odt.invehicledevice.view.fragment.utils;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Handler;

import com.kogasoftware.odt.invehicledevice.R;

/**
 * フラグメント用の共通処理
 */
public class FragmentUtils {
    private static FragmentTransaction setCustomAnimations(
            FragmentTransaction fragmentTransaction) {
        fragmentTransaction.setCustomAnimations(R.animator.show_full_fragment,
                R.animator.hide_full_fragment, R.animator.show_full_fragment,
                R.animator.hide_full_fragment);
        return fragmentTransaction;
    }

    public static void hideModal(final Fragment fragment, Handler handler) {
        handler.post(() -> {
            if (!fragment.isAdded()) {
                return;
            }
            setCustomAnimations(
                    fragment.getFragmentManager().beginTransaction())
                    .remove(fragment).commitAllowingStateLoss();
        });
    }

    public static void hideModal(Fragment fragment) {
        hideModal(fragment, new Handler());
    }

    public static void showModal(FragmentManager fragmentManager, Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        setCustomAnimations(fragmentTransaction);
        fragmentTransaction.add(R.id.modal_fragment_container, fragment, tag);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public static void showModal(FragmentManager fragmentManager, Fragment fragment) {
        showModal(fragmentManager, fragment, null);
    }
}
