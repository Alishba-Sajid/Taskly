package com.example.taskly.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskly.domain.model.Task
import com.example.taskly.domain.model.TaskStatus
import com.example.taskly.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.launch


enum class TaskFilter {
    ALL,
    PENDING,
    IN_PROGRESS,
    COMPLETED
}

data class HomeUiState(
    val tasks: List<Task> = emptyList(),
    val selectedFilter: TaskFilter = TaskFilter.ALL,
    val searchQuery: String = "",
    val isLoading: Boolean = true
)

sealed class HomeEvent {
    data class SearchQueryChanged(val query: String) : HomeEvent()
    data class FilterSelected(val filter: TaskFilter) : HomeEvent()

    data class DeleteTaskClicked(val task: Task) : HomeEvent()
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val selectedFilter = MutableStateFlow(TaskFilter.ALL)

    val uiState: StateFlow<HomeUiState> = combine(
        searchQuery.debounce(300.milliseconds).distinctUntilChanged(),
        selectedFilter
    ) { query, filter ->
        query to filter
    }.flatMapLatest { (query, filter) ->

        val taskFlow = if (query.isBlank()) {
            taskRepository.getAllTasks()
        } else {
            taskRepository.searchTasks(query)
        }

        taskFlow.map { tasks ->
            val filteredTasks = when (filter) {
                TaskFilter.ALL -> tasks
                TaskFilter.PENDING ->
                    tasks.filter { it.status == TaskStatus.PENDING }

                TaskFilter.IN_PROGRESS ->
                    tasks.filter { it.status == TaskStatus.IN_PROGRESS }

                TaskFilter.COMPLETED ->
                    tasks.filter { it.status == TaskStatus.COMPLETED }
            }

            HomeUiState(
                tasks = filteredTasks,
                selectedFilter = filter,
                searchQuery = query,
                isLoading = false
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.SearchQueryChanged -> {
                searchQuery.value = event.query
            }

            is HomeEvent.FilterSelected -> {
                selectedFilter.value = event.filter
            }

            is HomeEvent.DeleteTaskClicked -> {
                viewModelScope.launch {
                    taskRepository.deleteTask(event.task)
                }
            }

        }
    }
}
