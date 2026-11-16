package com.example.taskly.presentation.splash

import androidx.navigation.fragment.findNavController
import com.example.taskly.R
import com.example.taskly.databinding.SplashFragmentBinding
import com.example.taskly.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.viewModels
@AndroidEntryPoint
class SplashFragment : BaseFragment<SplashFragmentBinding>(
    SplashFragmentBinding::inflate
) {
    private val viewModel: SplashViewModel by viewModels()

    override fun observeData() {
        viewModel.startSplash { isOnboardingCompleted ->

            val destination = if (isOnboardingCompleted) {
                R.id.action_splashFragment_to_homeFragment
            } else {
                R.id.action_splashFragment_to_onboardingFragment
            }

            findNavController().navigate(destination)
        }
    }
}