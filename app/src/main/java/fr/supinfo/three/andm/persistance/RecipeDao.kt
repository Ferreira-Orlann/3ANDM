package fr.supinfo.three.andm.persistance

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.delay

@Dao
interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<RecipeEntity>) {
        Log.d("RecipeDao", "💾 Inserted ${recipes.size} recipes into database")
        // Ajouter un log pour afficher le contenu de la base de données après l'insertion



        delay(3000)
        val allRecipes = getAllRecipesFromDb()
        Log.d("RecipeDao", "💾 Total recipes in DB after insert: ${allRecipes.size}")
    }

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
