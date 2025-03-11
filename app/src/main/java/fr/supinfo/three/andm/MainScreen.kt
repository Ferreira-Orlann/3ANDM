package fr.supinfo.three.andm

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import kotlinx.coroutines.launch



@Composable
fun MainScreen(
    recipes: List<Recipe>,
    onRecipeClick: (Recipe) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onRecipesLoaded: (List<Recipe>) -> Unit // Callback pour mettre à jour les recettes dans l'activité principale
) {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "chicken", "Beef", "Soup", "Dessert", "Vegetarian", "French")

    // CoroutineScope pour gérer les appels réseau
    val coroutineScope = rememberCoroutineScope()

    // Charger les recettes lorsque la recherche change
    LaunchedEffect(searchQuery, selectedCategory) {
        coroutineScope.launch {
            try {
                val recipeApi = RecipeApi()
                val newRecipes = recipeApi.searchRecipes(searchQuery)

                val filteredRecipes = if (selectedCategory == "All") {
                    newRecipes
                } else {
                    newRecipes.filter { recipe ->

                        recipe.ingredients.any { it.contains(selectedCategory, ignoreCase = true) }
                    }
                }

                println("🔍 Catégorie sélectionnée: $selectedCategory")
                println("📌 Recettes avant filtrage: ${newRecipes.size}")
                println("✅ Recettes après filtrage: ${filteredRecipes.size}")

                onRecipesLoaded(filteredRecipes)
            } catch (e: Exception) {
                println("Erreur lors du chargement des recettes : ${e.message}")
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Recettes") })
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Barre de recherche
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text("Rechercher une recette") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    Button(
                        onClick = { selectedCategory = category },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(category)
                    }
                }
            }

            if (recipes.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn {
                    items(recipes.filter {

                        (selectedCategory == "All" || it.ingredients.any { ingredient ->
                            ingredient.contains(selectedCategory, ignoreCase = true)
                        }) && it.title.contains(searchQuery, ignoreCase = true)
                    }) { recipe ->
                        RecipeCard(recipe, onRecipeClick)
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeCard(recipe: Recipe, onRecipeClick: (Recipe) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onRecipeClick(recipe) },
        elevation = 4.dp
    ) {
        Column {
            Image(
                painter = rememberImagePainter(recipe.featured_image),
                contentDescription = recipe.title,
                modifier = Modifier.height(200.dp).fillMaxWidth()
            )
            Text(recipe.title, modifier = Modifier.padding(8.dp))
        }
    }
}

