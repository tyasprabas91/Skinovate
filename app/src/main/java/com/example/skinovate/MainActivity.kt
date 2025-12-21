package com.example.skinovate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.skinovate.ui.theme.SkinovateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkinovateTheme {
                // Calls the App Hub we made in File 4
                SkinovateApp()
            }
        }
    }
}