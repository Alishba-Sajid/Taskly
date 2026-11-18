package com.example.taskly.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
abstract class BaseFragment<VB : ViewBinding>(
    private val inflateBinding: (
        inflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ) -> VB
) : Fragment() {
    private var _binding: VB? = null
    protected val binding: VB
        get() = checkNotNull(_binding) {
            "Binding is only valid between onCreateView and onDestroyView."
        }

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflateBinding(inflater, container, false)
        return binding.root
    }

    final override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupListeners()
        observeData()
    }
    protected open fun setupViews() = Unit
    protected open fun setupListeners() = Unit
    protected open fun observeData() = Unit

    final override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
