package com.example.taskly.presentation.onboarding

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.taskly.R
import com.example.taskly.databinding.OnboardingFragmentBinding
import com.example.taskly.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OnboardingFragment : BaseFragment<OnboardingFragmentBinding>(
    OnboardingFragmentBinding::inflate
) {
    private val viewModel: OnboardingViewModel by viewModels()

    override fun setupListeners() {
        binding.btnContinue.setOnClickListener {
            viewModel.onContinueClicked()
        }
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.titleResId.collect { titleResId ->
                        binding.tvOnboardingTitle.text = getString(titleResId)
                    }
                }

                launch {
                    viewModel.descriptionResId.collect { descriptionResId ->
                        binding.tvOnboardingDescription.text =
                            getString(descriptionResId)
                    }
                }

                launch {
                    viewModel.illustrationResId.collect { illustrationResId ->
                        binding.tvIllustration.text = getString(illustrationResId)
                    }
                }

                launch {
                    viewModel.buttonTextResId.collect { buttonTextResId ->
                        binding.btnContinue.text = getString(buttonTextResId)
                    }
                }

                launch {
                    viewModel.currentPageIndex.collect { currentPageIndex ->
                        renderPageIndicator(currentPageIndex)
                    }
                }

                launch {
                    viewModel.navigationEvents.collect {
                        findNavController().navigate(
                            R.id.action_onboardingFragment_to_homeFragment
                        )
                    }
                }
            }
        }
    }

    private fun renderPageIndicator(currentPageIndex: Int) {
        binding.tvPageIndicator.text = buildString {
            repeat(viewModel.totalPages) { index ->
                append(if (index == currentPageIndex) "●" else "○")

                if (index < viewModel.totalPages - 1) {
                    append(" ")
                }
            }
        }
    }
}

