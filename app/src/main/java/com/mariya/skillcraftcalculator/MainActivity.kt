package com.mariya.skillcraftcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.mariya.skillcraftcalculator.ui.screen.CalculatorScreen
import com.mariya.skillcraftcalculator.ui.theme.SkillCraftCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            SkillCraftCalculatorTheme {
                CalculatorScreen()
            }
        }
    }
}