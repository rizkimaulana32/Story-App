package com.example.storyapp.ui.main.addstory

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.pref.UserPreference
import com.example.storyapp.repository.Result
import com.example.storyapp.repository.StoryRepository
import com.example.storyapp.utils.Event
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(
    private val storyRepository: StoryRepository,
    private val userPreference: UserPreference
) : ViewModel() {

    private val _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: LiveData<Uri?> = _currentImageUri

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _successMessage = MutableLiveData<Event<String?>>()
    val successMessage: LiveData<Event<String?>> get() = _successMessage

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> get() = _errorMessage

    fun setImageUri(uri: Uri) {
        _currentImageUri.value = uri
    }

    fun addStory(uri: MultipartBody.Part, description: RequestBody, lat: RequestBody? = null, lon: RequestBody? = null) {
        _isLoading.value = true
        viewModelScope.launch {
            val token = userPreference.getToken().first()
            if (token != null) {
                when (val result = storyRepository.addStory(token, uri, description, lat, lon)) {
                    is Result.Loading -> {
                        _isLoading.value = true
                    }
                    is Result.Success -> {
                        _isLoading.value = false
                        _successMessage.value = Event(result.data.message)
                        _currentImageUri.value = null
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


    private val lat = MutableLiveData<Double>()
    val latitude: LiveData<Double> = lat

    private val lon = MutableLiveData<Double>()
    val longitude: LiveData<Double> = lon

    fun addLocation(lat: Double? = null, lon: Double? = null) {
        this.lat.value = lat
        this.lon.value = lon
    }
}