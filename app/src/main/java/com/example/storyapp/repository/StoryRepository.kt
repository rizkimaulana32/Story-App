package com.example.storyapp.repository

import com.example.storyapp.data.remote.response.AddStoryResponse
import com.example.storyapp.data.remote.response.DetailStoryResponse
import com.example.storyapp.data.remote.response.StoryResponse
import com.example.storyapp.data.remote.retrofit.ApiService
import com.example.storyapp.utils.ErrorUtil.getErrorMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService)
            }.also { instance = it }
    }

    suspend fun gwtAllStories(token: String): Result<StoryResponse> =
        withContext(dispatcher) {
            Result.Loading
            try {
                val response = apiService.getStories("Bearer $token")
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(getErrorMessage(e))
            }
        }

    suspend fun getStory(token: String, id: String): Result<DetailStoryResponse> =
        withContext(dispatcher) {
            try {
                val response = apiService.getStory("Bearer $token", id)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(getErrorMessage(e))
            }
        }

    suspend fun addStory(
        token: String,
        photo: MultipartBody.Part,
        description: RequestBody
    ): Result<AddStoryResponse> =
        withContext(dispatcher) {
            Result.Loading
            try {
                val response = apiService.addStory("Bearer $token", photo, description)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(getErrorMessage(e))
            }
        }
}