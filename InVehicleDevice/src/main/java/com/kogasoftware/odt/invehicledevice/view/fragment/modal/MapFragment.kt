package com.kogasoftware.odt.invehicledevice.view.fragment.modal

import android.app.Fragment
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.FragmentUtils.hideModal
import android.os.Bundle
import android.view.*
import android.widget.*
import com.kogasoftware.odt.invehicledevice.R

/**
 * 地図表示画面
 */
class MapFragment : Fragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        view!!.findViewById<Button>(R.id.quit_map_button).setOnClickListener { v: View? ->
            hideModal(this@MapFragment)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.map_fragment, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(): MapFragment {
            return MapFragment()
        }
    }
}
