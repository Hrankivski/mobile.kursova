package com.example.kursova

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.kursova.ui.navigation.AppNavGraph
import com.example.kursova.ui.theme.EvChargingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EvChargingTheme(){
                AppNavGraph()
            }
        }
    }
}
