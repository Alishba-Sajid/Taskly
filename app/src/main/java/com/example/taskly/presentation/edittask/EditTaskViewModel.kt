package com.example.taskly.presentation.edittask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskly.domain.model.Task
import com.example.taskly.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
data class EditTaskUiState(
    val task: Task? = null,
    val isLoading: Boolean = true
)sealed class EditTaskEffect {
    object NavigateBack : EditTaskEffect()
    object ShowTitleError : EditTaskEffect()
    object ShowDueDateError : EditTaskEffect()
    object TaskNotFound : EditTaskEffect()
}
@HiltViewModel
class EditTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditTaskUiState())
    val uiState = _uiState.asStateFlow()
    private val _effects = MutableSharedFlow<EditTaskEffect>()
    val effects = _effects.asSharedFlow()

    fun loadTask(taskId: Int) {
        viewModelScope.launch {
            val task = taskRepository.getTaskById(taskId)

            _uiState.value = EditTaskUiState(
                task = task,
                isLoading = false
            )

            if (task == null) {
                _effects.emit(EditTaskEffect.TaskNotFound)
            }
        }
    }

    fun updateTask(
        title: String,
        description: String,
        dueDate: String?
    ) {
        val existingTask = _uiState.value.task ?: return

        if (title.isBlank()) {
            viewModelScope.launch {
                _effects.emit(EditTaskEffect.ShowTitleError)
            }
            return
        }

        val validDueDate = dueDate?.trim()

        if (validDueDate.isNullOrBlank()) {
            viewModelScope.launch {
                _effects.emit(EditTaskEffect.ShowDueDateError)
            }
            return
        }

        viewModelScope.launch {
            taskRepository.updateTask(
                existingTask.copy(
                    title = title.trim(),
                    description = description.trim(),
                    dueDate = validDueDate
                )
            )

            _effects.emit(EditTaskEffect.NavigateBack)
        }
    }
}
