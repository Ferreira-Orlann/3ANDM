package fr.supinfo.three.andm.persistance

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey() val categoryId: Int,
    val name: String
)
