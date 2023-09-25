package com.kogasoftware.odt.invehicledevice.infra.api

import android.util.Log
import com.kogasoftware.odt.invehicledevice.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException

class MapApi(val userId: String, val password: String, val serviceId: String) {
    val DEVICE_FLAG = "1"

    @Throws(InterruptedException::class, IOException::class)
    fun imageUrl(endpointHost: String,
                 width: Int,
                 height: Int,
                 zoom: Int,
                 vehicleLatitude: String,
                 vehicleLongitude: String,
                 platformLatitude: String,
                 platformLongitude: String): String {

        val (aid: String, kid: String, zisLmtinf) = getAuthInfo()
        val userFigure: String = userFigureRequest(vehicleLatitude, vehicleLongitude, platformLatitude, platformLongitude)

        val url = endpointHost + "/api/zips/general/map" +
                "?width=$width" +
                "&height=$height" +
                "&center=$vehicleLongitude%2C$vehicleLatitude" +
                "&zoom=$zoom" +
                "&zis_authtype=aid" +
                "&zis_lmtinf=$zisLmtinf" +
                "&zis_zips_authkey=$kid" +
                "&zis_aid=$aid" +
                "&user_figure=$userFigure"

        Log.i(TAG, "url: $url")

        return url
    }

    private fun userFigureRequest(
            vehicleLatitude: String,
            vehicleLongitude: String,
            platformLatitude: String,
            platformLongitude: String): String {

        val platformIconNo = 41109
        val vehicleIconNo = 42301

        val userFigureMap = mapOf(
            "type" to "FeatureCollection",
            "features" to listOf(
                mapOf(
                    "type" to "Feature",
                    "geometry" to mapOf(
                        "type" to "Point",
                        "coordinates" to listOf(platformLongitude.toDouble(), platformLatitude.toDouble())),
                    "properties" to mapOf("icon" to platformIconNo.toString())
                ),
                mapOf(
                    "type" to "Feature",
                    "geometry" to mapOf(
                        "type" to "Point",
                        "coordinates" to listOf(vehicleLongitude.toDouble(), vehicleLatitude.toDouble())),
                    "properties" to mapOf("icon" to vehicleIconNo.toString())
                )
            )
        )

        return JSONObject(userFigureMap).toString()
    }

    private fun getAuthInfo(): Triple<String, String, String> {
        val mapApi = mapApiInterface()

        val loginInfo = runBlocking {
            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                mapApi.login(userId, password, serviceId, DEVICE_FLAG).result
            }
        }

        val aid: String = loginInfo!!["aid"].toString()
        Log.i(TAG, "aid: $aid")

        runBlocking {
            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                mapApi.logout(aid).status
            }
        }

          val kid: String = loginInfo!!["kid"].toString()
          Log.i(TAG, "kid: $kid")

          val items: Map<*, *> = loginInfo!!["items"] as Map<*, *>
          val funcList = items["func"] as List<Map<*, *>>
          val mapFunc = funcList.filter { func ->
              func["id"] == "0007" && func["subid"] == "0001"
          }.first()

          val areaCode = mapFunc["areaCode"]
          val funcInfo = mapFunc["funcInfo"]

          val zisLmtinf = "$areaCode,$funcInfo"

          Log.i(TAG, "areaCode: $areaCode")
          Log.i(TAG, "funcInfo: $funcInfo")
          Log.i(TAG, "zisLmtinf: $zisLmtinf")

        return Triple(aid, kid, zisLmtinf)
    }

    private fun mapApiInterface(): MapApiInterface {
        val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://test-api.zip-site.com/api/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        return retrofit.create(MapApiInterface::class.java)
    }

    companion object {
        val TAG = MapApi::class.java.simpleName
    }
}
