package fr.supinfo.three.andm

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

private const val API_URL = "https://food2fork.ca/api/recipe/search/?query="
private const val API_KEY = "9c8b06d329136da358c2d00e76946b0111ce2c48"

@Serializable
data class RecipeResponse(
    val count: Int,
    val results: List<Recipe>
)

@Serializable
data class Recipe(
    val pk: Int,
    val title: String,
    val featured_image: String,
    val ingredients: List<String>
)

class RecipeApi {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun getRecipes(query: String): List<Recipe> = withContext(Dispatchers.IO) {
        val response: HttpResponse = client.get("$API_URL$query") {
            headers {
                append("Authorization", "Token $API_KEY")
            }
        }
        return@withContext response.body<RecipeResponse>().results
    }
}