package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.data.model.Article
import com.example.data.model.Category
import com.example.data.model.Source
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Query("SELECT * FROM sources ORDER BY name ASC")
    fun getAllSources(): Flow<List<Source>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSource(source: Source)

    @Update
    suspend fun updateSource(source: Source)

    @Query("DELETE FROM sources WHERE id = :id")
    suspend fun deleteSource(id: Long)

    @Query("SELECT * FROM articles ORDER BY pubDate DESC")
    fun getAllArticles(): Flow<List<Article>>

    @Query("SELECT * FROM articles WHERE isSaved = 1 ORDER BY pubDate DESC")
    fun getSavedArticles(): Flow<List<Article>>
    
    @Query("SELECT * FROM articles WHERE isDownloaded = 1 ORDER BY pubDate DESC")
    fun getDownloadedArticles(): Flow<List<Article>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArticles(articles: List<Article>)

    @Update
    suspend fun updateArticle(article: Article)

    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Update
    suspend fun updateCategory(category: Category)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategory(id: Long)

    @Query("UPDATE articles SET isRead = 0")
    suspend fun clearHistory()

    @Query("DELETE FROM articles WHERE isSaved = 0 AND isDownloaded = 0")
    suspend fun clearCache()

    @Query("DELETE FROM articles")
    suspend fun deleteAllArticles()

    @Query("DELETE FROM sources")
    suspend fun deleteAllSources()

    @Query("DELETE FROM categories")
    suspend fun deleteAllCategories()
}

@Database(entities = [Source::class, Article::class, Category::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "newsfusion_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
