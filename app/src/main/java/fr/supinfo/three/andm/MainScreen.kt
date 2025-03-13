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
import fr.supinfo.three.andm.ui.theme.AnzacColor
import kotlinx.coroutines.launch



import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*

import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import fr.supinfo.three.andm.ui.theme.AnzacColor
import kotlinx.coroutines.launch
@Composable
fun MainScreen(
    recipeApi: RecipeApi,
    recipes: List<Recipe>,
    onRecipeClick: (Recipe) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    onRecipesLoaded: (List<Recipe>) -> Unit,
    currentPage: Int,
    onPageChange: (Int) -> Unit
) {
    val categories = listOf("All", "Chicken", "Beef", "Soup", "Dessert", "Vegetarian", "French")
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(searchQuery, selectedCategory, currentPage) {
        coroutineScope.launch {
            try {
                val query = if (searchQuery.isNotEmpty()) searchQuery else if (selectedCategory == "All") "" else selectedCategory
                val newRecipes = recipeApi.searchRecipes(query, currentPage)
                val filteredRecipes = if (selectedCategory == "All") {
                    newRecipes.take(30)
                } else {
                    newRecipes.filter { recipe ->
                        recipe.ingredients.any { it.contains(selectedCategory, ignoreCase = true) }
                    }.take(30)
                }
                onRecipesLoaded(filteredRecipes)
            } catch (e: Exception) {
            }
        }
    }

    Scaffold(
        topBar = {
            Column {
                Spacer(modifier = Modifier.height(0.dp))
                TopAppBar(
                    title = { Text("Recettes", color = MaterialTheme.colors.onPrimary) },
                    backgroundColor = AnzacColor
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text("Rechercher une recette", color = AnzacColor) },
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = MaterialTheme.colors.onSurface,
                    backgroundColor = AnzacColor.copy(alpha = 0.1f),
                    focusedIndicatorColor = AnzacColor,
                    unfocusedIndicatorColor = AnzacColor.copy(alpha = 0.5f)
                )
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    Button(
                        onClick = { onCategoryChange(category) },
                        modifier = Modifier.padding(horizontal = 4.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = AnzacColor),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text(category, color = MaterialTheme.colors.onPrimary)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { if (currentPage > 1) onPageChange(currentPage - 1) },
                    enabled = currentPage > 1,
                    colors = ButtonDefaults.buttonColors(backgroundColor = AnzacColor),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Previous", color = MaterialTheme.colors.onPrimary)
                }

                Text("Page $currentPage", modifier = Modifier.align(Alignment.CenterVertically))

                Button(
                    onClick = { onPageChange(currentPage + 1) },
                    colors = ButtonDefaults.buttonColors(backgroundColor = AnzacColor),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Next", color = MaterialTheme.colors.onPrimary)
                }
            }

            if (recipes.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn {
                    items(recipes.filter {
                        it.title.contains(searchQuery, ignoreCase = true) ||
                                it.ingredients.any { ingredient ->
                                    ingredient.contains(searchQuery, ignoreCase = true)
                                }
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


