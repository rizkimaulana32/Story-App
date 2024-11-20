package com.example.storyapp.repository

import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.RegisterResponse
import com.example.storyapp.data.remote.retrofit.ApiService
import com.example.storyapp.utils.ErrorUtil.getErrorMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class AuthRepository private constructor(
    private val apiService: ApiService,
    private val dispatcher: CoroutineDispatcher
) {
    companion object {
        @Volatile
        private var instance: AuthRepository? = null
        fun getInstance(
            apiService: ApiService,
            dispatcher: CoroutineDispatcher
        ): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(apiService, dispatcher)
            }.also { instance = it }
    }

    suspend fun register(name: String, email: String, password: String): Result<RegisterResponse> =
        withContext(dispatcher) {
            try {
                val response = apiService.register(name, email, password)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(getErrorMessage(e))
            }
        }

    suspend fun login(email: String, password: String): Result<LoginResponse> =
        withContext(dispatcher) {
            try {
                val response = apiService.login(email, password)
                Result.Success(response)
            } catch (e: Exception) {
                Result.Error(getErrorMessage(e))
            }
        }
}