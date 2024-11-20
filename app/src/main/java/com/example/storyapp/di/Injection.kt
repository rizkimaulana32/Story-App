package com.example.storyapp.di

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

    fun provideStoryRepository(): StoryRepository {
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(apiService)
    }
}