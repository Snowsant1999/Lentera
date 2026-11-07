package com.PPBM.lentera.backend.core

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

/**
 * Konfigurasi singleton Supabase Client untuk aplikasi Lentera.
 * Terhubung ke project Lentera_dev (zrzypkduulwtkumqkubu).
 */
object SupabaseConfig {
    const val SUPABASE_URL = "https://zrzypkduulwtkumqkubu.supabase.co"
    const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inpyenlwa2R1dWx3dGt1bXFrdWJ1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTIzODQ5MjEsImV4cCI6MjEwNzk2MDkyMX0.vAFC8FWnTGpNjs5zQDi6X6mkTuj5eh-roor5JTqr3bI"

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
        }
    }
}

