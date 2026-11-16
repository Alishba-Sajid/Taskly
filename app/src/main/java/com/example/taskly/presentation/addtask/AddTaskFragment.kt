package com.example.taskly.presentation.addtask

import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.taskly.core.extentionfunc.collectLifecycleFlow
import com.example.taskly.core.extentionfunc.disable
import com.example.taskly.core.extentionfunc.enable
import com.example.taskly.core.extentionfunc.showDatePicker
import com.example.taskly.databinding.AddTaskFragmentBinding
import com.example.taskly.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddTaskFragment : BaseFragment<AddTaskFragmentBinding>(
    AddTaskFragmentBinding::inflate
) {
    private val viewModel: AddTaskViewModel by viewModels()

    override fun setupListeners() {

        binding.btnBack.setOnClickListener {
            viewModel.onBackClicked()
        }

        binding.etTaskTitle.doAfterTextChanged { text ->
            viewModel.onTitleChanged(text?.toString().orEmpty())
        }

        binding.etTaskDescription.doAfterTextChanged { text ->
            viewModel.onDescriptionChanged(text?.toString().orEmpty())
        }

        binding.etTaskDueDate.setOnClickListener {
            viewModel.onDueDateClicked()
        }

        binding.btnSaveTask.setOnClickListener {
            viewModel.onSaveClicked()
        }
    }

    override fun observeData() {

        collectLifecycleFlow(viewModel.events) { event ->
            when (event) {
                AddTaskEvent.NavigateBack -> {
                    findNavController().navigateUp()
                }

                AddTaskEvent.ShowDatePicker -> {
                    openDatePicker()
                }
            }
        }

        collectLifecycleFlow(viewModel.dueDateText) { dueDate ->
            binding.etTaskDueDate.setText(dueDate)
        }

        collectLifecycleFlow(viewModel.titleError) { errorResId ->
            binding.tilTaskTitle.error = errorResId?.let {
                getString(it)
            }
        }

        collectLifecycleFlow(viewModel.dueDateError) { errorResId ->
            binding.tilTaskDueDate.error = errorResId?.let {
                getString(it)
            }
        }

        collectLifecycleFlow(viewModel.isSaving) { isSaving ->
            if (isSaving) {
                binding.btnSaveTask.disable()
            } else {
                binding.btnSaveTask.enable()
            }
        }
    }private fun openDatePicker() {
        showDatePicker { year, month, dayOfMonth ->

            val selectedCalendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }

            val dueDate = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            ).format(selectedCalendar.time)

            val displayDate = SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
            ).format(selectedCalendar.time)

            viewModel.onDueDateSelected(
                dueDate = dueDate,
                displayDate = displayDate
            )
        }
    }
    }

