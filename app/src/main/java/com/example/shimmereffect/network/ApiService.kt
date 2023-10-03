package com.example.shimmereffect.network

import com.example.shimmereffect.model.QuoteResponse
import com.example.shimmereffect.model.User
import retrofit2.http.GET

interface ApiService {

    @GET("quotes")
    suspend fun getQuotes(): QuoteResponse

}