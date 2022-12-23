package com.kogasoftware.odt.invehicledevice.view.fragment.utils

import android.app.Fragment
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.os.Handler
import com.kogasoftware.odt.invehicledevice.R
import kotlin.jvm.JvmOverloads

/**
 * フラグメント用の共通処理
 */
object FragmentUtils {
    private fun setCustomAnimations(
            fragmentTransaction: FragmentTransaction): FragmentTransaction {
        fragmentTransaction.setCustomAnimations(R.animator.show_full_fragment,
                R.animator.hide_full_fragment, R.animator.show_full_fragment,
                R.animator.hide_full_fragment)
        return fragmentTransaction
    }

    @JvmStatic
    @JvmOverloads
    fun hideModal(fragment: Fragment, handler: Handler = Handler()) {
        handler.post {
            if (!fragment.isAdded) {
                return@post
            }
            setCustomAnimations(
                    fragment.fragmentManager.beginTransaction())
                    .remove(fragment).commitAllowingStateLoss()
        }
    }

    @JvmStatic
    @JvmOverloads
    fun showModal(fragmentManager: FragmentManager, fragment: Fragment?, tag: String? = null) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        setCustomAnimations(fragmentTransaction)
        fragmentTransaction.add(R.id.modal_fragment_container, fragment, tag)
        fragmentTransaction.commitAllowingStateLoss()
    }
}
