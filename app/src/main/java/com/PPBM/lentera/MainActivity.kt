package com.PPBM.lentera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.PPBM.lentera.core.designsystem.theme.LenteraTheme
import com.PPBM.lentera.core.navigation.LenteraNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LenteraTheme {
                val navController = rememberNavController()
                LenteraNavGraph(navController = navController)
            }
        }
    }
}
