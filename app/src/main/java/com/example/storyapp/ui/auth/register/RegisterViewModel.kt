package com.example.storyapp.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.repository.AuthRepository
import kotlinx.coroutines.launch
import com.example.storyapp.repository.Result
import com.example.storyapp.utils.Event

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _successMessage = MutableLiveData<Event<String?>>()
    val successMessage: LiveData<Event<String?>> get() = _successMessage

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true

        viewModelScope.launch {
            when (val result = authRepository.register(name, email, password)) {
                is Result.Loading -> {
                    _isLoading.value = true
                }

                is Result.Success -> {
                    _isLoading.value = false
                    _successMessage.value = Event(result.data.message)
                }

                is Result.Error -> {
                    _isLoading.value = false
                    _errorMessage.value = Event(result.error)
                }
            }
        }
    }
}