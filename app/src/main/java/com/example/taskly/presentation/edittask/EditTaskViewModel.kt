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
sealed class EditTaskEffect {
    object NavigateBack : EditTaskEffect()
    object ShowTitleError : EditTaskEffect()
    object ShowDueDateError : EditTaskEffect()
    object TaskNotFound : EditTaskEffect()
}
@HiltViewModel
class EditTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    private val _task = MutableStateFlow<Task?>(null)
    val task = _task.asStateFlow()
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()
    private val _effects = MutableSharedFlow<EditTaskEffect>()
    val effects = _effects.asSharedFlow()
    fun loadTask(taskId: Int) {
        viewModelScope.launch {
            val loadedTask = taskRepository.getTaskById(taskId)

            _task.value = loadedTask
            _isLoading.value = false

            if (loadedTask == null) {
                _effects.emit(EditTaskEffect.TaskNotFound)
            }
        }
    }

    fun updateTask(
        title: String,
        description: String,
        dueDate: String?
    ) {
        val existingTask = _task.value ?: return

        if (title.isBlank()) {
            showEffect(EditTaskEffect.ShowTitleError)
            return
        }

        val validDueDate = dueDate?.trim()

        if (validDueDate.isNullOrBlank()) {
            showEffect(EditTaskEffect.ShowDueDateError)
            return
        }

        executeTaskAction {
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
    private fun showEffect(effect: EditTaskEffect) {
        viewModelScope.launch {
            _effects.emit(effect)
        }
    }
    private fun executeTaskAction(action: suspend () -> Unit) {
        viewModelScope.launch {
            action()
        }
    }

}
