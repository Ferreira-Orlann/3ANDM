package fr.supinfo.three.andm

import android.util.Log
import fr.supinfo.three.andm.persistance.RecipeDao
import fr.supinfo.three.andm.persistance.RecipeDatabase
import fr.supinfo.three.andm.persistance.RecipeDetailEntity
import fr.supinfo.three.andm.persistance.RecipeEntity
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val BASE_URL = "https://food2fork.ca/api/recipe"
private const val API_URL = "https://food2fork.ca/api/recipe/search/?query="
private const val API_KEY = "9c8b06d329136da358c2d00e76946b0111ce2c48"

@Serializable
data class RecipeResponse(
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    val results: List<Recipe>
)

@Serializable
data class Recipe(
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

@Serializable
data class RecipeDetail(
    val pk: Int,
    val title: String,
    val featured_image: String,
    val ingredients: List<String>,
    val rating: Int,
    val publisher: String,
    val source_url: String,
    val description: String,
    val cooking_instructions: String? = null,
    val date_added: String,
    val date_updated: String
)

class RecipeApi(private val database: RecipeDatabase) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    private val recipeDao: RecipeDao = database.recipeDao()

    suspend fun searchRecipes(query: String, page: Int): List<Recipe> = withContext(Dispatchers.IO) {
        try {
            val url = "$BASE_URL/search/?page=$page&query=$query"
            Log.d("RecipeApi", "🔍 Fetching recipes from: $url")

            val response: HttpResponse = client.get(url) {
                headers {
                    append(HttpHeaders.Authorization, "Token $API_KEY")
                }
            }

            if (response.status == HttpStatusCode.OK) {
                val recipes = response.body<RecipeResponse>().results
                Log.d("RecipeApi", "✅ ${recipes.size} recipes retrieved from API")

                // Sauvegarder dans la base de données
                recipeDao.insertRecipes(recipes.map { it.toEntity() })
                Log.d("RecipeApi", "💾 Recipes saved in database")

                return@withContext recipes
            } else {
                Log.e("RecipeApi", "❌ Error: ${response.status}")
                // Si l'API échoue, récupérer les recettes depuis la base de données locale
                val cachedRecipes = recipeDao.getAllRecipesFromDb()  // Tu devras créer une méthode dans RecipeDao pour cela
                Log.d("RecipeApi", "💾 Retrieved ${cachedRecipes.size} recipes from database")
                return@withContext cachedRecipes.map { it.toDomain() }
            }
        } catch (e: Exception) {
            Log.e("RecipeApi", "❌ Exception: ${e.message}")
            // En cas d'exception, récupérer les recettes depuis la base de données locale
            val cachedRecipes = recipeDao.getAllRecipesFromDb()  // Méthode à implémenter dans RecipeDao
            Log.d("RecipeApi", "💾 Retrieved ${cachedRecipes.size} recipes from database")
            return@withContext cachedRecipes.map { it.toDomain() }
        }
    }

    suspend fun getRecipeById(id: Int): RecipeDetail? = withContext(Dispatchers.IO) {
        try {
            val cachedRecipe = recipeDao.getRecipeDetailByIdFromDb(id)
            if (cachedRecipe != null) {
                Log.d("RecipeApi", "✅ Recipe found in database: ${cachedRecipe.title}")
                return@withContext cachedRecipe.toDomain()
            }

            val response: HttpResponse = client.get("$BASE_URL/get/") {
                parameter("id", id)
                headers {
                    append(HttpHeaders.Authorization, "Token $API_KEY")
                }
            }

            if (response.status == HttpStatusCode.OK) {
                val recipeDetail = response.body<RecipeDetail>()
                Log.d("RecipeApi", "✅ Recipe retrieved from API: ${recipeDetail.title}")

                // Sauvegarder les détails dans la base de données
                recipeDao.insertRecipeDetail(recipeDetail.toEntity())
                Log.d("RecipeApi", "💾 Recipe detail saved in database")

                return@withContext recipeDetail
            } else {
                Log.e("RecipeApi", "❌ Error fetching recipe by ID: ${response.status}")
                return@withContext null
            }
        } catch (e: Exception) {
            Log.e("RecipeApi", "❌ Exception: ${e.message}")
            // Si l'API échoue, récupérer les détails de la recette depuis la base de données locale
            val cachedRecipe = recipeDao.getRecipeDetailByIdFromDb(id)
            if (cachedRecipe != null) {
                Log.d("RecipeApi", "✅ Recipe found in database: ${cachedRecipe.title}")
                return@withContext cachedRecipe.toDomain()
            }
            return@withContext null
        }
    }

    private fun Recipe.toEntity() = RecipeEntity(
        pk = pk,
        title = title,
        featured_image = featured_image,
        ingredients = ingredients,
        rating = rating,
        publisher = publisher,
        source_url = source_url,
        description = description
    )

    private fun RecipeEntity.toDomain() = Recipe(
        pk = pk,
        title = title,
        featured_image = featured_image,
        ingredients = ingredients,
        rating = rating,
        publisher = publisher,
        source_url = source_url,
        description = description
    )


    private fun RecipeDetail.toEntity() = RecipeDetailEntity(
        pk = pk,
        title = title,
        featured_image = featured_image,
        ingredients = ingredients,
        rating = rating,
        publisher = publisher,
        source_url = source_url,
        description = description,
        cooking_instructions = cooking_instructions,
        date_added = date_added,
        date_updated = date_updated
    )

    private fun RecipeDetailEntity.toDomain() = RecipeDetail(
        pk = pk,
        title = title,
        featured_image = featured_image,
        ingredients = ingredients,
        rating = rating,
        publisher = publisher,
        source_url = source_url,
        description = description,
        cooking_instructions = cooking_instructions,
        date_added = date_added,
        date_updated = date_updated
    )
}