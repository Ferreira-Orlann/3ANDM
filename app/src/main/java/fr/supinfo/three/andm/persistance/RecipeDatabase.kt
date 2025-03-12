package fr.supinfo.three.andm.persistance

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [RecipeEntity::class, CategoryEntity::class, CategoryRecipeRelationshipEntity::class],
    version = 1
)
@TypeConverters(JsonConverter::class)
abstract class RecipeDatabase: RoomDatabase() {
    abstract fun recipeDao(): RecipeDao;
    abstract fun relationshipDto(): CategoryRecipeRelationshipDao

}