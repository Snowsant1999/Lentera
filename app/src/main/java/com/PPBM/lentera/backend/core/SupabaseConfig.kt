package com.PPBM.lentera.backend.core

import com.PPBM.lentera.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

/**
 * Konfigurasi singleton Supabase Client untuk aplikasi Lentera.
 * URL dan API Key dibaca dari BuildConfig.
 */
object SupabaseConfig {
    const val DEEP_LINK_SCHEME = "lentera"
    const val DEEP_LINK_HOST = "reset-password"
    const val RESET_PASSWORD_REDIRECT_URL = "$DEEP_LINK_SCHEME://$DEEP_LINK_HOST"

    val SUPABASE_URL: String = BuildConfig.SUPABASE_URL
    val SUPABASE_KEY: String = BuildConfig.SUPABASE_KEY

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY
        ) {
            install(Auth) {
                scheme = DEEP_LINK_SCHEME
                host = DEEP_LINK_HOST
                // Recovery dikirim admin/lintas konteks tanpa code_verifier perangkat penerima
                flowType = FlowType.IMPLICIT
            }
            install(Postgrest)
            install(Storage)
        }
    }
}
