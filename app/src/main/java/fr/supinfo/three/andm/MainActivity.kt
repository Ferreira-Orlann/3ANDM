package fr.supinfo.three.andm

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import fr.supinfo.three.andm.persistance.RecipeDatabase
import fr.supinfo.three.andm.ui.theme.ProjetFinModuleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = RecipeDatabase.getInstance(applicationContext)
        Log.d("MainActivity", "Database instance created: $database")

        setContent {
            ProjetFinModuleTheme {
                RecipeApp(database)
            }
        }
    }
}