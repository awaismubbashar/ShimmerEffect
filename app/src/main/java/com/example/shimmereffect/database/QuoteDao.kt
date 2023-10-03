package com.example.shimmereffect.database

import androidx.room.*
import com.example.shimmereffect.model.Quotes

@Dao
interface QuoteDao {
    @Query("SELECT * FROM QuoteEntity LIMIT (:index), 10")
    fun getQuotes(index: Int): List<QuoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addQuote(quotes: List<QuoteEntity>?)
}