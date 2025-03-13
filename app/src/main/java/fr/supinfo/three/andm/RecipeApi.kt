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

    suspend fun searchRecipes(query: String, page: Int): List<Recipe> {
        try {
            val url = "$BASE_URL/search/?page=$page&query=$query"
            val response: HttpResponse = client.get(url) {
                headers {
                    append(HttpHeaders.Authorization, "Token $API_KEY")
                }
            }

            if (response.status == HttpStatusCode.OK) {
                val recipes = response.body<RecipeResponse>().results

                recipeDao.insertAllRecipes(recipes.map { it.toEntity() })
                return recipes
            } else {
                val cachedRecipes = recipeDao.getAllRecipesFromDb()
                return cachedRecipes.map { it.toDomain() }
            }
        } catch (e: Exception) {
            val cachedRecipes = recipeDao.getAllRecipesFromDb()
            return cachedRecipes.map { it.toDomain() }
        }
    }

    suspend fun getRecipeById(id: Int): RecipeDetail? {
        try {
            val response: HttpResponse = client.get("$BASE_URL/get/") {
                parameter("id", id)
                headers {
                    append(HttpHeaders.Authorization, "Token $API_KEY")
                }
            }

            if (response.status == HttpStatusCode.OK) {
                val recipeDetail = response.body<RecipeDetail>()

                recipeDao.insertOneRecipeDetail(recipeDetail.toEntity())
                return recipeDetail
            } else {
                val cachedRecipe = recipeDao.getRecipeDetailByIdFromDb(id)
                return cachedRecipe.toDomain()
            }
        } catch (e: Exception) {
            val cachedRecipe = recipeDao.getRecipeDetailByIdFromDb(id)
            return cachedRecipe.toDomain()
        }
    }

    private fun Recipe.toEntity() = RecipeEntity(
        pk = this.pk,
        title = this.title,
        featured_image = this.featured_image,
        ingredients = this.ingredients,
        rating = this.rating,
        publisher = this.publisher,
        source_url = this.source_url,
        description = this.description
    )

    private fun RecipeEntity.toDomain() = Recipe(
        pk = this.pk,
        title = this.title,
        featured_image = this.featured_image,
        ingredients = this.ingredients,
        rating = this.rating,
        publisher = this.publisher,
        source_url = this.source_url,
        description = this.description
    )


    private fun RecipeDetail.toEntity() = RecipeDetailEntity(
        pk = this.pk,
        title = this.title,
        featured_image = this.featured_image,
        ingredients = this.ingredients,
        rating = this.rating,
        publisher = this.publisher,
        source_url = this.source_url,
        description = this.description,
        cooking_instructions = this.cooking_instructions,
        date_added = this.date_added,
        date_updated = this.date_updated
    )

    private fun RecipeDetailEntity.toDomain() = RecipeDetail(
        pk = this.pk,
        title = this.title,
        featured_image = this.featured_image,
        ingredients = this.ingredients,
        rating = this.rating,
        publisher = this.publisher,
        source_url = this.source_url,
        description = this.description,
        cooking_instructions = this.cooking_instructions,
        date_added = this.date_added,
        date_updated = this.date_updated
    )
}