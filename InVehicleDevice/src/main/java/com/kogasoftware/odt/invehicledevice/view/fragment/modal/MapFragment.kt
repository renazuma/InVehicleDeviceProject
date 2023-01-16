package com.kogasoftware.odt.invehicledevice.view.fragment.modal

import android.app.Fragment
import android.content.Context.WINDOW_SERVICE
import android.graphics.Insets
import android.graphics.Point
import android.os.Build
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.FragmentUtils.hideModal
import android.os.Bundle
import android.view.*
import android.widget.*
import com.kogasoftware.odt.invehicledevice.R
import com.kogasoftware.odt.invehicledevice.infra.api.MapApi
import com.bumptech.glide.Glide
import java.io.IOException
import kotlin.math.roundToInt

/**
 * 地図表示画面
 */
class MapFragment : Fragment() {
    val zoom = 20
    val center = "139.797993%2C35.715472"

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val imageView = view!!.findViewById<ImageView>(R.id.map_image)

        val (width, height) = imageSizePair()

        try {

            var mapUrl: String = MapApi().imageUrl(width, height, zoom, center)
            Glide.with(view!!).load(mapUrl).into(imageView)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        view!!.findViewById<Button>(R.id.quit_map_button).setOnClickListener { v: View? ->
            hideModal(this@MapFragment)
        }
    }

    private fun imageSizePair(): Pair<Int, Int> {
        val deviceRatio: Float =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowMetrics: WindowMetrics = activity.windowManager.currentWindowMetrics
                val insets: Insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())

                val width = windowMetrics.bounds.width()
                val height = windowMetrics.bounds.height() - insets.bottom
                height.toFloat() / width
            } else {
                val windowManager: WindowManager = activity.getSystemService(WINDOW_SERVICE) as WindowManager
                val disp: Display = windowManager.defaultDisplay
                val realSize = Point();
                disp.getRealSize(realSize)

                val width = realSize.x
                val height = realSize.y
                height.toFloat() / width
            }

        val width = 640
        // height は 下部ボタンの分だけ短くしているが、ボタン部分のサイズは dp 指定で、heightはピクセルなので、ボタンと正確に対応はしていない。
        val buttonHeight = 30
        val height = (width * deviceRatio).roundToInt() - buttonHeight

        return Pair(width, height)
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
