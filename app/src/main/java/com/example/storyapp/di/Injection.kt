package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.data.local.StoryDatabase
import com.example.storyapp.data.remote.retrofit.ApiConfig
import com.example.storyapp.repository.AuthRepository
import com.example.storyapp.repository.StoryRepository
import kotlinx.coroutines.Dispatchers

object Injection {
    fun provideAuthRepository(): AuthRepository {
        val apiService = ApiConfig.getApiService()
        val dispatcher = Dispatchers.IO
        return AuthRepository.getInstance(apiService, dispatcher)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val storyDatabase = StoryDatabase.getDatabase(context)
        val dispatcher = Dispatchers.IO
        return StoryRepository.getInstance(apiService, storyDatabase, dispatcher)
    }
}