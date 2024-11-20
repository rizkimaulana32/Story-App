package com.example.storyapp.ui.main.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.databinding.FragmentHomeBinding
import com.example.storyapp.pref.UserPreference
import com.example.storyapp.pref.dataStore
import com.example.storyapp.ui.adapters.ListStoryAdapter
import com.example.storyapp.ui.factory.StoryViewModelFactory

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = UserPreference.getInstance(requireContext().dataStore)
        homeViewModel = ViewModelProvider(
            requireActivity(),
            StoryViewModelFactory.getInstance(pref)
        )[HomeViewModel::class.java]

        val adapter = ListStoryAdapter { story ->
            val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(story.id ?: "")
            findNavController().navigate(action)
        }

        binding.rvStory.adapter = adapter
        binding.rvStory.layoutManager = LinearLayoutManager(requireContext())

        homeViewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        homeViewModel.stories.observe(viewLifecycleOwner) { stories ->
            adapter.submitList(stories)
        }

        homeViewModel.errorMessage.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addStoryFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}