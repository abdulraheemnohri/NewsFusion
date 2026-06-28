package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.viewmodel.NewsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(viewModel: NewsViewModel, onArticleClick: (Long) -> Unit) {
    val articles by viewModel.articles.collectAsState()
    val hiddenArticles = remember { mutableStateListOf<Long>() }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("News Feed", fontWeight = FontWeight.Bold) },
            )
        }
    ) { padding ->
        val visibleArticles = articles.filter { !hiddenArticles.contains(it.id) }
        
        androidx.compose.material3.pulltorefresh.PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshFeeds() },
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            items(visibleArticles, key = { it.id }) { article ->
                ArticleCard(
                    article = article, 
                    onSaveClick = { 
                        viewModel.toggleSaveArticle(article)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(if (article.isSaved) "Removed from Saved" else "Saved to Bookmarks")
                        }
                    },
                    onClick = { onArticleClick(article.id) },
                    onHide = {
                        hiddenArticles.add(article.id)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Article hidden")
                        }
                    },
                    onAddCategory = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Add category dialogue would appear here")
                        }
                    },
                    onDownloadClick = {
                        viewModel.toggleDownloadArticle(article)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(if (article.isDownloaded) "Removed from Downloads" else "Saved for Offline Reading")
                        }
                    }
                )
            }
        }
        }
    }
}
