package com.example.shimmereffect.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class Quotes(
    @Json(name = "author")
    var author: String?,
    @Json(name = "id")
    var id: Int?,
    @Json(name = "quote")
    var quote: String?
) : Parcelable