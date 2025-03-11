package fr.supinfo.three.andm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Composable
fun RecipeApp() {
    val navController = rememberNavController()
    var recipes by remember { mutableStateOf<List<Recipe>>(emptyList()) }
    var filteredRecipes by remember { mutableStateOf<List<Recipe>>(emptyList()) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var selectedCategory by remember { mutableStateOf("All") } // ✅ Ajouter la catégorie sélectionnée
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        recipes = RecipeApi().searchRecipes("")
        filteredRecipes = recipes
        isLoading = false
    }

    LaunchedEffect(searchQuery.text, selectedCategory) {
        val query = if (searchQuery.text.isNotEmpty()) searchQuery.text else selectedCategory
        val newRecipes = RecipeApi().searchRecipes(query)

        filteredRecipes = if (selectedCategory == "All") {
            newRecipes
        } else {
            newRecipes.filter { recipe ->
                recipe.ingredients.any { it.contains(selectedCategory, ignoreCase = true) }
            }
        }

        println("🔍 Recherche ou catégorie sélectionnée : $query")
        println("📌 Recettes trouvées : ${filteredRecipes.size}")
    }

    NavHost(navController, startDestination = "list") {
        composable("list") {
            MainScreen(
                recipes = filteredRecipes,
                onRecipeClick = { recipe ->
                    navController.navigate("detail/${recipe.pk}")
                },
                searchQuery = searchQuery.text,
                onSearchQueryChange = { newQuery -> searchQuery = TextFieldValue(newQuery) },
                selectedCategory = selectedCategory, // ✅ Passer la catégorie sélectionnée
                onCategoryChange = { newCategory -> selectedCategory = newCategory }, // ✅ Permettre de la mettre à jour
                onRecipesLoaded = { newRecipes -> recipes = newRecipes }
            )
        }
        composable(
            "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getInt("id")
            var recipe by remember { mutableStateOf<Recipe?>(null) }
            var isLoading by remember { mutableStateOf(true) }

            LaunchedEffect(recipeId) {
                isLoading = true
                val recipes = RecipeApi().searchRecipes("")
                recipe = recipes.find { it.pk == recipeId }
                isLoading = false
            }

            recipe?.let {
                DetailScreen(
                    recipeId = recipeId!!,
                    onBack = { navController.popBackStack() },
                    paddingValues = it
                )
            } ?: Scaffold { paddingValues ->
                Column(modifier = Modifier.padding(paddingValues)) {
                    Text("Chargement...")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRecipeApp() {
    RecipeApp()
}