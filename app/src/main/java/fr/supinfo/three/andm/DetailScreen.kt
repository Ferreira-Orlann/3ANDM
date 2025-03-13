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
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import fr.supinfo.three.andm.ui.theme.AnzacColor

@Composable
fun DetailScreen(recipeId: Int, onBack: () -> Unit, paddingValues: Recipe, recipeApi: RecipeApi) {
    var recipe by remember { mutableStateOf<RecipeDetail?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(recipeId) {
        isLoading = true
        try {
            recipe = recipeApi.getRecipeById(recipeId)
        } catch (e: Exception) {
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        Scaffold { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.food),
                        contentDescription = "Chargement",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    } else {
        recipe?.let {
            Scaffold(
                topBar = {
                    Column {
                        Spacer(modifier = Modifier.height(0.dp))
                        TopAppBar(
                            title = { Text(it.title, color = MaterialTheme.colors.onPrimary) },
                            navigationIcon = {
                                IconButton(onClick = onBack) {
                                    Icon(
                                        Icons.Default.ArrowBack,
                                        contentDescription = "Retour",
                                        tint = MaterialTheme.colors.onPrimary
                                    )
                                }
                            },
                            backgroundColor = AnzacColor
                        )
                    }
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

                    Text("Ingrédients :", style = MaterialTheme.typography.h6, color = AnzacColor)
                    it.ingredients.forEach { ingredient ->
                        Text(text = "• $ingredient", style = MaterialTheme.typography.body1, color = MaterialTheme.colors.onSurface)
                    }
                }
            }
        } ?: run {
            Scaffold { paddingValues ->
                Column(modifier = Modifier.padding(paddingValues)) {
                    Text("Recette non trouvée.", color = AnzacColor)
                }
            }
        }
    }
}