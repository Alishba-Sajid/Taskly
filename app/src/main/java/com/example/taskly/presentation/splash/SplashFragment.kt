package com.example.taskly.presentation.splash

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.taskly.R
import com.example.taskly.core.extention.collectLifecycleFlow
import com.example.taskly.databinding.SplashFragmentBinding
import com.example.taskly.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : BaseFragment<SplashFragmentBinding>(
    SplashFragmentBinding::inflate
) {
    private val viewModel: SplashViewModel by viewModels()

    override fun observeData() {
        collectLifecycleFlow(viewModel.navigationEvents) { isOnboardingCompleted ->

            val destination = if (isOnboardingCompleted) {
                R.id.action_splashFragment_to_homeFragment
            } else {
                R.id.action_splashFragment_to_onboardingFragment
            }

            findNavController().navigate(destination)
        }

        viewModel.startSplash()
    }
}
