package com.recipebook.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: Category,
    val cookingMinutes: Int,
    val servings: Int,
    val isFavorite: Boolean = false,
    val photoFileName: String? = null,
)