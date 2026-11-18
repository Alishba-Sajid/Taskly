package com.example.taskly.presentation.addtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskly.R
import com.example.taskly.domain.model.Task
import com.example.taskly.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _selectedDueDate = MutableStateFlow<String?>(null)
    val selectedDueDate = _selectedDueDate.asStateFlow()

    private val _dueDateText = MutableStateFlow("")
    val dueDateText = _dueDateText.asStateFlow()

    private val _titleError = MutableStateFlow<Int?>(null)
    val titleError = _titleError.asStateFlow()

    private val _dueDateError = MutableStateFlow<Int?>(null)
    val dueDateError = _dueDateError.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    private val _events = MutableSharedFlow<AddTaskEvent>(
        replay = 0,
        extraBufferCapacity = 1
    )

    val events = _events.asSharedFlow()

    fun onTitleChanged(title: String) {
        _title.value = title
        _titleError.value = null
    }

    fun onDescriptionChanged(description: String) {
        _description.value = description
    }

    fun onDueDateClicked() {
        if (_isSaving.value) return

        _events.tryEmit(AddTaskEvent.ShowDatePicker)
    }

    fun onDueDateSelected(dueDate: String, displayDate: String) {
        _selectedDueDate.value = dueDate
        _dueDateText.value = displayDate
        _dueDateError.value = null
    }

    fun onSaveClicked() {
        if (_isSaving.value) return

        if (!validateTaskForm()) return

        saveTask()
    }

    fun onBackClicked() {
        if (_isSaving.value) return

        _events.tryEmit(AddTaskEvent.NavigateBack)
    }

    private fun validateTaskForm(): Boolean {
        val validTitle = _title.value.trim()
        val validDueDate = _selectedDueDate.value

        if (validTitle.isBlank()) {
            _titleError.value = R.string.task_title_required
            return false
        }

        if (validDueDate.isNullOrBlank()) {
            _dueDateError.value = R.string.task_due_date_required
            return false
        }

        return true
    }

    private fun saveTask() {
        viewModelScope.launch {
            _isSaving.value = true

            try {
                taskRepository.insertTask(
                    Task(
                        title = _title.value.trim(),
                        description = _description.value.trim(),
                        dueDate = _selectedDueDate.value.orEmpty()
                    )
                )

                _events.emit(AddTaskEvent.NavigateBack)
            } finally {
                _isSaving.value = false
            }
        }
    }
}

sealed interface AddTaskEvent {

    data object NavigateBack : AddTaskEvent
    
    data object ShowDatePicker : AddTaskEvent
}

