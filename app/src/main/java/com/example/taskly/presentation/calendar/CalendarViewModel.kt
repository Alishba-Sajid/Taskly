package com.example.taskly.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskly.domain.model.Task
import com.example.taskly.domain.model.TaskStatus
import com.example.taskly.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val dateFormat = SimpleDateFormat(
        "yyyy-MM-dd",
        Locale.US
    )

    private val _selectedDate = MutableStateFlow(
        dateFormat.format(Date())
    )
    val selectedDate = _selectedDate.asStateFlow()

    val tasks = selectedDate
        .flatMapLatest { date ->
            taskRepository.getTasksByDate(date)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun onDateSelected(date: String) {
        _selectedDate.value = date
    }

    fun deleteTask(task: Task) {
        executeTaskAction {
            taskRepository.deleteTask(task)
        }
    }
    fun updateTaskStatus(task: Task, isCompleted: Boolean) {
        executeTaskAction {
            val updatedStatus = if (isCompleted) {
                TaskStatus.COMPLETED
            } else {
                TaskStatus.IN_PROGRESS
            }

            taskRepository.updateTask(
                task.copy(status = updatedStatus)
            )
        }
    }
    private fun executeTaskAction(action: suspend () -> Unit) {
        viewModelScope.launch {
            action()
        }
    }
}
