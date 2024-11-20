package com.example.storyapp.ui.auth.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.pref.UserPreference
import com.example.storyapp.repository.AuthRepository
import com.example.storyapp.repository.Result
import com.example.storyapp.utils.Event
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val userPreference: UserPreference
) : ViewModel() {
    private val _successMessage = MutableLiveData<Event<String?>>()
    val successMessage: LiveData<Event<String?>> get() = _successMessage

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun login(email: String, password: String) {
        _isLoading.value = true

        viewModelScope.launch {
            when (val result = authRepository.login(email, password)) {
                is Result.Loading -> {
                    _isLoading.value = true
                }

                is Result.Success -> {
                    _isLoading.value = false
                    result.data.loginResult?.token?.let {
                        userPreference.saveToken(it)
                    }

                    val token = userPreference.getToken().first()
                    if (!token.isNullOrEmpty()) {
                        _successMessage.value = Event(result.data.message)
                    } else {
                        Log.d("LoginViewModel", "Token is null")
                    }
                }

                is Result.Error -> {
                    _isLoading.value = false
                    _errorMessage.value = Event(result.error)
                }
            }
        }
    }
}