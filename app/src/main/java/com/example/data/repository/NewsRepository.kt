package com.example.data.repository

import com.example.data.local.NewsDao
import com.example.data.model.Article
import com.example.data.model.Category
import com.example.data.model.Source
import kotlinx.coroutines.flow.Flow

class NewsRepository(private val newsDao: NewsDao) {

    val allSources: Flow<List<Source>> = newsDao.getAllSources()
    val allArticles: Flow<List<Article>> = newsDao.getAllArticles()
    val savedArticles: Flow<List<Article>> = newsDao.getSavedArticles()
    val downloadedArticles: Flow<List<Article>> = newsDao.getDownloadedArticles()
    val allCategories: Flow<List<Category>> = newsDao.getAllCategories()

    suspend fun addSource(source: Source) {
        newsDao.insertSource(source)
    }

    suspend fun toggleSourceState(source: Source) {
        newsDao.updateSource(source.copy(isEnabled = !source.isEnabled))
    }
    
    suspend fun deleteSource(sourceId: Long) {
        newsDao.deleteSource(sourceId)
    }

    suspend fun toggleArticleSaved(article: Article) {
        newsDao.updateArticle(article.copy(isSaved = !article.isSaved))
    }

    suspend fun toggleArticleDownloaded(article: Article) {
        newsDao.updateArticle(article.copy(isDownloaded = !article.isDownloaded))
    }

    suspend fun markArticleRead(article: Article) {
        if (!article.isRead) {
            newsDao.updateArticle(article.copy(isRead = true))
        }
    }
    
    suspend fun addArticles(articles: List<Article>) {
        newsDao.insertArticles(articles)
    }

    suspend fun addCategory(category: Category) {
        newsDao.insertCategory(category)
    }

    suspend fun updateCategory(category: Category) {
        newsDao.updateCategory(category)
    }

    suspend fun deleteCategory(categoryId: Long) {
        newsDao.deleteCategory(categoryId)
    }

    suspend fun clearHistory() {
        newsDao.clearHistory()
    }

    suspend fun clearCache() {
        newsDao.clearCache()
    }

    suspend fun deleteDatabase() {
        newsDao.deleteAllArticles()
        newsDao.deleteAllSources()
        newsDao.deleteAllCategories()
    }
}
