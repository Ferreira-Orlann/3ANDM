package fr.supinfo.three.andm.persistance

import androidx.room.Entity

@Entity(primaryKeys = ["recipeId", "categoryId"])
data class CategoryRecipeRelationshipEntity(
    val recipeId: Int,
    val categoryId: Int
)