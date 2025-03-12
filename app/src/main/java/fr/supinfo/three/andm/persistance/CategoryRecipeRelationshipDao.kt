package fr.supinfo.three.andm.persistance

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction

data class RecipeWithCategories(
    @Embedded val recipe: RecipeEntity,
    @Relation(
        parentColumn = "recipeId",
        entityColumn = "categoryId",
        associateBy = Junction(CategoryRecipeRelationshipEntity::class)
    )
    val categories: List<CategoryEntity>
)

data class CategoryWithRecipes(
    @Embedded val song: CategoryEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "recipeId",
        associateBy = Junction(CategoryRecipeRelationshipEntity::class)
    )
    val playlists: List<RecipeEntity>
)

abstract class CategoryRecipeRelationshipDao {
    @Transaction
    @Query("SELECT * FROM recipes")
    abstract fun getRecipeWithCategories(): List<RecipeWithCategories>

    @Transaction
    @Query("SELECT * FROM categories")
    abstract fun getCategoryWithRecipes(): List<CategoryWithRecipes>
}