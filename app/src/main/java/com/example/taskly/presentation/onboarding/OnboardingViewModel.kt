package com.example.taskly.presentation.onboarding

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskly.R
import com.example.taskly.data.repository.OnboardingPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OnboardingPage(
    @param:StringRes val titleResId: Int,
    @param:StringRes val descriptionResId: Int,
    @param:StringRes val illustrationResId: Int
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingPreferencesRepository: OnboardingPreferencesRepository
) : ViewModel() {

    private val pages = listOf(
        OnboardingPage(
            titleResId = R.string.onboarding_page_1_title,
            descriptionResId = R.string.onboarding_page_1_description,
            illustrationResId = R.string.onboarding_page_1_illustration
        ),
        OnboardingPage(
            titleResId = R.string.onboarding_page_2_title,
            descriptionResId = R.string.onboarding_page_2_description,
            illustrationResId = R.string.onboarding_page_2_illustration
        ),
        OnboardingPage(
            titleResId = R.string.onboarding_page_3_title,
            descriptionResId = R.string.onboarding_page_3_description,
            illustrationResId = R.string.onboarding_page_3_illustration
        )
    )

    private val _currentPageIndex = MutableStateFlow(0)
    val currentPageIndex: StateFlow<Int> = _currentPageIndex.asStateFlow()

    private val _titleResId = MutableStateFlow(pages.first().titleResId)
    val titleResId: StateFlow<Int> = _titleResId.asStateFlow()

    private val _descriptionResId =
        MutableStateFlow(pages.first().descriptionResId)
    val descriptionResId: StateFlow<Int> = _descriptionResId.asStateFlow()

    private val _illustrationResId =
        MutableStateFlow(pages.first().illustrationResId)
    val illustrationResId: StateFlow<Int> = _illustrationResId.asStateFlow()

    private val _buttonTextResId =
        MutableStateFlow(R.string.onboarding_button_continue)
    val buttonTextResId: StateFlow<Int> = _buttonTextResId.asStateFlow()

    val totalPages: Int = pages.size

    private val _navigationEvents = MutableSharedFlow<Unit>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    fun onContinueClicked() {
        val nextPageIndex = _currentPageIndex.value + 1

        if (nextPageIndex < pages.size) {
            updatePage(nextPageIndex)
        } else {
            completeOnboarding()
        }
    }

    private fun updatePage(pageIndex: Int) {
        val page = pages[pageIndex]

        _currentPageIndex.value = pageIndex
        _titleResId.value = page.titleResId
        _descriptionResId.value = page.descriptionResId
        _illustrationResId.value = page.illustrationResId

        _buttonTextResId.value = if (pageIndex == pages.lastIndex) {
            R.string.onboarding_button_get_started
        } else {
            R.string.onboarding_button_continue
        }
    }
    
    private fun completeOnboarding() {
        viewModelScope.launch {
            onboardingPreferencesRepository.setOnboardingCompleted()
            _navigationEvents.emit(Unit)
        }
    }
}
