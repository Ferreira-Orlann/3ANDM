/*package fr.supinfo.three.andm

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import androidx.compose.ui.graphics.Color*/



package fr.supinfo.three.andm

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter

@Composable
fun MainScreen(recipes: List<Recipe>, onRecipeClick: (Recipe) -> Unit) {
    Scaffold(

    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
            items(recipes) { recipe ->
                RecipeCard(recipe, onRecipeClick)
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

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    MainScreen(
        recipes = listOf(
            Recipe(1, "Test Recipe", "https://via.placeholder.com/150", listOf("Ingredient 1", "Ingredient 2"))
        ),
        onRecipeClick = {}
    )
}