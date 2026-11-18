package com.example.taskly.presentation.edittask

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.taskly.R
import com.example.taskly.core.extention.collectLifecycleFlow
import com.example.taskly.core.extention.showDatePicker
import com.example.taskly.databinding.EditTaskFragmentBinding
import com.example.taskly.core.extention.enable
import com.example.taskly.core.extention.disable
import com.example.taskly.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class EditTaskFragment : BaseFragment<EditTaskFragmentBinding>(
    EditTaskFragmentBinding::inflate
) {
    private val viewModel: EditTaskViewModel by viewModels()

    private var selectedDueDate: String? = null

    override fun setupListeners() {
        binding.apply {
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            etTaskDueDate.setOnClickListener {
                openDatePicker()
            }

            btnSaveChanges.setOnClickListener {
                val title = etTaskTitle.text.toString()
                val description = etTaskDescription.text.toString()

                viewModel.updateTask(
                    title = title,
                    description = description,
                    dueDate = selectedDueDate
                )
            }

            etTaskTitle.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    tilTaskTitle.error = null
                }
            }
        }
    }

    override fun observeData() {

        collectLifecycleFlow(viewModel.isLoading) { isLoading ->
            binding.apply {
                if (isLoading) {
                    btnSaveChanges.disable()
                } else {
                    btnSaveChanges.enable()
                }
            }
        }

        collectLifecycleFlow(viewModel.task) { task ->
            task?.let {
                val parsedDate = SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.US
                ).parse(it.dueDate)

                val displayDate = parsedDate?.let { date ->
                    SimpleDateFormat(
                        "dd MMM yyyy",
                        Locale.getDefault()
                    ).format(date)
                } ?: it.dueDate

                selectedDueDate = it.dueDate

                binding.apply {
                    etTaskTitle.setText(it.title)
                    etTaskDescription.setText(it.description)
                    etTaskDueDate.setText(displayDate)
                }
            }
        }

        collectLifecycleFlow(viewModel.effects) { effect ->
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

    private fun openDatePicker() {
        showDatePicker(initialDate = selectedDueDate) { year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }

            selectedDueDate = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            ).format(selectedCalendar.time)

            val displayDate = SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
            ).format(selectedCalendar.time)

            binding.apply {
                etTaskDueDate.setText(displayDate)
                tilTaskDueDate.error = null
            }
        }
    }
}