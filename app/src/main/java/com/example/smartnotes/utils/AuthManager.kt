package com.example.smartnotes.utils

import android.content.Context
import android.content.SharedPreferences

object AuthManager {

    private const val PREFS_NAME = "SmartNotesAuth"
    private const val KEY_IS_LOGGED_IN = "isLoggedIn"
    private const val KEY_USER_EMAIL = "userEmail"
    private const val KEY_USER_NAME = "userName"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isLoggedIn(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun login(context: Context, email: String, name: String) {
        getPrefs(context).edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_NAME, name)
            apply()
        }
    }

    fun logout(context: Context) {
        getPrefs(context).edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, false)
            remove(KEY_USER_EMAIL)
            remove(KEY_USER_NAME)
            apply()
        }
    }

    fun getUserEmail(context: Context): String? {
        return getPrefs(context).getString(KEY_USER_EMAIL, null)
    }

    fun getUserName(context: Context): String? {
        return getPrefs(context).getString(KEY_USER_NAME, null)
    }

    // Simple validation - for demo purposes
    // In real app, you'd validate against a backend
    fun validateCredentials(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.length >= 6
    }
}