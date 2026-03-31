package com.example.triplink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.triplink.core.components.PublicationList
import com.example.triplink.core.navigation.AppNavigation
import com.example.triplink.features.login.LoginScreen
import com.example.triplink.features.recoverypassword.RecoveryPasswordScreen
import com.example.triplink.features.resetpassword.ResetPasswordScreen
import com.example.triplink.ui.theme.DescubreuqTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DescubreuqTheme {
                AppNavigation()

            }

        }
    }
}
