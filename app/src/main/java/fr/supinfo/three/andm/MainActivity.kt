package fr.supinfo.three.andm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import fr.supinfo.three.andm.ui.theme.ProjetFinModuleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjetFinModuleTheme {
                RecipeApp()
            }
        }
    }
}