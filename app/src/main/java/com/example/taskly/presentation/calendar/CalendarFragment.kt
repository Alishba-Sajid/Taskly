package com.example.taskly.presentation.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskly.R
import com.example.taskly.databinding.FragmentCalendarBinding
import com.example.taskly.domain.model.Task
import com.example.taskly.presentation.home.TaskAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
@AndroidEntryPoint
class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CalendarViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        setupRecyclerView()
        setupCalendar()
        observeUiState()
    }
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onEditClick = { task ->
                val bundle = Bundle().apply {
                    putInt("taskId", task.id)
                }

                findNavController().navigate(
                    R.id.action_calendarFragment_to_editTaskFragment,
                    bundle
                )
            },

            onDeleteClick = { task ->
                showDeleteConfirmation(task)
            },

            onStatusChangeClick = { task, isCompleted ->
                viewModel.updateTaskStatus(
                    task = task,
                    isCompleted = isCompleted
                )
            }
        )

        binding.rvCalendarTasks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdapter
        }
    }

    private fun setupCalendar() {
        binding.calendarView.setOnDateChangeListener {
                _, year, month, dayOfMonth ->

            val selectedDate = String.format(
                Locale.US,
                "%04d-%02d-%02d",
                year,
                month + 1,
                dayOfMonth
            )

            viewModel.onDateSelected(selectedDate)
        }
    }
    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->

                    binding.tvSelectedDate.text = getString(
                        R.string.tasks_for_date,
                        formatDisplayDate(state.selectedDate)
                    )

                    taskAdapter.submitList(state.tasks)

                    binding.tvEmptyTasks.isVisible = state.tasks.isEmpty()
                    binding.rvCalendarTasks.isVisible = state.tasks.isNotEmpty()
                }
            }
        }
    }
    private fun formatDisplayDate(date: String): String {
        val parsedDate = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        ).parse(date)

        return parsedDate?.let {
            SimpleDateFormat(
                "EEEE, dd MMM yyyy",
                Locale.getDefault()
            ).format(it)
        } ?: date
    }
    private fun showDeleteConfirmation(task: Task) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_task_title))
            .setMessage(getString(R.string.delete_task_message))
            .setNegativeButton(getString(R.string.cancel), null)
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                viewModel.deleteTask(task)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
