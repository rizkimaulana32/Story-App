package com.example.storyapp.ui.auth.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.storyapp.R
import com.example.storyapp.databinding.FragmentRegisterBinding
import com.example.storyapp.ui.factory.RegisterViewModelFactory

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerViewModel = ViewModelProvider(
            requireActivity(),
            RegisterViewModelFactory.getInstance()
        )[RegisterViewModel::class.java]

        binding.textLoginButton.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.registerButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (validateInputs(name, email, password)) {
                registerViewModel.register(name, email, password)
            }
        }

        observeViewModel()
        playAnimation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        registerViewModel.successMessage.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                showToast(message)
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }

        registerViewModel.errorMessage.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                showToast(message)
            }
        }

        registerViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.overlayView.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun validateInputs(name: String, email: String, password: String): Boolean {
        var isValid = true
        val nameLayout = binding.nameEditLayout
        val editLayout = binding.emailEditTextLayout
        val passwordLayout = binding.passwordEditTextLayout

        if (name.isBlank()) {
            nameLayout.error = getString(R.string.error_empty_name)
            isValid = false
        } else {
            nameLayout.error = null
            nameLayout.isErrorEnabled = false
        }

        if (email.isBlank()) {
            editLayout.error = getString(R.string.error_empty_email)
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editLayout.error = getString(R.string.validationEmail)
            isValid = false
        } else {
            editLayout.error = null
            editLayout.isErrorEnabled = false
        }

        if (password.isBlank()) {
            passwordLayout.error = getString(R.string.error_empty_password)
            isValid = false
        } else if (password.length < 8) {
            passwordLayout.error = getString(R.string.error_short_password)
            isValid = false
        } else {
            passwordLayout.error = null
            passwordLayout.isErrorEnabled = false
        }

        return isValid
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.logo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val tvTitle = ObjectAnimator.ofFloat(binding.titleText, View.ALPHA, 1f).setDuration(500)
        val name = ObjectAnimator.ofFloat(binding.nameEditLayout, View.ALPHA, 1f).setDuration(500)
        val email =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val password =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val register =
            ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(500)
        val tvHave =
            ObjectAnimator.ofFloat(binding.tvHaveAnAccount, View.ALPHA, 1f).setDuration(500)
        val txtBtn =
            ObjectAnimator.ofFloat(binding.textLoginButton, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(tvHave, txtBtn)
        }

        AnimatorSet().apply {
            playSequentially(tvTitle, name, email, password, register, together)
            start()
        }
    }
}