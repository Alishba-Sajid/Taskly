package com.example.taskly.core.cache

import androidx.datastore.preferences.core.booleanPreferencesKey

object Preferences {

    val ONBOARDING_COMPLETED =
        booleanPreferencesKey("onboarding_completed")
}