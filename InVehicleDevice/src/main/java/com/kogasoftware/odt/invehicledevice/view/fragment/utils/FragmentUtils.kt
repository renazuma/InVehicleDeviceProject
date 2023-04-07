package com.kogasoftware.odt.invehicledevice.view.fragment.utils

import android.app.Fragment
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.content.Context
import android.graphics.Insets
import android.graphics.Point
import android.os.Build
import android.os.Handler
import android.view.Display
import android.view.WindowInsets
import android.view.WindowManager
import android.view.WindowMetrics
import com.kogasoftware.odt.invehicledevice.R
import kotlin.jvm.JvmOverloads
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.PassengerRecord

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

    @JvmStatic
    fun deviceSize(fragment: Fragment): Pair<Int, Int> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics = fragment.activity.windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())

            Pair(windowMetrics.bounds.width(), windowMetrics.bounds.height() - insets.bottom)
        } else {
            val windowManager: WindowManager = fragment.activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val disp: Display = windowManager.defaultDisplay
            val realSize = Point();
            disp.getRealSize(realSize)

            Pair(realSize.x, realSize.y)
        }
    }


    enum class PassengerStatus {
        SELECTED_GET_OFF, GET_OFF, SELECTED_GET_ON, GET_ON, NONE
    }

    @JvmStatic
    fun getPassengerStatus(passengerRecord: PassengerRecord, operationSchedule: OperationSchedule): PassengerStatus? {
        if (operationSchedule.id == passengerRecord.arrivalScheduleId) {
            return if (passengerRecord.getOffTime != null) {
                PassengerStatus.SELECTED_GET_OFF
            } else {
                PassengerStatus.GET_OFF
            }
        } else if (operationSchedule.id == passengerRecord.departureScheduleId) {
            return if (passengerRecord.getOnTime != null) {
                PassengerStatus.SELECTED_GET_ON
            } else {
                PassengerStatus.GET_ON
            }
        }
        return PassengerStatus.NONE
    }

}
