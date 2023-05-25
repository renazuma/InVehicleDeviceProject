package com.kogasoftware.odt.invehicledevice.view.fragment.modal

import android.app.Fragment
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.FragmentUtils.deviceSize
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
        val imageView = view!!.findViewById<ImageView>(R.id.map_image)

        try {
            Glide.with(view!!)
                .load(mapUrl())
                .placeholder(circularProgressDrawable())
                .error(requestErrorDrawable())
                .listener(requestListener(imageView))
                .into(imageView)
        } catch (e: Exception) {
            e.printStackTrace()
            imageView.layoutParams.width = WRAP_CONTENT
            imageView.layoutParams.height = WRAP_CONTENT
            imageView.setImageDrawable(requestErrorDrawable())
        }

        view!!.findViewById<Button>(R.id.quit_map_button).setOnClickListener { v: View? ->
            hideModal(this@MapFragment)
        }
    }

    private fun mapUrl(): String {
        val zoom = 20

        val (width, height) = imageSizePair()
        val (userId, password, serviceId) = ZenrinMapsAccount.getAccountData(context.contentResolver)
        val (vehicleLatitude, vehicleLongitude) = ServiceUnitStatusLog.getLatestLocation(context.contentResolver)

        val operationSchedule = arguments.getSerializable(ARG_OPERATION_SCHEDULE) as OperationSchedule

        val platformLatitude = operationSchedule.latitude.toString()
        val platformLongitude = operationSchedule.longitude.toString()

        return MapApi(userId, password, serviceId)
            .imageUrl(width, height, zoom, vehicleLatitude, vehicleLongitude, platformLatitude, platformLongitude)
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
        val (_, deviceHeight) = deviceSize(this)

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
        val (deviceWidth, deviceHeight) = deviceSize(this)
        val deviceRatio = deviceHeight.toFloat() / deviceWidth

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
        private const val ARG_OPERATION_SCHEDULE = "operation_schedule"

        @JvmStatic
        fun newInstance(operationSchedule: OperationSchedule): MapFragment {
            val mapFragment = MapFragment()
            val args = Bundle()
            args.putSerializable(ARG_OPERATION_SCHEDULE, operationSchedule)
            mapFragment.arguments = args
            return mapFragment
        }
    }
}
