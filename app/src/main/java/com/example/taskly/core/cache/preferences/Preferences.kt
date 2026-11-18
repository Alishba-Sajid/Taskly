package com.example.taskly.core.cache.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
object Preferences {
    val ONBOARDING_COMPLETED =
        booleanPreferencesKey("onboarding_completed")
}