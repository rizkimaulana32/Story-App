package com.example.storyapp.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.pref.UserPreference
import com.example.storyapp.repository.AuthRepository
import com.example.storyapp.ui.auth.login.LoginViewModel

class LoginViewModelFactory(
    private val authRepository: AuthRepository,
    private val userPreference: UserPreference
) : ViewModelProvider.NewInstanceFactory() {

//    companion object {
//        @Volatile
//        private var instance: LoginViewModelFactory? = null
//
//        fun getInstance(
//            userPreference: UserPreference
//        ): LoginViewModelFactory =
//            instance ?: synchronized(this) {
//                instance ?: LoginViewModelFactory(
//                    Injection.provideAuthRepository(), userPreference
//                )
//            }.also { instance = it }
//    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(authRepository, userPreference) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}
