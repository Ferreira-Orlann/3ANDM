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
import fr.supinfo.three.andm.persistance.RecipeDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Composable
fun RecipeApp(database: RecipeDatabase) {
    val navController = rememberNavController()
    val recipeApi = remember { RecipeApi(database) }
    var recipes by remember { mutableStateOf<List<Recipe>>(emptyList()) }
    var filteredRecipes by remember { mutableStateOf<List<Recipe>>(emptyList()) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var selectedCategory by remember { mutableStateOf("All") }
    var isLoading by remember { mutableStateOf(true) }
    var currentPage by remember { mutableStateOf(1) }

    LaunchedEffect(Unit) {
        isLoading = true
        navController.navigate("splashScreen") // Naviguer vers l'écran de splash pendant le chargement
        recipes = recipeApi.searchRecipes("", currentPage)
        filteredRecipes = recipes
        isLoading = false
        navController.popBackStack() // Revenir à la liste une fois les recettes chargées
    }

    LaunchedEffect(searchQuery.text, selectedCategory, currentPage) {
        val query = if (searchQuery.text.isNotEmpty()) searchQuery.text else selectedCategory
        val newRecipes = recipeApi.searchRecipes(query, currentPage)

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

    NavHost(navController, startDestination = "splashScreen") { // Modifier la destination de départ
        composable("splashScreen") {
            SplashScreen(navController)
        }

        composable("listScreen") {
            MainScreen(
                recipeApi = recipeApi,
                recipes = filteredRecipes,
                onRecipeClick = { recipe ->
                    navController.navigate("detail/${recipe.pk}")
                },
                searchQuery = searchQuery.text,
                onSearchQueryChange = { newQuery -> searchQuery = TextFieldValue(newQuery) },
                selectedCategory = selectedCategory,
                onCategoryChange = { newCategory -> selectedCategory = newCategory },
                onRecipesLoaded = { newRecipes -> recipes = newRecipes },
                currentPage = currentPage,
                onPageChange = { newPage -> currentPage = newPage }
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
                val recipes = filteredRecipes
                recipe = recipes.find { it.pk == recipeId }
                isLoading = false
            }

            if (isLoading) {
                SplashScreen(navController)
            } else {
                recipe?.let {
                    DetailScreen(
                        recipeId = recipeId!!,
                        onBack = { navController.popBackStack() },
                        paddingValues = it,
                        recipeApi = recipeApi,
                    )
                } ?: Scaffold { paddingValues ->
                    Column(modifier = Modifier.padding(paddingValues)) {
                        Text("Chargement...") // Vous pouvez ici aussi afficher un message
                    }
                }
            }
        }
    }
}



