package com.example.shimmereffect.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "QuoteEntity")
data class QuoteEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: Int?,
    @ColumnInfo(name = "author")
    var author: String?,
    @ColumnInfo(name = "quote")
    var quote: String?
)