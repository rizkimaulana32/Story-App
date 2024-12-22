package com.example.storyapp.ui.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.di.Injection
import com.example.storyapp.repository.StoryRepository
import com.example.storyapp.ui.main.home.HomeViewModel

class HomeViewModelFactory(
    private val storyRepository: StoryRepository,
) : ViewModelProvider.NewInstanceFactory() {

    companion object {
        @Volatile
        private var instance: HomeViewModelFactory? = null

        fun getInstance(
            context: Context,
        ): HomeViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: HomeViewModelFactory(
                    Injection.provideStoryRepository(
                        context
                    )
                )
            }.also { instance = it }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}
