package com.example.storyapp.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.pref.UserPreference
import com.example.storyapp.repository.StoryRepository
import kotlinx.coroutines.launch
import com.example.storyapp.repository.Result
import com.example.storyapp.utils.Event
import kotlinx.coroutines.flow.first


class HomeViewModel(
    private val storyRepository: StoryRepository,
    private val userPreference: UserPreference
) : ViewModel() {

    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> get() = _stories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> get() = _errorMessage

    init {
        getAllStories()
    }

    private fun getAllStories() {
        _isLoading.value = true
        viewModelScope.launch {
            val token = userPreference.getToken().first()
            if (token != null) {
                when (val result = storyRepository.gwtAllStories(token)) {
                    is Result.Loading -> {
                        _isLoading.value = true
                    }

                    is Result.Success -> {
                        _isLoading.value = false
                        _stories.value = result.data.listStory
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