package com.example.shimmereffect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shimmereffect.database.QuoteEntity
import com.example.shimmereffect.model.QuoteResponse
import com.example.shimmereffect.model.Quotes
import com.example.shimmereffect.network.Resource
import com.example.shimmereffect.repository.QuotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val quotesRepository: QuotesRepository) : ViewModel() {


    private val _quote: MutableLiveData<Resource<QuoteResponse>> = MutableLiveData()
    val quoteResponse: LiveData<Resource<QuoteResponse>>
        get() = _quote

    fun getQuotesFromServer() = viewModelScope.launch {
        _quote.value = Resource.Loading
        _quote.value = quotesRepository.getQuotesFromServer()
    }

    private val _localQuote: MutableLiveData<Resource<List<QuoteEntity>>> = MutableLiveData()
    val localQuoteResponse: LiveData<Resource<List<QuoteEntity>>>
        get() = _localQuote

    fun getQuotesFromDB(index: Int) = viewModelScope.launch {
        _localQuote.value = Resource.Loading
        _localQuote.value = quotesRepository.getQuoteFromDB(index)
    }

    fun addQuotesInDB(quotes: List<QuoteEntity>?) = viewModelScope.launch {
        quotesRepository.addQuoteInDB(quotes)
    }

}