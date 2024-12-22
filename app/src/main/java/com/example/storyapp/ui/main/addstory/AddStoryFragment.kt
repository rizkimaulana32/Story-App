package com.example.storyapp.ui.main.addstory

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.databinding.FragmentAddStoryBinding
import com.example.storyapp.pref.UserPreference
import com.example.storyapp.pref.dataStore
import com.example.storyapp.ui.factory.StoryViewModelFactory
import com.example.storyapp.ui.main.MainActivity
import com.example.storyapp.utils.ImageUtil
import com.example.storyapp.utils.ImageUtil.reduceFileImage
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoryFragment : Fragment() {
    private var _binding: FragmentAddStoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var addStoryViewModel: AddStoryViewModel
    private var currentImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = UserPreference.getInstance(requireContext().dataStore)
        addStoryViewModel = ViewModelProvider(
            requireActivity(),
            StoryViewModelFactory.getInstance(requireContext(), pref)
        )[AddStoryViewModel::class.java]

        addStoryViewModel.currentImageUri.observe(viewLifecycleOwner) { uri ->
            uri?.let {
                showImage(it)
            }
        }

        addStoryViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        addStoryViewModel.successMessage.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { message ->
                showToast(message)
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }

        addStoryViewModel.errorMessage.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { message ->
                showToast(message)
            }
        }

        binding.buttonGallery.setOnClickListener { startGallery() }
        binding.buttonCamera.setOnClickListener { startCamera() }
        binding.buttonUpload.setOnClickListener { upload() }
        binding.cbAddLoc.setOnClickListener {
            if (binding.cbAddLoc.isChecked) {
                getMyLocation()
            } else {
                addStoryViewModel.addLocation(null, null)
            }
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            addStoryViewModel.setImageUri(uri)
            showImage(uri)
        } else {
            showToast(getString(R.string.no_image_selected))
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showToast(s: String) {
        Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show()
    }

    private fun showImage(uri: Uri) {
        binding.imagePreview.setImageURI(uri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess && currentImageUri != null) {
            addStoryViewModel.setImageUri(currentImageUri!!)
            showImage(currentImageUri!!)
        } else {
            showToast(getString(R.string.failed_take_picture))
        }
    }

    private fun startCamera() {
        currentImageUri = ImageUtil.getImageUri(requireContext())
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private fun upload() {
        addStoryViewModel.currentImageUri.value?.let { uri ->
            val description = binding.editTextDescription.text.toString()
            val lat = addStoryViewModel.latitude.value
            val lon = addStoryViewModel.longitude.value
            val image = ImageUtil.uriToFile(uri, requireContext()).reduceFileImage()

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = image.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                image.name,
                requestImageFile
            )

            val latRequestBody = lat?.toString()?.toRequestBody("text/plain".toMediaType())
            val lonRequestBody = lon?.toString()?.toRequestBody("text/plain".toMediaType())

            showLoading(true)
            addStoryViewModel.addStory(multipartBody, requestBody, latRequestBody, lonRequestBody)
            Log.d("halooo", "$lat $lon")
        } ?: showToast(getString(R.string.no_image_selected))
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            val locationManager =
                requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
                binding.cbAddLoc.isChecked = false
            } else {
                val fusedLocation =
                    LocationServices.getFusedLocationProviderClient(requireContext())
                fusedLocation.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        addStoryViewModel.addLocation(location.latitude, location.longitude)
                        showToast(getString(R.string.location_added))
                    } else {
                        showToast(getString(R.string.location_not_found))
                    }
                }
            }
        } else {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            showToast(getString(R.string.permission_location_granted))
            getMyLocation()
        } else {
            showToast(getString(R.string.permission_location_denied))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}