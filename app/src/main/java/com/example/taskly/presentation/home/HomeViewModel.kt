package com.example.taskly.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskly.domain.model.Task
import com.example.taskly.domain.model.TaskStatus
import com.example.taskly.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class TaskFilter {
    ALL,
    IN_PROGRESS,
    COMPLETED,
    OVERDUE
}
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    private val _allTasks = MutableStateFlow<List<Task>>(emptyList())
    private val _searchQuery = MutableStateFlow("")

    val searchQuery: StateFlow<String> =
        _searchQuery.asStateFlow()
    private val _selectedFilter =
        MutableStateFlow(TaskFilter.ALL)

    val selectedFilter: StateFlow<TaskFilter> =
        _selectedFilter.asStateFlow()
    private val _isLoading = MutableStateFlow(true)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()

    val tasks: StateFlow<List<Task>> = combine(
        _allTasks,
        _searchQuery,
        _selectedFilter
    ) { allTasks, query, filter ->

        allTasks.applyFilters(
            searchQuery = query,
            selectedFilter = filter
        )

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    init {
        observeTasks()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onFilterSelected(filter: TaskFilter) {
        _selectedFilter.value = filter
    }

    fun onDeleteTaskClicked(task: Task) {
        executeTaskAction {
            taskRepository.deleteTask(task)
        }
    }

    fun onTaskStatusChanged(
        task: Task,
        isCompleted: Boolean
    ) {
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
    private fun observeTasks() {
        viewModelScope.launch {
            taskRepository.getAllTasks().collect { tasks ->
                _allTasks.value = tasks
                _isLoading.value = false
            }
        }
    }
    private fun List<Task>.applyFilters(
        searchQuery: String,
        selectedFilter: TaskFilter
    ): List<Task> {

        val today = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        ).format(Date())

        val query = searchQuery.trim()

        return filter { task ->

            val matchesSearch =
                query.isBlank() ||
                        task.title.contains(
                            other = query,
                            ignoreCase = true
                        ) ||
                        task.description.contains(
                            other = query,
                            ignoreCase = true
                        )

            val matchesFilter = when (selectedFilter) {

                TaskFilter.ALL -> true

                TaskFilter.IN_PROGRESS ->
                    task.status == TaskStatus.IN_PROGRESS &&
                            task.dueDate >= today

                TaskFilter.COMPLETED ->
                    task.status == TaskStatus.COMPLETED

                TaskFilter.OVERDUE ->
                    task.status == TaskStatus.IN_PROGRESS &&
                            task.dueDate < today
            }

            matchesSearch && matchesFilter
        }
    }
    private fun executeTaskAction(
        action: suspend () -> Unit
    ) {
        viewModelScope.launch {
            action()
        }
    }
}
