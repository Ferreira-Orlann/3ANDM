package fr.supinfo.three.andm

import android.util.Log
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

class RecipeApi {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    // 🔎 Récupérer des recettes avec une recherche
    suspend fun searchRecipes(query: String, page: Int): List<Recipe> = withContext(Dispatchers.IO) {
        try {
            val url = "$BASE_URL/search/?page=$page&query=$query"
            println("🔍 URL de la requête : $url") // Log de l'URL

            val response: HttpResponse = client.get(url) {
                parameter("page", page)
                parameter("query", query)
                headers {
                    append(HttpHeaders.Authorization, "Token $API_KEY")
                }
            }

            if (response.status == HttpStatusCode.OK) {
                return@withContext response.body<RecipeResponse>().results
            } else {
                return@withContext emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext emptyList()
        }
    }

    suspend fun searchRecipess(query: String): List<Recipe> {
        val url = "$API_URL$query"
        Log.d("RecipeApi", "Fetching recipes from: $url")

        val response: HttpResponse = client.get(url) {
            headers {
                append("Authorization", "Token $API_KEY")
            }
        }
        return response.body<RecipeResponse>().results
    }

    suspend fun getRecipeById(id: Int): RecipeDetail? = withContext(Dispatchers.IO) {
        try {
            val response: HttpResponse = client.get("$BASE_URL/get/") {
                parameter("id", id)
                headers {
                    append(HttpHeaders.Authorization, "Token $API_KEY")
                }
            }
            if (response.status == HttpStatusCode.OK) {
                return@withContext response.body<RecipeDetail>()
            } else {
                return@withContext null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
}
