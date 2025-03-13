package fr.supinfo.three.andm.persistance

import androidx.room.Entity

@Entity(primaryKeys = ["pk", "categoryId"])
data class CategoryRecipeRelationshipEntity(
    val pk: Int,
    val categoryId: Int
)