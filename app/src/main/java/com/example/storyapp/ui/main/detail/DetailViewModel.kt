package com.example.storyapp.ui.main.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.pref.UserPreference
import com.example.storyapp.repository.Result
import com.example.storyapp.repository.StoryRepository
import com.example.storyapp.utils.Event
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DetailViewModel(
    private val storyRepository: StoryRepository,
    private val userPreference: UserPreference
) : ViewModel() {
    private val _story = MutableLiveData<Story?>()
    val story: LiveData<Story?> get() = _story

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> get() = _errorMessage

    fun fetchStoryWithToken(id: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val token = userPreference.getToken().first()
            if (token != null) {
                when (val result = storyRepository.getStory(token, id)) {
                    is Result.Loading -> {
                        _isLoading.value = true
                    }
                    is Result.Success -> {
                        _isLoading.value = false
                        _story.value = result.data.story
                    }
                    is Result.Error -> {
                        _isLoading.value = false
                        _errorMessage.value = Event(result.error)
                    }
                }
            } else {
                _isLoading.value = false
            }
        }
    }
}