package fr.supinfo.three.andm.persistance

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction


@Dao
interface RecipeDao {

    @Transaction
    suspend fun insertAllRecipes(recipes: List<RecipeEntity>) {
        insertRecipes(recipes)
        val allRecipes = getAllRecipesFromDb()
    }

    @Transaction
    suspend fun insertOneRecipeDetail(recipe: RecipeDetailEntity) {
        insertRecipeDetail(recipe)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<RecipeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeDetail(recipe: RecipeDetailEntity)

    @Query("SELECT * FROM recipes")
    suspend fun getAllRecipesFromDb(): List<RecipeEntity>

    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query || '%' OR ingredients LIKE '%' || :query || '%'")
    suspend fun searchRecipesByQuery(query: String): List<RecipeEntity>

    @Query("SELECT * FROM recipes WHERE pk = :id")
    suspend fun getRecipeByIdFromDb(id: Int): RecipeEntity

    @Query("SELECT * FROM recipeDetails WHERE pk = :id")
    suspend fun getRecipeDetailByIdFromDb(id: Int): RecipeDetailEntity
}