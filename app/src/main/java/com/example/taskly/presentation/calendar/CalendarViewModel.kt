package com.example.taskly.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskly.domain.model.Task
import com.example.taskly.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch


data class CalendarUiState(
    val selectedDate: String,
    val tasks: List<Task> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val dateFormat =
        SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private val selectedDate = MutableStateFlow(
        dateFormat.format(Date())
    )

    val uiState: StateFlow<CalendarUiState> =
        selectedDate
            .flatMapLatest { date ->
                taskRepository.getTasksByDate(date).map { tasks ->
                    CalendarUiState(
                        selectedDate = date,
                        tasks = tasks
                    )
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                CalendarUiState(selectedDate.value)
            )

    fun onDateSelected(date: String) {
        selectedDate.value = date
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }

    fun updateTaskStatus(task: Task, isCompleted: Boolean) {
        viewModelScope.launch {
            val updatedStatus = if (isCompleted) {
                com.example.taskly.domain.model.TaskStatus.COMPLETED
            } else {
                com.example.taskly.domain.model.TaskStatus.IN_PROGRESS
            }

            taskRepository.updateTask(
                task.copy(status = updatedStatus)
            )
        }
    }



}
