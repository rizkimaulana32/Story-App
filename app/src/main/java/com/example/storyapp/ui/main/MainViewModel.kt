package com.example.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.pref.UserPreference
import kotlinx.coroutines.launch

class MainViewModel(private val userPreference: UserPreference): ViewModel() {
    init {
        getToken()
    }

    fun getToken(): LiveData<String?> {
        return userPreference.getToken().asLiveData()
    }

    fun clearToken() {
        viewModelScope.launch {
            userPreference.clearToken()
        }
    }
}