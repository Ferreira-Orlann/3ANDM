/*package fr.supinfo.three.andm

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
import kotlinx.coroutines.withContext*/


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
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        recipes = RecipeApi().getRecipes("")
        filteredRecipes = recipes
        isLoading = false
    }

    LaunchedEffect(searchQuery.text) {
        filteredRecipes = recipes.filter {
            it.title.contains(searchQuery.text, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recipe Search", style = MaterialTheme.typography.h6) },
                backgroundColor = MaterialTheme.colors.primarySurface
            )
        },

        ) { paddingValues ->
        // Display loading indicator while fetching data
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Display list of recipes
            Column(modifier = Modifier.padding(paddingValues)) {
                filteredRecipes.forEach { recipe ->
                    MainScreen(
                        recipes = recipes,
                        onRecipeClick = { recipe ->
                            navController.navigate("detail/${recipe.pk}")
                        }
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    NavHost(navController, startDestination = "list") {
        composable("list") {
            MainScreen(
                recipes = recipes,
                onRecipeClick = { recipe ->
                    navController.navigate("detail/${recipe.pk}")
                }
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
                val recipes = RecipeApi().getRecipes("")
                println("Recettes récupérées: $recipes")  // Affichez les recettes récupérées dans les logs
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