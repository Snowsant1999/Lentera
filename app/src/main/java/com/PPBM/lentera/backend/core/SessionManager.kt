package com.PPBM.lentera.backend.core

import android.content.Context
import android.content.SharedPreferences
import com.PPBM.lentera.backend.auth.model.UserProfile
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Menyimpan sesi login lokal secara persisten di SharedPreferences.
 */
class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private const val PREF_NAME = "lentera_session_prefs"
        private const val KEY_USER_PROFILE = "key_user_profile"
        private const val KEY_IS_LOGGED_IN = "key_is_logged_in"

        @Volatile
        private var instance: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return instance ?: synchronized(this) {
                instance ?: SessionManager(context.applicationContext).also { instance = it }
            }
        }
    }

    fun saveSession(profile: UserProfile) {
        val profileJson = json.encodeToString(profile)
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_PROFILE, profileJson)
            .apply()
    }

    fun getProfile(): UserProfile? {
        val profileJson = prefs.getString(KEY_USER_PROFILE, null) ?: return null
        return try {
            json.decodeFromString<UserProfile>(profileJson)
        } catch (_: Exception) {
            null
        }
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false) && getProfile() != null
    }

    fun getRole(): String? {
        return getProfile()?.role
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}

