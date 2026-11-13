package com.example.taskly.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OnboardingPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private val onboardingCompletedKey =
        booleanPreferencesKey("onboarding_completed")

    val isOnboardingCompleted: Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[onboardingCompletedKey] ?: false
        }

    suspend fun setOnboardingCompleted() {
        dataStore.edit { preferences ->
            preferences[onboardingCompletedKey] = true
        }
    }
}

