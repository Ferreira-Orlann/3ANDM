package fr.supinfo.three.andm.persistance

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("recipes")
data class RecipeEntity (
    @PrimaryKey() val recipeId: Int,
    val pk: Int,
    val title: String,
    val featured_image: String,
    val ingredients: List<String>,
    val rating: Int,
    val publisher: String,
    val source_url: String,
    val description: String,
    val categories: List<String>? = null
)