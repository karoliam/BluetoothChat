package com.karoliinamultas.bluetoothchat.network.responce

import Thumb
import com.google.gson.annotations.SerializedName




data class Image (

	@SerializedName("name") val name : String,
	@SerializedName("extension") val extension : String,
	@SerializedName("size") val size : Int,
	@SerializedName("width") val width : Int,
	@SerializedName("height") val height : Int,
	@SerializedName("date") val date : String,
	@SerializedName("date_gmt") val date_gmt : String,
	@SerializedName("storage_id") val storage_id : String,
	@SerializedName("description") val description : String,
	@SerializedName("nsfw") val nsfw : Int,
	@SerializedName("md5") val md5 : String,
	@SerializedName("storage") val storage : String,
	@SerializedName("original_filename") val original_filename : String,
	@SerializedName("original_exitdata") val original_exitdata : String,
	@SerializedName("views") val views : Int,
	@SerializedName("id_encoded") val id_encoded : String,
	@SerializedName("filename") val filename : String,
	@SerializedName("ratio") val ratio : Double,
	@SerializedName("size_formatted") val size_formatted : String,
	@SerializedName("mime") val mime : String,
	@SerializedName("bits") val bits : Int,
	@SerializedName("channels") val channels : String,
	@SerializedName("url") val url : String,
	@SerializedName("url_viewer") val url_viewer : String,
	@SerializedName("thumb") val thumb : Thumb,
	@SerializedName("medium") val medium : Medium,
	@SerializedName("views_label") val views_label : String,
	@SerializedName("display_url") val display_url : String,
	@SerializedName("how_long_ago") val how_long_ago : String
)