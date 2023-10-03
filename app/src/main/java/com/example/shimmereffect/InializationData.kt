package com.example.shimmereffect

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object InializationData {
    fun isInternetAvailable(context: Context): String? {
        var result: String? = null
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    result = when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> TYPE_WIFI
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> TYPE_CELLULAR
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> TYPE_ETHERNET
                        else -> null
                    }
                }
            }
        } else {
            cm?.run {
                cm.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        result = TYPE_WIFI
                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
                        result = TYPE_CELLULAR
                    }
                }
            }
        }
        return result
    }

}