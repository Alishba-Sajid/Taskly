
package com.example.taskly.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val currentPage: Int = 0,
    val title: String = "Welcome to Taskly",
    val description: String = "A simple space to plan your day and stay on track.",
    val buttonText: String = "Continue"
)

sealed class OnboardingEvent {
    object ContinueClicked : OnboardingEvent()
}

sealed class OnboardingEffect {
    object NavigateToHome : OnboardingEffect()
}

@HiltViewModel
class OnboardingViewModel @Inject constructor() : ViewModel() {

    private val pages = listOf(
        OnboardingUiState(
            currentPage = 0,
            title = "Welcome to Taskly",
            description = "A simple space to plan your day and stay on track.",
            buttonText = "Continue"
        ),
        OnboardingUiState(
            currentPage = 1,
            title = "Everything in One Place",
            description = "Add and edit tasks, assign dates, and track your progress easily.",
            buttonText = "Continue"
        ),
        OnboardingUiState(
            currentPage = 2,
            title = "Make Every Day Count",
            description = "Organize your tasks and stay focused on what matters.",
            buttonText = "Get Started"
        )
    )

    private val _uiState = MutableStateFlow(pages[0])
    val uiState: StateFlow<OnboardingUiState> = _uiState

    private val _effects = MutableSharedFlow<OnboardingEffect>()
    val effects = _effects.asSharedFlow()

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            OnboardingEvent.ContinueClicked -> {
                val nextPage = _uiState.value.currentPage + 1

                if (nextPage < pages.size) {
                    _uiState.value = pages[nextPage]
                } else {
                    viewModelScope.launch {
                        _effects.emit(OnboardingEffect.NavigateToHome)
                    }
                }
            }
        }
    }
}
