package com.example.storyapp.ui.auth.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.storyapp.R
import com.example.storyapp.databinding.FragmentLoginBinding
import com.example.storyapp.di.Injection
import com.example.storyapp.pref.UserPreference
import com.example.storyapp.pref.dataStore
import com.example.storyapp.ui.main.MainActivity
import com.example.storyapp.ui.factory.LoginViewModelFactory


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = UserPreference.getInstance(requireContext().dataStore)
        loginViewModel = ViewModelProvider(
            requireActivity(),
            LoginViewModelFactory(Injection.provideAuthRepository(), pref)
        )[LoginViewModel::class.java]

        binding.textButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (validateInputs(email, password)) {
                loginViewModel.login(email, password)
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
        loginViewModel.successMessage.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                showToast(message)
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                requireActivity().finish()
            }
        }

        loginViewModel.errorMessage.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                showToast(message)
            }
        }

        loginViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
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

    private fun validateInputs(email: String, password: String): Boolean {
        var isValid = true
        val editLayout = binding.emailEditTextLayout
        val passwordLayout = binding.passwordEditTextLayout

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
        val email =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val password =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)
        val tvCreate =
            ObjectAnimator.ofFloat(binding.tvCreateAccount, View.ALPHA, 1f).setDuration(500)
        val txtBtn = ObjectAnimator.ofFloat(binding.textButton, View.ALPHA, 1f).setDuration(500)


        val together = AnimatorSet().apply {
            playTogether(tvCreate, txtBtn)
        }

        AnimatorSet().apply {
            playSequentially(tvTitle, email, password, login, together)
            start()
        }
    }

}