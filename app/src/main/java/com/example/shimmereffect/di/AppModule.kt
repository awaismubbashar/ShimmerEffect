package com.example.shimmereffect.di

import android.content.Context
import android.os.StrictMode
import androidx.room.Room
import com.example.shimmereffect.AppPreferences
import com.example.shimmereffect.database.QuoteDao
import com.example.shimmereffect.database.QuoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesAppPreferences(@ApplicationContext context: Context): AppPreferences {
        val oldPolicy = StrictMode.allowThreadDiskReads()
        try {
            return AppPreferences(context)
        } finally {
            StrictMode.setThreadPolicy(oldPolicy)
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): QuoteDatabase {
        return Room.databaseBuilder(
            context,
            QuoteDatabase::class.java,
            "quote_database"
        ).allowMainThreadQueries().build()
    }


    @Provides
    @Singleton
    fun provideTripDao(quoteDb: QuoteDatabase): QuoteDao = quoteDb.quoteDao()

}