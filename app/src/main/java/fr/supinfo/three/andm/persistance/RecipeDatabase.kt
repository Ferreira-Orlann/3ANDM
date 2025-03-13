package fr.supinfo.three.andm.persistance

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [RecipeEntity::class, RecipeDetailEntity::class, CategoryEntity::class, CategoryRecipeRelationshipEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class RecipeDatabase: RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
    abstract fun categoryRecipeRelationshipDao(): CategoryRecipeRelationshipDao

    companion object {
        @Volatile
        private var INSTANCE: RecipeDatabase? = null

        fun getInstance(context: Context): RecipeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecipeDatabase::class.java,
                    "recipe_database"
                ) // Ajoute ceci pour éviter les conflits de version
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}