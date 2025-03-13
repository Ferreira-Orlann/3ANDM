package fr.supinfo.three.andm.persistance

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.delay
import fr.supinfo.three.andm.persistance.RecipeEntity
import fr.supinfo.three.andm.persistance.RecipeDetailEntity

@Dao
interface RecipeDao {

    @Transaction
    suspend fun insertAllRecipes(recipes: List<RecipeEntity>) {
        insertRecipes(recipes)
        val allRecipes = getAllRecipesFromDb()
        Log.d("RecipeDao", "💾 Après insertion : ${allRecipes.size} recettes en base")
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<RecipeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeDetail(recipe: RecipeDetailEntity) {
        Log.d("RecipeDao", "💾 Inserted recipe detail into database: ${recipe.title}")
    }

    @Query("SELECT * FROM recipes")
    suspend fun getAllRecipesFromDb(): List<RecipeEntity>

    suspend fun getAllRecipesWithLog(): List<RecipeEntity> {
        val recipes = getAllRecipesFromDb()
        Log.d("RecipeDao", "💾 Retrieved ${recipes.size} recipes from database")
        return recipes
    }


    @Query("SELECT * FROM recipes WHERE pk = :id")
    suspend fun getRecipeByIdFromDb(id: Int): RecipeEntity

    @Query("SELECT * FROM recipeDetails WHERE pk = :id")
    suspend fun getRecipeDetailByIdFromDb(id: Int): RecipeDetailEntity
}
