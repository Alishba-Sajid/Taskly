package com.example.taskly.presentation.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.taskly.R
import com.example.taskly.databinding.SplashFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: SplashFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SplashFragmentBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.startSplash { isOnboardingCompleted ->

            val destination = if (isOnboardingCompleted) {
                R.id.action_splashFragment_to_homeFragment
            } else {
                R.id.action_splashFragment_to_onboardingFragment
            }

            findNavController().navigate(destination)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
