package com.example.shimmereffect.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id: Int = 0,
    val name: String = "",
    val email: String = "",
    val avatar: Int = 0
): Parcelable
