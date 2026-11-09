package com.PPBM.lentera.backend.auth.repository

import com.PPBM.lentera.backend.core.SupabaseConfig
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

sealed interface RecoveryDeepLinkResult {
    data class TokenSession(
        val fragment: String,
        val type: String
    ) : RecoveryDeepLinkResult

    data class Error(
        val code: String,
        val message: String
    ) : RecoveryDeepLinkResult
}

object RecoveryDeepLinkParser {

    fun isRecoveryUri(scheme: String?, host: String?): Boolean {
        return scheme.equals(SupabaseConfig.DEEP_LINK_SCHEME, ignoreCase = true) &&
            host.equals(SupabaseConfig.DEEP_LINK_HOST, ignoreCase = true)
    }

    /**
     * Mem-parsing URI deep link pemulihan (`lentera://reset-password#...`).
     * Mengembalikan null bila skema atau host bukan target deep link auth Lentera.
     */
    fun parse(
        scheme: String?,
        host: String?,
        encodedQuery: String?,
        encodedFragment: String?
    ): RecoveryDeepLinkResult? {
        if (!isRecoveryUri(scheme, host)) {
            return null
        }

        val cleanQuery = encodedQuery?.trim()?.removePrefix("?")
        val cleanFragment = encodedFragment?.trim()?.removePrefix("#")
        val queryParams = parseFormParams(cleanQuery)
        val fragmentParams = parseFormParams(cleanFragment)
        val merged = queryParams + fragmentParams

        val errorCode = merged["error_code"].orEmpty().ifBlank { merged["error"].orEmpty() }
        val errorDesc = merged["error_description"].orEmpty()
        if (errorCode.isNotBlank() || errorDesc.isNotBlank()) {
            return RecoveryDeepLinkResult.Error(
                code = errorCode.ifBlank { "auth_link_error" },
                message = mapDeepLinkErrorMessage(errorCode, errorDesc)
            )
        }

        val sanitizedFragment = cleanFragment
            ?.split("&")
            ?.filter { it.isNotBlank() && it.contains('=') }
            ?.joinToString("&")
            .orEmpty()
        val accessToken = fragmentParams["access_token"].orEmpty()
        val refreshToken = fragmentParams["refresh_token"].orEmpty()
        val expiresIn = fragmentParams["expires_in"]?.toLongOrNull()
        val tokenType = fragmentParams["token_type"].orEmpty()

        if (accessToken.isBlank() || refreshToken.isBlank() || expiresIn == null || tokenType.isBlank()) {
            return RecoveryDeepLinkResult.Error(
                code = "invalid_or_incomplete_link",
                message = "Tautan pemulihan tidak lengkap atau sudah kedaluwarsa. Minta admin mengirim ulang tautan baru."
            )
        }

        val type = fragmentParams["type"].orEmpty()
        if (type.isNotBlank() &&
            !type.equals("recovery", ignoreCase = true) &&
            !type.equals("magiclink", ignoreCase = true) &&
            !type.equals("invite", ignoreCase = true)
        ) {
            return RecoveryDeepLinkResult.Error(
                code = "unsupported_link_type",
                message = "Jenis tautan autentikasi ($type) tidak didukung untuk reset password."
            )
        }

        return RecoveryDeepLinkResult.TokenSession(
            fragment = sanitizedFragment,
            type = type
        )
    }

    internal fun parseFormParams(raw: String?): Map<String, String> {
        if (raw.isNullOrBlank()) return emptyMap()
        val result = linkedMapOf<String, String>()
        for (pair in raw.split("&")) {
            if (pair.isBlank()) continue
            val idx = pair.indexOf('=')
            val rawKey = if (idx >= 0) pair.substring(0, idx) else pair
            val rawVal = if (idx >= 0) pair.substring(idx + 1) else ""
            val key = decodeComponent(rawKey)
            val value = decodeComponent(rawVal)
            if (key.isNotBlank()) {
                result[key] = value
            }
        }
        return result
    }

    private fun decodeComponent(value: String): String {
        return runCatching {
            URLDecoder.decode(value, StandardCharsets.UTF_8.name())
        }.getOrDefault(value)
    }

    private fun mapDeepLinkErrorMessage(errorCode: String, description: String): String {
        val combined = "$errorCode $description".lowercase()
        return when {
            combined.contains("otp_expired") ||
                combined.contains("expired") ||
                combined.contains("invalid") ->
                "Tautan aktivasi atau reset password sudah kedaluwarsa atau pernah digunakan. Minta admin mengirim ulang tautan baru."
            combined.contains("access_denied") ->
                "Tautan aktivasi ditolak atau tidak berlaku lagi. Minta admin mengirim ulang tautan baru."
            description.isNotBlank() -> description
            else -> "Tautan pemulihan tidak dapat diproses. Silakan minta tautan baru."
        }
    }
}
