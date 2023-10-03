package com.example.shimmereffect.mapper

import com.example.shimmereffect.database.QuoteEntity
import com.example.shimmereffect.model.Quotes

fun List<Quotes>.quotesToQuoteEntity(): List<QuoteEntity> {

   return this.map {
       QuoteEntity(id = it.id, author = it.author, quote = it.quote)
    }.toMutableList()

   /* return QuoteEntity(
        id = id,
        author= author,
        quote = quote

    )*/
}


