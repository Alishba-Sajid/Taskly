package com.example.taskly.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {

    fun startSplash(onSplashFinished: () -> Unit) {
        viewModelScope.launch {
            delay(1500.milliseconds)
            onSplashFinished()
        }
    }
}