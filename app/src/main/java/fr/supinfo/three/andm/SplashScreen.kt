package fr.supinfo.three.andm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.*
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(4000) // Attente de 4 secondes
        navController.navigate("listScreen") { // Naviguer vers la liste après l'attente
            popUpTo("splashScreen") { inclusive = true } // Retirer l'écran de splash de la pile
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.food),
            contentDescription = "food",
            modifier = Modifier.fillMaxSize(), // L'image occupe toute la taille de l'écran
            contentScale = ContentScale.Crop // Pour que l'image couvre tout l'écran tout en maintenant ses proportions
        )
    }
}

