package com.example.eventshub.util

import android.content.SharedPreferences
import androidx.core.content.edit

fun logout(preferences: SharedPreferences) {
    preferences.edit() { clear() }
}
