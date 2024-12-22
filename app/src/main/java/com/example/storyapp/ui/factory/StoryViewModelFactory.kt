package com.example.storyapp.ui.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.di.Injection
import com.example.storyapp.pref.UserPreference
import com.example.storyapp.repository.StoryRepository
import com.example.storyapp.ui.main.addstory.AddStoryViewModel
import com.example.storyapp.ui.main.detail.DetailViewModel
import com.example.storyapp.ui.main.map.MapViewModel

class StoryViewModelFactory(
    private val storyRepository: StoryRepository,
    private val userPreference: UserPreference
) : ViewModelProvider.NewInstanceFactory() {

    companion object {
        @Volatile
        private var instance: StoryViewModelFactory? = null

        fun getInstance(
            context: Context,
            userPreference: UserPreference
        ): StoryViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: StoryViewModelFactory(
                    Injection.provideStoryRepository(context), userPreference
                )
            }.also { instance = it }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
       if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(storyRepository, userPreference) as T
        } else if (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) {
            return AddStoryViewModel(storyRepository, userPreference) as T
        } else if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(storyRepository, userPreference) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}
