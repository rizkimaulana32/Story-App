package com.example.storyapp.ui.main.addstory

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.databinding.FragmentAddStoryBinding
import com.example.storyapp.pref.UserPreference
import com.example.storyapp.pref.dataStore
import com.example.storyapp.ui.factory.StoryViewModelFactory
import com.example.storyapp.ui.main.MainActivity
import com.example.storyapp.utils.ImageUtil
import com.example.storyapp.utils.ImageUtil.reduceFileImage
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
            StoryViewModelFactory.getInstance(pref)
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
        binding.buttonUpload.setOnClickListener { uploadImage() }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            addStoryViewModel.setImageUri(uri)
            showImage(uri)
        } else {
            showToast(resources.getString(R.string.no_image_selected))
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
            showToast(resources.getString(R.string.failed_take_picture))
        }
    }

    private fun startCamera() {
        currentImageUri = ImageUtil.getImageUri(requireContext())
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private fun uploadImage() {
        addStoryViewModel.currentImageUri.value?.let { uri ->
            val description = binding.editTextDescription.text.toString()
            val image = ImageUtil.uriToFile(uri, requireContext()).reduceFileImage()

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = image.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                image.name,
                requestImageFile
            )

            showLoading(true)
            addStoryViewModel.addStory(multipartBody, requestBody)
        } ?: showToast(resources.getString(R.string.no_image_selected))
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}