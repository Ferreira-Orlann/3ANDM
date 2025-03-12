package fr.supinfo.three.andm.persistance

import androidx.room.Database
import androidx.room.TypeConverters

@Database(
    entities = [],
    version = 1
)
@TypeConverters(JsonConverter::class)
abstract class RecipeDatabase {
    abstract fun recipeDao(): RecipeDao;
}