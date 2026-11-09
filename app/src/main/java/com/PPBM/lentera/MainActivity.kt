package com.PPBM.lentera

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.PPBM.lentera.backend.auth.repository.RecoveryDeepLinkParser
import com.PPBM.lentera.core.designsystem.theme.LenteraTheme
import com.PPBM.lentera.core.navigation.LenteraNavGraph

class MainActivity : ComponentActivity() {
    private var pendingRecoveryUri by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (savedInstanceState == null) {
            pendingRecoveryUri = extractRecoveryUri(intent)
        }
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
