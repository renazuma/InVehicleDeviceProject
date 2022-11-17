package com.kogasoftware.odt.invehicledevice.infra.api

import retrofit2.http.GET
import com.kogasoftware.odt.invehicledevice.infra.api.json.LoginInfo
import com.kogasoftware.odt.invehicledevice.infra.api.json.LogoutInfo
import retrofit2.Call
import retrofit2.http.Query

interface MapApiInterface {
    @GET("auth/login")
    fun login(
        @Query("user_id") userId: String,
        @Query("password") password: String,
        @Query("service_id") serviceId: String,
        @Query("device_flag") deviceFlag: String
    ): Call<LoginInfo>

    @GET("auth/logout")
    fun logout(@Query("aid") aid: String): Call<LogoutInfo>
}
