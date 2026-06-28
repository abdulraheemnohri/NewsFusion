package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.viewmodel.NewsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: NewsViewModel, onArticleClick: (Long) -> Unit) {
    var query by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    val allArticles by viewModel.articles.collectAsState()
    val savedArticles by viewModel.savedArticles.collectAsState()
    val offlineArticles by viewModel.downloadedArticles.collectAsState()
    val webResults by viewModel.webSearchResults.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val sourceArticles = when (selectedFilter) {
        "Saved" -> savedArticles
        "Offline" -> offlineArticles
        else -> allArticles
    }

    val filteredArticles = if (query.isBlank() && selectedFilter == "All") {
        emptyList()
    } else if (query.isBlank()) {
        sourceArticles
    } else {
        sourceArticles.filter { 
            it.title.contains(query, ignoreCase = true) || 
            it.content.contains(query, ignoreCase = true) ||
            it.sourceName.contains(query, ignoreCase = true)
        }
    }

    val finalArticles = if (selectedFilter == "Web Search (DDG)") {
        webResults
    } else {
        filteredArticles
    }

    // Trigger DDG search when query changes and we are in Web Search mode
    LaunchedEffect(query, selectedFilter) {
        if (selectedFilter == "Web Search (DDG)" && query.isNotBlank()) {
            viewModel.searchWeb(query)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search", fontWeight = FontWeight.Bold) },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(if (selectedFilter == "Web Search (DDG)") "Search DuckDuckGo..." else "Search local articles...") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    shape = MaterialTheme.shapes.extraLarge,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        if (selectedFilter == "Web Search (DDG)" && query.isNotBlank()) {
                            viewModel.searchWeb(query)
                        }
                        keyboardController?.hide()
                    })
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "Web Search (DDG)", "Saved", "Offline").forEach { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { 
                                selectedFilter = filter 
                                if (filter == "Web Search (DDG)" && query.isNotBlank()) {
                                    viewModel.searchWeb(query)
                                }
                            },
                            label = { Text(filter) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Advanced Search Filters",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(onClick = { }, label = { Text("Date Range") }, trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) })
                    AssistChip(onClick = { }, label = { Text("Source Quality") }, trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) })
                    AssistChip(onClick = { }, label = { Text("Category") }, trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) })
                    AssistChip(onClick = { }, label = { Text("Language") }, trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) })
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            if (isRefreshing) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (finalArticles.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (query.isBlank() && selectedFilter == "All") {
                        Text("Type to query local full-text index", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else if (selectedFilter == "Web Search (DDG)" && query.isBlank()) {
                        Text("Search public news via DuckDuckGo", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        Text("No articles found matching query", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(finalArticles) { article ->
                        ArticleCard(
                            article = article,
                            onSaveClick = { viewModel.toggleSaveArticle(article) },
                            onClick = { onArticleClick(article.id) },
                            onDownloadClick = { viewModel.toggleDownloadArticle(article) }
                        )
                    }
                }
            }
        }
    }
}
