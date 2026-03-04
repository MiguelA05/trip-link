package com.example.triplink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.triplink.features.login.LoginScreen
import com.example.triplink.ui.theme.DescubreuqTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DescubreuqTheme {
                LoginScreen(
                    onNavigateToUsers = {
                        // TODO: Implement navigation to Users screen
                    }
                )
            }
        }
    }
}
