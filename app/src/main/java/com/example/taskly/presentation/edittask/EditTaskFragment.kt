package com.example.taskly.presentation.edittask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.taskly.R
import com.example.taskly.databinding.EditTaskFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditTaskFragment : Fragment() {

    private var _binding: EditTaskFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditTaskViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EditTaskFragmentBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeUiState()
        observeEffects()

        val taskId = arguments?.getInt("taskId", -1) ?: -1

        if (taskId > 0) {
            viewModel.loadTask(taskId)
        } else {
            Toast.makeText(
                requireContext(),
                R.string.task_not_found,
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigateUp()
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSaveChanges.setOnClickListener {
            val title = binding.etTaskTitle.text.toString()
            val description = binding.etTaskDescription.text.toString()

            viewModel.updateTask(title, description)
        }

        binding.etTaskTitle.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.tilTaskTitle.error = null
            }
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    state.task?.let { task ->
                        binding.etTaskTitle.setText(task.title)
                        binding.etTaskDescription.setText(task.description)
                    }
                }
            }
        }
    }

    private fun observeEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effects.collect { effect ->
                    when (effect) {
                        EditTaskEffect.NavigateBack -> {
                            findNavController().navigateUp()
                        }

                        EditTaskEffect.ShowTitleError -> {
                            binding.tilTaskTitle.error =
                                getString(R.string.task_title_required)
                        }

                        EditTaskEffect.TaskNotFound -> {
                            Toast.makeText(
                                requireContext(),
                                R.string.task_not_found,
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigateUp()
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