package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sources")
data class Source(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val url: String,
    val type: String = "RSS", // RSS, JSON, WEB
    val lastUpdate: Long = System.currentTimeMillis(),
    val isEnabled: Boolean = true
)

@Entity(tableName = "articles")
data class Article(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceId: Long,
    val sourceName: String,
    val title: String,
    val link: String,
    val content: String,
    val imageUrl: String? = null,
    val pubDate: Long = System.currentTimeMillis(),
    val category: String = "General",
    val isSaved: Boolean = false,
    val isDownloaded: Boolean = false,
    val isRead: Boolean = false
)

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val iconName: String = "ic_category"
)
