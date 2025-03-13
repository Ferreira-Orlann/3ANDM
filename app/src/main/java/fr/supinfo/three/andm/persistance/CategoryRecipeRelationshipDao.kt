package fr.supinfo.three.andm.persistance

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction

data class RecipeWithCategories(
    @Embedded val recipe: RecipeEntity,
    @Relation(
        parentColumn = "pk",
        entityColumn = "categoryId",
        associateBy = Junction(CategoryRecipeRelationshipEntity::class)
    )
    val categories: List<CategoryEntity>
)

data class CategoryWithRecipes(
    @Embedded val category: CategoryEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "pk",
        associateBy = Junction(CategoryRecipeRelationshipEntity::class)
    )
    val recipes: List<RecipeEntity>
)

@Dao
interface CategoryRecipeRelationshipDao {
    @Transaction
    @Query("SELECT * FROM recipes")
    fun getRecipeWithCategories(): List<RecipeWithCategories>

    @Transaction
    @Query("SELECT * FROM categories")
    fun getCategoryWithRecipes(): List<CategoryWithRecipes>
}