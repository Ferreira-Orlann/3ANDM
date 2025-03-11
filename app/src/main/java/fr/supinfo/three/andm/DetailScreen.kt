package fr.supinfo.three.andm

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.layout.ContentScale

@Composable
fun DetailScreen(recipeId: Int, onBack: () -> Unit, paddingValues: Recipe) {
    var recipe by remember { mutableStateOf<Recipe?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Effet pour charger la recette
    LaunchedEffect(recipeId) {
        isLoading = true
        try {
            // Récupérer toutes les recettes et trouver celle correspondant à l'ID
            val recipes = RecipeApi().searchRecipes("")
            recipe = recipes.find { it.pk == recipeId }
        } catch (e: Exception) {
            println("Erreur lors de la récupération de la recette: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    // Affichez un message de chargement tant que la recette n'est pas disponible
    if (isLoading) {
        Scaffold { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                Text("Chargement...")
            }
        }
    } else {
        // Une fois que la recette est chargée, affichez les détails
        recipe?.let {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(it.title) },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    Image(
                        painter = rememberImagePainter(it.featured_image),
                        contentDescription = it.title,
                        modifier = Modifier
                            .height(250.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Ingrédients :", style = MaterialTheme.typography.h6)
                    it.ingredients.forEach { ingredient ->
                        Text(text = "• $ingredient", style = MaterialTheme.typography.body1)
                    }
                }
            }
        } ?: run {
            // Si la recette n'est pas trouvée
            Scaffold { paddingValues ->
                Column(modifier = Modifier.padding(paddingValues)) {
                    Text("Recette non trouvée.")
                }
            }
        }
    }
}

