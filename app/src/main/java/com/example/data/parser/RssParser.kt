package com.example.data.parser

import android.util.Log
import com.example.data.model.Article
import com.example.data.model.Source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RssParser {
    
    suspend fun parseFeed(source: Source): List<Article> = withContext(Dispatchers.IO) {
        val articles = mutableListOf<Article>()
        try {
            val url = URL(source.url)
            val connection = url.openConnection() as HttpURLConnection
            connection.readTimeout = 10000
            connection.connectTimeout = 10000
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
            connection.connect()
            
            val inputStream = connection.inputStream
            if (source.type.equals("JSON", ignoreCase = true)) {
                val jsonText = inputStream.bufferedReader().use { it.readText() }
                articles.addAll(parseJsonFeed(jsonText, source))
            } else if (source.type.equals("Website", ignoreCase = true)) {
                val htmlText = inputStream.bufferedReader().use { it.readText() }
                articles.addAll(parseWebsiteHtml(htmlText, source))
            } else {
                articles.addAll(parseXml(inputStream, source))
            }
            inputStream.close()
        } catch (e: Exception) {
            Log.e("RssParser", "Error fetching feed ${source.url}: ${e.message}")
        }
        return@withContext articles
    }

    suspend fun searchDuckDuckGo(query: String): List<Article> = withContext(Dispatchers.IO) {
        val articles = mutableListOf<Article>()
        try {
            val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
            val url = URL("https://html.duckduckgo.com/html/?q=$encodedQuery")
            val connection = url.openConnection() as HttpURLConnection
            connection.readTimeout = 10000
            connection.connectTimeout = 10000
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
            connection.connect()
            
            val html = connection.inputStream.bufferedReader().use { it.readText() }
            
            // Parse DuckDuckGo search results
            val resultRegex = Regex("<a\\s+class=\"result__url\"\\s+href=\"([^\"]+)\">\\s*(.*?)\\s*</a>.*?<a\\s+class=\"result__snippet\"[^>]*>(.*?)</a>", RegexOption.DOT_MATCHES_ALL)
            val matches = resultRegex.findAll(html).take(10)
            for (match in matches) {
                val link = match.groups[1]?.value?.trim() ?: ""
                val title = match.groups[2]?.value?.replace(Regex("<.*?>"), "")?.trim() ?: ""
                val snippet = match.groups[3]?.value?.replace(Regex("<.*?>"), "")?.trim() ?: ""
                
                if (title.isNotEmpty() && link.isNotEmpty()) {
                    articles.add(Article(
                        id = System.nanoTime(), // Unique runtime ID
                        sourceId = -99L,
                        sourceName = "DuckDuckGo",
                        title = title,
                        link = link,
                        content = snippet,
                        pubDate = System.currentTimeMillis()
                    ))
                }
            }
        } catch (e: Exception) {
            Log.e("RssParser", "Error searching DDG: ${e.message}")
        }
        return@withContext articles
    }

    private fun parseJsonFeed(jsonString: String, source: Source): List<Article> {
        val articles = mutableListOf<Article>()
        try {
            val root = org.json.JSONObject(jsonString)
            val items = root.optJSONArray("items") ?: return articles
            for (i in 0 until items.length()) {
                val item = items.getJSONObject(i)
                val title = item.optString("title", "Untitled")
                val link = item.optString("url", "")
                val content = item.optString("content_html", item.optString("content_text", ""))
                val imageUrl = item.optString("image", item.optString("banner_image", null))
                val pubDateStr = item.optString("date_published", "")
                var pubDate = System.currentTimeMillis()
                if (pubDateStr.isNotEmpty()) {
                    try {
                        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
                        pubDate = format.parse(pubDateStr)?.time ?: pubDate
                    } catch (e: Exception) {}
                }
                articles.add(Article(
                    sourceId = source.id,
                    sourceName = source.name,
                    title = title,
                    link = link,
                    content = content.replace(Regex("<.*?>"), ""),
                    imageUrl = imageUrl,
                    pubDate = pubDate
                ))
            }
        } catch (e: Exception) {
            Log.e("RssParser", "Error parsing JSON feed: ${e.message}")
        }
        return articles
    }

    private fun parseWebsiteHtml(html: String, source: Source): List<Article> {
        val articles = mutableListOf<Article>()
        try {
            val aRegex = Regex("<a\\s+[^>]*href=\"([^\"]+)\"[^>]*>(.*?)</a>", RegexOption.IGNORE_CASE)
            val matches = aRegex.findAll(html).take(15)
            for (match in matches) {
                val link = match.groups[1]?.value ?: continue
                val text = match.groups[2]?.value?.replace(Regex("<.*?>"), "")?.trim() ?: ""
                if (text.length > 15 && (link.startsWith("http") || link.startsWith("/"))) {
                    val fullLink = if (link.startsWith("/")) {
                        val base = URL(source.url)
                        "${base.protocol}://${base.host}$link"
                    } else link
                    
                    articles.add(Article(
                        sourceId = source.id,
                        sourceName = source.name,
                        title = text,
                        link = fullLink,
                        content = "Scraped content from website. Tap to open and read full article original webpage.",
                        pubDate = System.currentTimeMillis()
                    ))
                }
            }
        } catch (e: Exception) {
            Log.e("RssParser", "Error scraping website: ${e.message}")
        }
        return articles
    }
    
    private fun parseXml(inputStream: InputStream, source: Source): List<Article> {
        val articles = mutableListOf<Article>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(inputStream, null)
            
            var eventType = parser.eventType
            var currentTitle = ""
            var currentLink = ""
            var currentDescription = ""
            var currentPubDate = ""
            var currentImageUrl: String? = null
            var insideItem = false
            
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagName = parser.name
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (tagName.equals("item", ignoreCase = true) || tagName.equals("entry", ignoreCase = true)) {
                            insideItem = true
                            currentTitle = ""
                            currentLink = ""
                            currentDescription = ""
                            currentPubDate = ""
                            currentImageUrl = null
                        } else if (insideItem) {
                            when {
                                tagName.equals("title", ignoreCase = true) -> currentTitle = parser.nextText().trim()
                                tagName.equals("link", ignoreCase = true) -> currentLink = parser.nextText().trim()
                                tagName.equals("description", ignoreCase = true) || tagName.equals("summary", ignoreCase = true) -> currentDescription = parser.nextText().trim()
                                tagName.equals("pubDate", ignoreCase = true) || tagName.equals("published", ignoreCase = true) -> currentPubDate = parser.nextText().trim()
                                tagName.equals("enclosure", ignoreCase = true) -> {
                                    val url = parser.getAttributeValue(null, "url")
                                    val type = parser.getAttributeValue(null, "type")
                                    if (type?.startsWith("image") == true) {
                                        currentImageUrl = url
                                    }
                                }
                                tagName.equals("thumbnail", ignoreCase = true) || tagName.equals("content", ignoreCase = true) -> {
                                    val url = parser.getAttributeValue(null, "url")
                                    if (url != null) {
                                        currentImageUrl = url
                                    }
                                }
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (tagName.equals("item", ignoreCase = true) || tagName.equals("entry", ignoreCase = true)) {
                            insideItem = false
                            
                            // parse date
                            var parsedDate = System.currentTimeMillis()
                            if (currentPubDate.isNotEmpty()) {
                                try {
                                    val format = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
                                    val date: Date? = format.parse(currentPubDate)
                                    if (date != null) {
                                        parsedDate = date.time
                                    }
                                } catch (e: Exception) {
                                    // ignore date parse errors and keep current time
                                }
                            }
                            
                            // Extract image URL from description HTML if not already found
                            if (currentImageUrl == null && currentDescription.isNotEmpty()) {
                                try {
                                    val imgRegex = Regex("<img[^>]+src\\s*=\\s*\"([^\"]+)\"", RegexOption.IGNORE_CASE)
                                    val match = imgRegex.find(currentDescription)
                                    if (match != null) {
                                        val url = match.groups[1]?.value
                                        if (url != null && (url.startsWith("http") || url.startsWith("https"))) {
                                            currentImageUrl = url
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e("RssParser", "Error parsing image regex: ${e.message}")
                                }
                            }
                            
                            val article = Article(
                                sourceId = source.id,
                                sourceName = source.name,
                                title = currentTitle,
                                link = currentLink,
                                content = currentDescription.replace(Regex("<.*?>"), ""), // simple strip HTML
                                pubDate = parsedDate,
                                imageUrl = currentImageUrl
                            )
                            articles.add(article)
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            Log.e("RssParser", "Error parsing feed ${source.url}: ${e.message}")
        }
        return articles
    }
}
