package com.karoliinamultas.bluetoothchat.network


import retrofit2.http.GET
import retrofit2.http.Query
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


object RetrofitApi {
    // base URL
    private const val URL = "https://en.wikipedia.org/w/"

    // Moshi
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // Rutrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    // the model
    object Model {
        data class Result(
            @Json(name = "query") val query: Query,
            @Json(name = "json") val json: Any?,
            @Json(name = "list") val list: Any?,
            @Json(name = "srsearch") val srsearch: Any?
        )
        data class Query(
            val searchinfo: SearchInfo
        )
        data class SearchInfo(
            val totalhits: Int?
        )
    }

    // service
    interface Service {
        @GET("api.php")
        suspend fun presidentName(
            @Query("action") action: String = "query",
            @Query("format") format: String = "json",
            @Query("list") list: String = "search",
            @Query("srsearch") srsearch: String = "Trump",
        ): Model.Result
    }

    val service: Service by lazy {
        retrofit.create(Service::class.java)
    }
}