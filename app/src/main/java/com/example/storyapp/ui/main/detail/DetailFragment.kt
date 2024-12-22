package com.example.storyapp.ui.main.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.databinding.FragmentDetailBinding
import com.example.storyapp.pref.UserPreference
import com.example.storyapp.pref.dataStore
import com.example.storyapp.ui.factory.StoryViewModelFactory
import com.example.storyapp.utils.DateUtil

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = UserPreference.getInstance(requireContext().dataStore)
        val detailViewModel = ViewModelProvider(
            requireActivity(),
            StoryViewModelFactory.getInstance(requireContext(), pref)
        )[DetailViewModel::class.java]

        val args = DetailFragmentArgs.fromBundle(requireArguments())
        val storyId = args.storyId

        if (savedInstanceState == null) {
            detailViewModel.fetchStoryWithToken(storyId)
        }

        detailViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            loading(isLoading)
        }

        detailViewModel.story.observe(viewLifecycleOwner) { story ->
            if (story != null) {
                setStoryData(story)
            }
        }

        detailViewModel.errorMessage.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                if (message.isNotEmpty()) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "An error occurred", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setStoryData(story: Story) {
        binding.apply {
            Glide.with(binding.imageViewPhoto.context)
                .load(story.photoUrl)
                .into(imageViewPhoto)

            textViewName.text = story.name
            textViewDescription.text = story.description
            textViewCreatedAt.text = DateUtil.dateFormat(story.createdAt)
        }
    }


    private fun loading(isLoading: Boolean) {
        binding.apply {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            imageViewPhoto.visibility = if (isLoading) View.GONE else View.VISIBLE
            textViewName.visibility = if (isLoading) View.GONE else View.VISIBLE
            textViewDescription.visibility = if (isLoading) View.GONE else View.VISIBLE
            textViewCreatedAt.visibility = if (isLoading) View.GONE else View.VISIBLE
            cardViewPhoto.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
    }
}