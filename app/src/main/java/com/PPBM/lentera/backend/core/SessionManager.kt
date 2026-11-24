package com.PPBM.lentera.backend.core

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import com.PPBM.lentera.backend.auth.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

/**
 * Menyimpan sesi login lokal secara persisten di SharedPreferences,
 * mengelola identitas unik perangkat (device_id), dan heartbeat sesi aktif (C1).
 */
class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }
    private var heartbeatJob: Job? = null
    private val _sessionConflictMessage = MutableStateFlow<String?>(null)
    val sessionConflictMessage: StateFlow<String?> = _sessionConflictMessage.asStateFlow()

    @Volatile
    var isStartupReady: Boolean = false
        private set

    private val _startupReadyFlow = MutableStateFlow(false)
    val startupReadyFlow: StateFlow<Boolean> = _startupReadyFlow.asStateFlow()

    companion object {
        private const val PREF_NAME = "lentera_session_prefs"
        private const val KEY_USER_PROFILE = "key_user_profile"
        private const val KEY_IS_LOGGED_IN = "key_is_logged_in"
        private const val KEY_DEVICE_ID = "key_device_id"

        @Volatile
        private var instance: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return instance ?: synchronized(this) {
                instance ?: SessionManager(context.applicationContext).also { instance = it }
            }
        }
    }

    /**
     * Mengambil ID perangkat persisten atau membuat UUID baru (C1).
     */
    fun getDeviceId(): String {
        val existing = prefs.getString(KEY_DEVICE_ID, null)
        if (!existing.isNullOrBlank()) return existing
        val newId = UUID.randomUUID().toString()
        prefs.edit().putString(KEY_DEVICE_ID, newId).apply()
        return newId
    }

    /**
     * Informasi merek dan tipe HP untuk pencatatan sesi aktif.
     */
    fun getDeviceInfo(): String {
        return "${Build.MANUFACTURER.replaceFirstChar { it.uppercase() }} ${Build.MODEL}".trim()
    }

    fun saveSession(profile: UserProfile) {
        clearSessionConflict()
        val profileJson = json.encodeToString(profile)
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_PROFILE, profileJson)
            .apply()
        markStartupReady()
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

    fun notifySessionConflict(message: String) {
        _sessionConflictMessage.value = message
    }

    fun clearSessionConflict() {
        _sessionConflictMessage.value = null
    }

    /**
     * Memulai pengiriman detak jantung sesi (heartbeat) setiap 60 detik (C1).
     */
    fun startHeartbeat(
        scope: CoroutineScope,
        immediateCheck: Boolean = true,
        onDetak: suspend (deviceId: String) -> Pair<Boolean, String?>
    ) {
        stopHeartbeat()
        heartbeatJob = scope.launch {
            if (!immediateCheck) delay(60_000L)
            while (isActive) {
                val deviceId = getDeviceId()
                val (isStillValid, conflictMessage) = onDetak(deviceId)
                if (!isStillValid) {
                    clearSession()
                    notifySessionConflict(
                        conflictMessage?.takeIf { it.isNotBlank() }
                            ?: "Akun Anda sedang digunakan di perangkat lain. Anda telah dikeluarkan dari sesi ini."
                    )
                    break
                }
                delay(60_000L) // 60 detik
            }
        }
    }

    fun resetStartupReady() {
        isStartupReady = false
        _startupReadyFlow.value = false
    }

    fun markStartupReady() {
        isStartupReady = true
        _startupReadyFlow.value = true
    }

    fun isHeartbeatRunning(): Boolean = heartbeatJob?.isActive == true

    fun stopHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = null
    }

    fun clearSession() {
        stopHeartbeat()
        resetStartupReady()
        val deviceId = getDeviceId() // Simpan deviceId agar tidak hilang
        prefs.edit().clear().apply()
        prefs.edit().putString(KEY_DEVICE_ID, deviceId).apply()
    }
}
