package com.example.storyapp.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.di.Injection
import com.example.storyapp.pref.UserPreference
import com.example.storyapp.repository.StoryRepository
import com.example.storyapp.ui.main.addstory.AddStoryViewModel
import com.example.storyapp.ui.main.detail.DetailViewModel
import com.example.storyapp.ui.main.home.HomeViewModel

class StoryViewModelFactory(
    private val storyRepository: StoryRepository,
    private val userPreference: UserPreference
) : ViewModelProvider.NewInstanceFactory() {

    companion object {
        @Volatile
        private var instance: StoryViewModelFactory? = null

        fun getInstance(
            userPreference: UserPreference
        ): StoryViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: StoryViewModelFactory(
                    Injection.provideStoryRepository(), userPreference
                )
            }.also { instance = it }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(storyRepository, userPreference) as T
        } else if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(storyRepository, userPreference) as T
        } else if (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) {
            return AddStoryViewModel(storyRepository, userPreference) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}
