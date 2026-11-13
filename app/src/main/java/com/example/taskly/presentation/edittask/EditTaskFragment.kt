package com.example.taskly.presentation.edittask

import android.app.DatePickerDialog
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
@AndroidEntryPoint
class EditTaskFragment : Fragment() {
    private var _binding: EditTaskFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EditTaskViewModel by viewModels()
    private var selectedDueDate: String? = null

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

        binding.etTaskDueDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnSaveChanges.setOnClickListener {
            val title = binding.etTaskTitle.text.toString()
            val description = binding.etTaskDescription.text.toString()

            viewModel.updateTask(
                title = title,
                description = description,
                dueDate = selectedDueDate
            )
        }

        binding.etTaskTitle.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.tilTaskTitle.error = null
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        selectedDueDate?.let { date ->
            val parts = date.split("-")

            if (parts.size == 3) {
                calendar.set(
                    parts[0].toInt(),
                    parts[1].toInt() - 1,
                    parts[2].toInt()
                )
            }
        }

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

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->

                    state.task?.let { task ->
                        binding.etTaskTitle.setText(task.title)
                        binding.etTaskDescription.setText(task.description)

                        selectedDueDate = task.dueDate

                        val parsedDate = SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.US
                        ).parse(task.dueDate)

                        val displayDate = parsedDate?.let {
                            SimpleDateFormat(
                                "dd MMM yyyy",
                                Locale.getDefault()
                            ).format(it)
                        } ?: task.dueDate

                        binding.etTaskDueDate.setText(displayDate)
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

                        EditTaskEffect.ShowDueDateError -> {
                            binding.tilTaskDueDate.error =
                                getString(R.string.task_due_date_required)
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
