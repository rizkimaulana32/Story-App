package com.example.storyapp.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.local.Story
import com.example.storyapp.repository.StoryRepository

class HomeViewModel(
    private val storyRepository: StoryRepository,
) : ViewModel() {
    private var _pagingData: LiveData<PagingData<Story>> = MutableLiveData()

    fun stories(token: String): LiveData<PagingData<Story>> {
        if (_pagingData.value == null) {
            _pagingData = storyRepository.getStories(token).cachedIn(viewModelScope)
        }
        return _pagingData
    }
}