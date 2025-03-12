package fr.supinfo.three.andm.persistance

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class JsonConverter {
    @TypeConverter
    fun fromListToJson(value: List<String>): String {
        return Json.encodeToString<List<String>>(value)
    }

    @TypeConverter
    fun fromJsonToList(value: String): List<String> {
        return Json.decodeFromString<List<String>>(value);
    }
}