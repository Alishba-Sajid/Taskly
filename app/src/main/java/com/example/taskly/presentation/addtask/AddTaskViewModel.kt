package com.example.taskly.presentation.addtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskly.domain.model.Task
import com.example.taskly.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed class AddTaskEffect {
    object NavigateBack : AddTaskEffect()
    object ShowTitleError : AddTaskEffect()
    object ShowDueDateError : AddTaskEffect()
}
@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    private val _effects = MutableSharedFlow<AddTaskEffect>()
    val effects = _effects.asSharedFlow()
    fun addTask(title: String, description: String, dueDate: String?) {

        if (title.isBlank()) {
            viewModelScope.launch {
                _effects.emit(AddTaskEffect.ShowTitleError)
            }
            return
        }

        val validDueDate = dueDate?.trim()

        if (validDueDate.isNullOrBlank()) {
            viewModelScope.launch {
                _effects.emit(AddTaskEffect.ShowDueDateError)
            }
            return
        }

        viewModelScope.launch {
            taskRepository.insertTask(
                Task(
                    title = title.trim(),
                    description = description.trim(),
                    dueDate = validDueDate
                )
            )

            _effects.emit(AddTaskEffect.NavigateBack)
        }
    }
}