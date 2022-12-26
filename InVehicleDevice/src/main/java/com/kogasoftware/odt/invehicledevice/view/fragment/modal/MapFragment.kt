package com.kogasoftware.odt.invehicledevice.view.fragment.modal

import android.app.Fragment
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Insets
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.FragmentUtils.hideModal
import android.os.Bundle
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.core.util.component1
import androidx.core.util.component2
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import androidx.swiperefreshlayout.widget.CircularProgressDrawable.*
import com.kogasoftware.odt.invehicledevice.R
import com.kogasoftware.odt.invehicledevice.infra.api.MapApi
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.ServiceUnitStatusLog
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.ZenrinMapsAccount
import java.io.IOException
import kotlin.math.roundToInt

/**
 * 地図表示画面
 */
class MapFragment : Fragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        drawMap()

        view!!.findViewById<View>(R.id.reload_map_button).setOnClickListener { v: View? ->
            drawMap()
        }

        view!!.findViewById<Button>(R.id.quit_map_button).setOnClickListener { v: View? ->
            hideModal(this@MapFragment)
        }
    }

    private fun drawMap() {
        val zoom = 20

        val imageView = view!!.findViewById<ImageView>(R.id.map_image)

        var mapUrl = ""

        try {
            val (width, height) = imageSizePair()
            val (userId, password, serviceId) = ZenrinMapsAccount.getAccountData(context.contentResolver)
            val (vehicleLatitude, vehicleLongitude) = ServiceUnitStatusLog.getLatestLocation(context.contentResolver)
            val (platformLatitude, platformLongitude) = OperationSchedule.getNextPlatformLocation(context.contentResolver)

            mapUrl = MapApi(userId, password, serviceId).imageUrl(width, height, zoom, vehicleLatitude, vehicleLongitude, platformLatitude, platformLongitude)
        } catch (e: Exception) {
            e.printStackTrace()
            imageView.layoutParams.width = WRAP_CONTENT
            imageView.layoutParams.height = WRAP_CONTENT
            imageView.setImageDrawable(requestErrorDrawable())
        }

        try {
            Glide.with(view!!)
                .load(mapUrl)
                .placeholder(circularProgressDrawable())
                .error(requestErrorDrawable())
                .listener(requestListener(imageView))
                .into(imageView)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        view!!.findViewById<Button>(R.id.quit_map_button).setOnClickListener { v: View? ->
            hideModal(this@MapFragment)
        }
    }

    private fun circularProgressDrawable(): CircularProgressDrawable {
        val drawable = CircularProgressDrawable(context);
        drawable.setStyle(LARGE)
        drawable.centerRadius = 30f
        drawable.strokeWidth = 5f
        drawable.start()
        return drawable
    }

    private fun requestErrorDrawable(): Drawable {
        val deviceHeight =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowMetrics: WindowMetrics = activity.windowManager.currentWindowMetrics
                val insets: Insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())

                windowMetrics.bounds.height() - insets.bottom
            } else {
                val windowManager: WindowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val disp: Display = windowManager.defaultDisplay
                val realSize = Point();
                disp.getRealSize(realSize)

                realSize.y
            }

        val imageHeight = deviceHeight / 8
        // HACK: 動的にする程の事でもないので、オリジナル画像の縦横比を直接指定して横のサイズを作っている。
        val imageWidth = (imageHeight * 0.844).toInt()

        val original = resources.getDrawable(R.drawable.request_error)
        return BitmapDrawable(
            resources,
            Bitmap.createScaledBitmap(
                (original as BitmapDrawable).bitmap,
                imageWidth,
                imageHeight,
                true
            )
        )
    }

    private fun requestListener(imageView: ImageView) = object : RequestListener<Drawable> {
        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
            imageView.layoutParams.width = WRAP_CONTENT
            imageView.layoutParams.height = WRAP_CONTENT
            return false
        }

        override fun onResourceReady(e: Drawable?, model: Any?, target: Target<Drawable>?, isFirstResource: DataSource?, p4: Boolean): Boolean {
            imageView.layoutParams.width = MATCH_PARENT
            imageView.layoutParams.height = MATCH_PARENT
            return false
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
                val windowManager: WindowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
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
