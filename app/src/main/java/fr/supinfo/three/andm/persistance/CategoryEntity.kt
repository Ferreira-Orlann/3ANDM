package fr.supinfo.three.andm.persistance

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("categories")
data class CategoryEntity(
    @PrimaryKey() val id: Int,
    val name: String
)
