package com.karoliinamultas.bluetoothchat.network.responce
import com.google.gson.annotations.SerializedName



data class Medium (

	@SerializedName("filename") val filename : String,
	@SerializedName("name") val name : String,
	@SerializedName("width") val width : Int,
	@SerializedName("height") val height : Int,
	@SerializedName("ratio") val ratio : Double,
	@SerializedName("size") val size : Int,
	@SerializedName("size_formatted") val size_formatted : String,
	@SerializedName("mime") val mime : String,
	@SerializedName("extension") val extension : String,
	@SerializedName("bits") val bits : Int,
	@SerializedName("channels") val channels : String,
	@SerializedName("url") val url : String
)