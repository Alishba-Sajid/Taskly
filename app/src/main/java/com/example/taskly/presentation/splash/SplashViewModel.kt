package com.example.taskly.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskly.data.repository.OnboardingPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val onboardingPreferencesRepository: OnboardingPreferencesRepository
) : ViewModel() {
    private val _navigationEvents = MutableSharedFlow<Boolean>()
    val navigationEvents = _navigationEvents.asSharedFlow()
    fun startSplash() {
        viewModelScope.launch {
            delay(1500L.milliseconds)

            val isOnboardingCompleted =
                onboardingPreferencesRepository.isOnboardingCompleted.first()

            _navigationEvents.emit(isOnboardingCompleted)
        }
    }
}
