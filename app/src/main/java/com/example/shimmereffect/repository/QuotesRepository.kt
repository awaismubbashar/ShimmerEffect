package com.example.shimmereffect.repository


import com.example.shimmereffect.base.BaseRepository
import com.example.shimmereffect.database.QuoteDao
import com.example.shimmereffect.database.QuoteEntity
import com.example.shimmereffect.model.Quotes
import com.example.shimmereffect.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuotesRepository @Inject constructor(var apiService: ApiService, var quoteDao: QuoteDao): BaseRepository() {

    suspend fun getQuotesFromServer(
    ) = safeApiCall {
        apiService.getQuotes()
    }

    suspend fun getQuoteFromDB(index: Int) = safeApiCall {
        quoteDao.getQuotes(index)
    }

    fun addQuoteInDB(quotes: List<QuoteEntity>?) {
        quoteDao.addQuote(quotes)
    }
}