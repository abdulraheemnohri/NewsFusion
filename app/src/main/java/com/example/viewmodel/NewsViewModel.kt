package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Article
import com.example.data.model.Category
import com.example.data.model.Source
import com.example.data.parser.RssParser
import com.example.data.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: NewsRepository,
    private val parser: RssParser
) : ViewModel() {

    val sources: StateFlow<List<Source>> = repository.allSources
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val articles: StateFlow<List<Article>> = repository.allArticles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedArticles: StateFlow<List<Article>> = repository.savedArticles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val downloadedArticles: StateFlow<List<Article>> = repository.downloadedArticles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<Category>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _webSearchResults = MutableStateFlow<List<Article>>(emptyList())
    val webSearchResults: StateFlow<List<Article>> = _webSearchResults.asStateFlow()

    private val _darkTheme = MutableStateFlow(true)
    val darkTheme: StateFlow<Boolean> = _darkTheme.asStateFlow()

    private val _amoledMode = MutableStateFlow(false)
    val amoledMode: StateFlow<Boolean> = _amoledMode.asStateFlow()

    private val _dynamicColor = MutableStateFlow(false)
    val dynamicColor: StateFlow<Boolean> = _dynamicColor.asStateFlow()

    private val _fontSize = MutableStateFlow("Medium")
    val fontSize: StateFlow<String> = _fontSize.asStateFlow()

    private val _defaultReaderMode = MutableStateFlow(true)
    val defaultReaderMode: StateFlow<Boolean> = _defaultReaderMode.asStateFlow()

    private val _autoDownloadImages = MutableStateFlow(true)
    val autoDownloadImages: StateFlow<Boolean> = _autoDownloadImages.asStateFlow()

    private val _autoMarkRead = MutableStateFlow(false)
    val autoMarkRead: StateFlow<Boolean> = _autoMarkRead.asStateFlow()

    private val _wifiOnly = MutableStateFlow(false)
    val wifiOnly: StateFlow<Boolean> = _wifiOnly.asStateFlow()

    private val _backgroundUpdates = MutableStateFlow(true)
    val backgroundUpdates: StateFlow<Boolean> = _backgroundUpdates.asStateFlow()

    private val _autoCleanup = MutableStateFlow(true)
    val autoCleanup: StateFlow<Boolean> = _autoCleanup.asStateFlow()

    init {
        viewModelScope.launch {
            categories.collect { currentCategories ->
                if (currentCategories.isEmpty()) {
                    repository.addCategory(Category(name = "Technology", iconName = "Technology"))
                    repository.addCategory(Category(name = "Science", iconName = "Science"))
                    repository.addCategory(Category(name = "World", iconName = "World"))
                    repository.addCategory(Category(name = "Sports", iconName = "Sports"))
                    repository.addCategory(Category(name = "Open Source", iconName = "Open Source"))
                }
            }
        }
    }

    fun setDarkTheme(enabled: Boolean) { _darkTheme.value = enabled }
    fun setAmoledMode(enabled: Boolean) { _amoledMode.value = enabled }
    fun setDynamicColor(enabled: Boolean) { _dynamicColor.value = enabled }
    fun setFontSize(size: String) { _fontSize.value = size }
    fun setDefaultReaderMode(enabled: Boolean) { _defaultReaderMode.value = enabled }
    fun setAutoDownloadImages(enabled: Boolean) { _autoDownloadImages.value = enabled }
    fun setAutoMarkRead(enabled: Boolean) { _autoMarkRead.value = enabled }
    fun setWifiOnly(enabled: Boolean) { _wifiOnly.value = enabled }
    fun setBackgroundUpdates(enabled: Boolean) { _backgroundUpdates.value = enabled }
    fun setAutoCleanup(enabled: Boolean) { _autoCleanup.value = enabled }

    fun addCategory(name: String) {
        viewModelScope.launch {
            repository.addCategory(Category(name = name))
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            repository.updateCategory(category)
        }
    }

    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            repository.deleteCategory(categoryId)
        }
    }

    fun toggleDownloadArticle(article: Article) {
        viewModelScope.launch {
            repository.toggleArticleDownloaded(article)
        }
    }

    fun markArticleRead(article: Article) {
        viewModelScope.launch {
            repository.markArticleRead(article)
        }
    }

    fun searchWeb(query: String) {
        if (query.isBlank()) {
            _webSearchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val results = parser.searchDuckDuckGo(query)
                _webSearchResults.value = results
            } catch (e: Exception) {
                Log.e("NewsViewModel", "Error searching DDG: " + e.message)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            repository.clearCache()
        }
    }

    fun deleteDatabase() {
        viewModelScope.launch {
            repository.deleteDatabase()
        }
    }

    fun importOpml(xmlText: String) {
        viewModelScope.launch {
            try {
                val factory = org.xmlpull.v1.XmlPullParserFactory.newInstance()
                factory.isNamespaceAware = true
                val xpp = factory.newPullParser()
                xpp.setInput(xmlText.reader())
                var eventType = xpp.eventType
                while (eventType != org.xmlpull.v1.XmlPullParser.END_DOCUMENT) {
                    if (eventType == org.xmlpull.v1.XmlPullParser.START_TAG && xpp.name.equals("outline", ignoreCase = true)) {
                        val title = xpp.getAttributeValue(null, "title") ?: xpp.getAttributeValue(null, "text") ?: ""
                        val xmlUrl = xpp.getAttributeValue(null, "xmlUrl") ?: ""
                        if (title.isNotEmpty() && xmlUrl.isNotEmpty()) {
                            repository.addSource(Source(name = title, url = xmlUrl, type = "RSS"))
                        }
                    }
                    eventType = xpp.next()
                }
                refreshFeeds()
            } catch (e: Exception) {
                Log.e("NewsViewModel", "Error importing OPML: " + e.message)
            }
        }
    }

    fun exportOpml(): String {
        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        sb.append("<opml version=\"2.0\">\n")
        sb.append("  <head>\n")
        sb.append("    <title>NewsFusion Subscriptions</title>\n")
        sb.append("  </head>\n")
        sb.append("  <body>\n")
        for (source in sources.value) {
            sb.append("    <outline type=\"rss\" text=\"" + source.name + "\" title=\"" + source.name + "\" xmlUrl=\"" + source.url + "\" />\n")
        }
        sb.append("  </body>\n")
        sb.append("</opml>")
        return sb.toString()
    }

    fun backupData(): String {
        val root = JSONObject()
        try {
            val sourcesArray = JSONArray()
            for (source in sources.value) {
                val sObj = JSONObject()
                sObj.put("name", source.name)
                sObj.put("url", source.url)
                sObj.put("type", source.type)
                sObj.put("isEnabled", source.isEnabled)
                sourcesArray.put(sObj)
            }
            root.put("sources", sourcesArray)
            val categoriesArray = JSONArray()
            for (category in categories.value) {
                val cObj = JSONObject()
                cObj.put("name", category.name)
                categoriesArray.put(cObj)
            }
            root.put("categories", categoriesArray)
        } catch (e: Exception) {
            Log.e("NewsViewModel", "Error creating backup: " + e.message)
        }
        return root.toString()
    }

    fun restoreBackup(backupJson: String) {
        viewModelScope.launch {
            try {
                val root = JSONObject(backupJson)
                val sourcesArray = root.optJSONArray("sources")
                if (sourcesArray != null) {
                    for (i in 0 until sourcesArray.length()) {
                        val sObj = sourcesArray.getJSONObject(i)
                        repository.addSource(Source(
                            name = sObj.getString("name"),
                            url = sObj.getString("url"),
                            type = sObj.optString("type", "RSS"),
                            isEnabled = sObj.optBoolean("isEnabled", true)
                        ))
                    }
                }
                val categoriesArray = root.optJSONArray("categories")
                if (categoriesArray != null) {
                    for (i in 0 until categoriesArray.length()) {
                        val cObj = categoriesArray.getJSONObject(i)
                        repository.addCategory(Category(name = cObj.getString("name")))
                    }
                }
                refreshFeeds()
            } catch (e: Exception) {
                Log.e("NewsViewModel", "Error restoring backup: " + e.message)
            }
        }
    }

    fun addMockData() {
        viewModelScope.launch {
            repository.addSource(Source(name = "BBC News", url = "http://feeds.bbci.co.uk/news/rss.xml", type = "RSS"))
            repository.addSource(Source(name = "TechCrunch", url = "https://techcrunch.com/feed/", type = "RSS"))
            repository.addSource(Source(name = "Wired", url = "https://www.wired.com/feed/rss", type = "RSS"))
            refreshFeeds()
        }
    }

    fun addSource(name: String, url: String, type: String) {
        viewModelScope.launch {
            repository.addSource(Source(name = name, url = url, type = type))
            refreshFeeds()
        }
    }

    fun toggleSourceState(source: Source) {
        viewModelScope.launch {
            repository.toggleSourceState(source)
        }
    }

    fun deleteSource(sourceId: Long) {
        viewModelScope.launch {
            repository.deleteSource(sourceId)
        }
    }

    fun toggleSaveArticle(article: Article) {
        viewModelScope.launch {
            repository.toggleArticleSaved(article)
        }
    }
    
    fun markAsRead(article: Article) {
        viewModelScope.launch {
            repository.markArticleRead(article)
        }
    }

    fun refreshFeeds() {
        if (_isRefreshing.value) return
        _isRefreshing.value = true
        viewModelScope.launch {
            try {
                val currentSources = sources.value.filter { it.isEnabled }
                val newArticles = mutableListOf<Article>()
                for (source in currentSources) {
                    val parsed = parser.parseFeed(source)
                    newArticles.addAll(parsed)
                }
                if (newArticles.isNotEmpty()) {
                    repository.addArticles(newArticles)
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
