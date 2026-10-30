package com.PPBM.lentera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.PPBM.lentera.features.siaga.AdminSiagaFlowScreen
import com.PPBM.lentera.ui.theme.LenteraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LenteraTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AdminSiagaFlowScreen()
                }
            }
        }
    }
}