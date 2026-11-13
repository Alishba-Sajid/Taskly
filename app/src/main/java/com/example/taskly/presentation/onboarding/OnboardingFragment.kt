package com.example.taskly.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.taskly.databinding.OnboardingFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController
import com.example.taskly.R
@AndroidEntryPoint
class OnboardingFragment : Fragment() {
    private var _binding: OnboardingFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OnboardingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = OnboardingFragmentBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnContinue.setOnClickListener {
            viewModel.onEvent(OnboardingEvent.ContinueClicked)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.uiState.collect { state ->

                        binding.tvOnboardingTitle.text = state.title
                        binding.tvOnboardingDescription.text = state.description
                        binding.btnContinue.text = state.buttonText

                        binding.tvPageIndicator.text = when (state.currentPage) {
                            0 -> "● ○ ○"
                            1 -> "○ ● ○"
                            else -> "○ ○ ●"
                        }

                        binding.tvIllustration.text = when (state.currentPage) {
                            0 -> "✓"
                            1 -> "✎"
                            else -> "▦"
                        }
                    }
                }

                launch {
                    viewModel.effects.collect { effect ->

                        when (effect) {
                            OnboardingEffect.NavigateToHome -> {
                                findNavController().navigate(
                                    R.id.action_onboardingFragment_to_homeFragment
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
