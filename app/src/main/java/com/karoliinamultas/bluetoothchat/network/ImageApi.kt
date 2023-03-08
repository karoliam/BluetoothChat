
package com.karoliinamultas.bluetoothchat.network


import com.google.gson.annotations.SerializedName
import com.karoliinamultas.bluetoothchat.network.responce.Image
import com.karoliinamultas.bluetoothchat.network.responce.Success
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

object ImageApi {
    const val URL = "https://freeimage.host/api/"


    object Model{
        data class Json4Kotlin_Base (

            @SerializedName("status_code") val status_code : Int,
            @SerializedName("success") val success : Success,
            @SerializedName("image") val image : Image,
            @SerializedName("status_txt") val status_txt : String
        )
    }
    interface Service {
        @FormUrlEncoded
        @POST("1/upload/?")
        suspend fun postData(
            @Field("key") param1: String,
            @Field("source") param2: String,
            @Field("format") param3: String
        ): Model.Json4Kotlin_Base
    }


    private val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: Service by lazy {
        retrofit.create(Service::class.java)
    }
}