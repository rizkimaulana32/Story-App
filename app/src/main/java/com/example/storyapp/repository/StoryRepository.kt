package com.example.storyapp.repository

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.data.StoryRemoteMediator
import com.example.storyapp.data.local.Story
import com.example.storyapp.data.local.StoryDatabase
import com.example.storyapp.data.remote.response.AddStoryResponse
import com.example.storyapp.data.remote.response.DetailStoryResponse
import com.example.storyapp.data.remote.response.StoryResponse
import com.example.storyapp.data.remote.retrofit.ApiService
import com.example.storyapp.utils.ErrorUtil.getErrorMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase,
    private val dispatcher: CoroutineDispatcher
) {
    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            storyDatabase: StoryDatabase,
            dispatcher: CoroutineDispatcher
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, storyDatabase, dispatcher)
            }.also { instance = it }
    }

    fun getStories(token: String): LiveData<PagingData<Story>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(
                database = storyDatabase,
                apiService = apiService,
                token = token
            ),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
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
        description: RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ): Result<AddStoryResponse> =
        withContext(dispatcher) {
            Result.Loading
            try {
                val response = apiService.addStory("Bearer $token", photo, description, lat, lon)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(getErrorMessage(e))
            }
        }

    suspend fun getStoriesWithLocation(token: String): Result<StoryResponse> =
        withContext(dispatcher) {
            Result.Loading
            try {
                val response = apiService.getStoriesWithLocation("Bearer $token")
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(getErrorMessage(e))
            }
        }
}