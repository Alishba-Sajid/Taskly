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
}

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _effects = MutableSharedFlow<AddTaskEffect>()
    val effects = _effects.asSharedFlow()

    fun addTask(title: String, description: String) {
        if (title.isBlank()) {
            viewModelScope.launch {
                _effects.emit(AddTaskEffect.ShowTitleError)
            }
            return
        }

        viewModelScope.launch {
            taskRepository.insertTask(
                Task(
                    title = title.trim(),
                    description = description.trim()
                )
            )

            _effects.emit(AddTaskEffect.NavigateBack)
        }
    }
}
