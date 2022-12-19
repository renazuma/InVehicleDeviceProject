package com.kogasoftware.odt.invehicledevice.infra.api

import android.util.Log
import kotlin.Throws
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import org.json.JSONObject
import java.io.IOException

class MapApi(val userId: String, val password: String, val serviceId: String) {
    val DEVICE_FLAG = "1"

    @Throws(InterruptedException::class, IOException::class)
    fun imageUrl(width: Int, height: Int, zoom: Int, latitude: String, longitude: String): String {

        val (aid: String, kid: String, zisLmtinf) = getAuthInfo()
        val userFigure: String = userFigureRequest(latitude, longitude)

        val url = "https://test-api.zip-site.com/api/zips/general/map" +
                "?width=$width" +
                "&height=$height" +
                "&center=$longitude%2C$latitude" +
                "&zoom=$zoom" +
                "&zis_authtype=aid" +
                "&zis_lmtinf=$zisLmtinf" +
                "&zis_zips_authkey=$kid" +
                "&zis_aid=$aid" +
                "&user_figure=$userFigure"

        Log.i(TAG, "url: $url")

        return url
    }

    private fun userFigureRequest(latitude: String, longitude: String): String {
        val platformIconNo = 41109
        val vehicleIconNo = 42301

        val userFigureMap = mapOf(
            "type" to "FeatureCollection",
            "features" to listOf(
                mapOf(
                    "type" to "Feature",
                    "geometry" to mapOf(
                        "type" to "Point",
                        "coordinates" to listOf(139.797995, 35.715386)),
                    "properties" to mapOf("icon" to platformIconNo.toString())
                ),
                mapOf(
                    "type" to "Feature",
                    "geometry" to mapOf(
                        "type" to "Point",
                        "coordinates" to listOf(longitude.toDouble(), latitude.toDouble())),
                    "properties" to mapOf("icon" to vehicleIconNo.toString())
                )
            )
        )

        return JSONObject(userFigureMap).toString()
    }

    private fun getAuthInfo(): Triple<String, String, String> {

        val mapApi = mapApiInterface()

        // HACK: 同期処理をするとAndroidのメイン処理自体にその間待ちが入る（多分）ので、出来ればAPIの実行は非同期にしたい
        val authInfo = mapApi.login(userId, password, serviceId, DEVICE_FLAG).execute()
        val aid: String = authInfo.body()!!.result["aid"].toString()
        val kid: String = authInfo.body()!!.result["kid"].toString()
        Log.i(TAG, "aid: $aid")
        Log.i(TAG, "kid: $kid")

        val items: Map<*, *> = authInfo.body()!!.result["items"] as Map<*, *>
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

        logout(mapApi, aid)

        return Triple(aid, kid, zisLmtinf)
    }

    private fun logout(mapApi: MapApiInterface, aid: String) {
        val logout = mapApi.logout(aid)
        val logoutInfo = logout.execute()
        Log.i(TAG, "logout: " + logoutInfo.body()!!.status["text"])
    }

    private fun mapApiInterface(): MapApiInterface {
        val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://test-api.zip-site.com/api/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        return retrofit.create(MapApiInterface::class.java)
    }

    companion object {
        val TAG = MapApi::class.java.simpleName
    }
}
