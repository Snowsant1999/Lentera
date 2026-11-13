package com.PPBM.lentera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.PPBM.lentera.backend.auth.repository.AuthRepository
import com.PPBM.lentera.backend.auth.repository.RecoveryDeepLinkParser
import com.PPBM.lentera.backend.core.SessionManager
import com.PPBM.lentera.core.designsystem.theme.LenteraTheme
import com.PPBM.lentera.core.navigation.LenteraNavGraph
import com.PPBM.lentera.core.notification.LenteraNotificationHelper

class MainActivity : ComponentActivity() {
    private var pendingRecoveryUri by mutableStateOf<Uri?>(null)

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Menyerahkan null agar Android tidak memulihkan bundle navigasi lama saat dibuka kembali dari Recent Apps
        super.onCreate(null)
        enableEdgeToEdge()
        // N1.1: Pastikan channel tetap lentera_alerts_v1 dibuat saat aplikasi dibuka & channel usang dibersihkan
        LenteraNotificationHelper.createNotificationChannel(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        pendingRecoveryUri = extractRecoveryUri(intent)
        setContent {
            LenteraTheme {
                val navController = rememberNavController()
                LenteraNavGraph(
                    navController = navController,
                    pendingRecoveryUri = pendingRecoveryUri,
                    onRecoveryUriConsumed = {
                        pendingRecoveryUri = null
                        val currentIntent = intent
                        if (currentIntent != null) {
                            setIntent(Intent(currentIntent).apply { data = null })
                        }
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // C1: Jalankan detak sesi aktif (heartbeat) tiap 60 detik selama aplikasi aktif
        val sessionManager = SessionManager.getInstance(this)
        if (sessionManager.isLoggedIn()) {
            val authRepo = AuthRepository(this)
            sessionManager.startHeartbeat(lifecycleScope) {
                authRepo.sendHeartbeat()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // C1: Hentikan detak saat aplikasi masuk ke background
        SessionManager.getInstance(this).stopHeartbeat()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        extractRecoveryUri(intent)?.let { uri ->
            pendingRecoveryUri = uri
        }
    }

    private fun extractRecoveryUri(intent: Intent?): Uri? {
        if (intent == null) return null
        if ((intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0) return null
        val data = intent.data ?: return null
        return data.takeIf { RecoveryDeepLinkParser.isRecoveryUri(it.scheme, it.host) }
    }
}
