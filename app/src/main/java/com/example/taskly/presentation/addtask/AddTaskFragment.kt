package com.example.taskly.presentation.addtask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.taskly.databinding.AddTaskFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import android.app.DatePickerDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.example.taskly.R


@AndroidEntryPoint
class AddTaskFragment : Fragment() {
    private var _binding: AddTaskFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddTaskViewModel by viewModels()
    private var selectedDueDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddTaskFragmentBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()

        observeEffects()
    }
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.etTaskDueDate.setOnClickListener {
            val calendar = Calendar.getInstance()

            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->

                    selectedDueDate = String.format(
                        Locale.US,
                        "%04d-%02d-%02d",
                        year,
                        month + 1,
                        dayOfMonth
                    )

                    val displayDate = SimpleDateFormat(
                        "dd MMM yyyy",
                        Locale.getDefault()
                    ).format(
                        Calendar.getInstance().apply {
                            set(year, month, dayOfMonth)
                        }.time
                    )

                    binding.etTaskDueDate.setText(displayDate)
                    binding.tilTaskDueDate.error = null

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.btnSaveTask.setOnClickListener {
            val title = binding.etTaskTitle.text.toString()
            val description = binding.etTaskDescription.text.toString()

            viewModel.addTask(title, description, selectedDueDate)
        }

        binding.etTaskTitle.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.tilTaskTitle.error = null
            }
        }
    }
    private fun observeEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effects.collect { effect ->
                    when (effect) {
                        AddTaskEffect.NavigateBack -> {
                            findNavController().navigateUp()
                        }

                        AddTaskEffect.ShowTitleError -> {
                            binding.tilTaskTitle.error =
                                getString(R.string.task_title_required)
                        }

                        AddTaskEffect.ShowDueDateError -> {
                            binding.tilTaskDueDate.error =
                                getString(R.string.task_due_date_required)
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
