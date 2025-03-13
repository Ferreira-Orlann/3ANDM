package fr.supinfo.three.andm.persistance

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromIngredientsList(ingredients: List<String>): String {
        return Json.encodeToString(ingredients)
    }

    @TypeConverter
    fun toIngredientsList(data: String): List<String> {
        return Json.decodeFromString(data)
    }
}