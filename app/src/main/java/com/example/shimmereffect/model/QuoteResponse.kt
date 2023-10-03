package com.example.shimmereffect.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class QuoteResponse(
    @Json(name = "limit")
    var limit: Int?,
    @Json(name = "quotes")
    var quotesList: List<Quotes>?,
    @Json(name = "skip")
    var skip: Int?,
    @Json(name = "total")
    var total: Int?
) : Parcelable