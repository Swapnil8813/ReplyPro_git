package com.digitechno.replypro.api

import com.digitechno.replypro.settings.SettingsRequest
import com.digitechno.replypro.settings.SettingsResponse
import com.digitechno.replypro.settings.UpdateResponse
import com.digitechno.replypro.sync.CallSyncRequest
import com.digitechno.replypro.sync.CallSyncResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    //--------------------------------------------------
    // Activation
    //--------------------------------------------------

    @POST("check-activation")
    fun checkActivation(
        @Body request: ActivationRequest
    ): Call<ActivationResponse>

    //--------------------------------------------------
    // Call Sync
    //--------------------------------------------------

    @POST("call/sync")
    fun syncCall(
        @Body request: CallSyncRequest
    ): Call<CallSyncResponse>

    //--------------------------------------------------
    // Settings
    //--------------------------------------------------

    @GET("settings/{mobile}")
    fun getSettings(
        @Path("mobile") mobile: String
    ): Call<SettingsResponse>

    @POST("settings/update")
    fun updateSettings(
        @Body request: SettingsRequest
    ): Call<UpdateResponse>

    @Multipart
    @POST("settings/upload")
    fun uploadAttachment(

        @Part attachment: MultipartBody.Part,

        @Part("mobile_number") mobile: RequestBody

    ): Call<UpdateResponse>

}