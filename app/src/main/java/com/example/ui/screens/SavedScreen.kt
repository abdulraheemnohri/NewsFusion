package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.viewmodel.NewsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(viewModel: NewsViewModel, onBack: () -> Unit, onArticleClick: (Long) -> Unit) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Bookmarks", "Downloads", "Reading History")
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val savedArticles by viewModel.savedArticles.collectAsState()
    val downloadedArticles by viewModel.downloadedArticles.collectAsState()
    val allArticles by viewModel.articles.collectAsState()

    var showExportDialog by remember { mutableStateOf(false) }
    var opmlText by remember { mutableStateOf("") }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val historyArticles = remember(allArticles) {
        allArticles.filter { it.isRead }
    }

    val displayArticles = when (selectedTabIndex) {
        0 -> savedArticles
        1 -> downloadedArticles
        else -> historyArticles
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Offline Saved & History", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        opmlText = viewModel.exportOpml()
                        showExportDialog = true
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = "Export OPML Subscriptions")
                    }
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Clear Current Section")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            if (displayArticles.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    val message = when (selectedTabIndex) {
                        0 -> "No bookmarked articles yet"
                        1 -> "No downloaded articles for offline reading"
                        else -> "Your reading history is empty"
                    }
                    Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(displayArticles) { article ->
                        ArticleCard(
                            article = article,
                            onSaveClick = { viewModel.toggleSaveArticle(article) },
                            onClick = { onArticleClick(article.id) }
                        )
                    }
                }
            }
        }

        // OPML Export Dialog
        if (showExportDialog) {
            AlertDialog(
                onDismissRequest = { showExportDialog = false },
                title = { Text("Export OPML Subscriptions") },
                text = {
                    Column {
                        Text("Copy your RSS subscription list in OPML format:", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = opmlText,
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            readOnly = true,
                            textStyle = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        clipboardManager.setText(AnnotatedString(opmlText))
                        Toast.makeText(context, "OPML copied to clipboard!", Toast.LENGTH_SHORT).show()
                        showExportDialog = false
                    }) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy OPML")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExportDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }

        // Delete confirmation
        if (showDeleteConfirm) {
            val sectionName = tabs[selectedTabIndex]
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text("Clear $sectionName?") },
                text = { Text("Are you sure you want to clear all items from $sectionName? This operation is permanent and managed entirely on your device.") },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        onClick = {
                            when (selectedTabIndex) {
                                0 -> {
                                    // Clear all saved
                                    savedArticles.forEach { viewModel.toggleSaveArticle(it) }
                                }
                                1 -> {
                                    // Clear downloads
                                    downloadedArticles.forEach { viewModel.toggleDownloadArticle(it) }
                                }
                                2 -> {
                                    // Clear history
                                    viewModel.clearHistory()
                                }
                            }
                            showDeleteConfirm = false
                            Toast.makeText(context, "Cleared $sectionName items!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Clear All")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
