package com.example.ui.screens

import android.content.Intent
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.viewmodel.NewsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
private fun getCategoryGradient(category: String): androidx.compose.ui.graphics.Brush {
    val colors = when (category.lowercase(Locale.ROOT)) {
        "technology", "tech" -> listOf(androidx.compose.ui.graphics.Color(0xFF00C6FF), androidx.compose.ui.graphics.Color(0xFF0072FF))
        "science" -> listOf(androidx.compose.ui.graphics.Color(0xFF7F00FF), androidx.compose.ui.graphics.Color(0xFFE100FF))
        "world", "news" -> listOf(androidx.compose.ui.graphics.Color(0xFF0575E6), androidx.compose.ui.graphics.Color(0xFF00F260))
        "sports" -> listOf(androidx.compose.ui.graphics.Color(0xFFF12711), androidx.compose.ui.graphics.Color(0xFFF5AF19))
        "gaming" -> listOf(androidx.compose.ui.graphics.Color(0xFF11998E), androidx.compose.ui.graphics.Color(0xFF38EF7D))
        "business", "finance" -> listOf(androidx.compose.ui.graphics.Color(0xFF8A2387), androidx.compose.ui.graphics.Color(0xFFE94057), androidx.compose.ui.graphics.Color(0xFFF27121))
        "open source", "coding" -> listOf(androidx.compose.ui.graphics.Color(0xFFFF416C), androidx.compose.ui.graphics.Color(0xFFFF4B2B))
        else -> listOf(androidx.compose.ui.graphics.Color(0xFF0A2463), androidx.compose.ui.graphics.Color(0xFF00E5FF))
    }
    return androidx.compose.ui.graphics.Brush.linearGradient(colors)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    articleId: Long,
    viewModel: NewsViewModel,
    onBack: () -> Unit
) {
    val articles by viewModel.articles.collectAsState()
    val article = articles.find { it.id == articleId }
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    // Reading adjustments
    var fontSizeFactor by remember { mutableStateOf(1.0f) } // Multiplier
    var lineSpacingFactor by remember { mutableStateOf(1.3f) } // Line height multiplier
    var isFullscreen by remember { mutableStateOf(false) }
    var imageLoadingEnabled by remember { mutableStateOf(true) }

    // TTS engine initialization
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    var isSpeaking by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        val ttsEngine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Engine is ready
            }
        }
        tts = ttsEngine
        onDispose {
            ttsEngine.stop()
            ttsEngine.shutdown()
        }
    }

    if (article == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Article") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                Text("Article not found", modifier = Modifier.align(Alignment.Center))
            }
        }
        return
    }

    // Mark article as Read immediately when opened
    LaunchedEffect(article) {
        viewModel.markArticleRead(article)
    }

    val dateFormat = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
    val dateString = dateFormat.format(Date(article.pubDate))

    Scaffold(
        topBar = {
            if (!isFullscreen) {
                TopAppBar(
                    title = { Text("Article Reader", style = MaterialTheme.typography.titleMedium) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { isFullscreen = true }) {
                            Icon(Icons.Filled.Fullscreen, contentDescription = "Fullscreen")
                        }
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Filled.MoreVert, contentDescription = "Options")
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Font Size: ${if (fontSizeFactor == 0.8f) "Small" else if (fontSizeFactor == 1.2f) "Large" else "Medium"}") },
                                    onClick = {
                                        fontSizeFactor = when (fontSizeFactor) {
                                            0.8f -> 1.0f
                                            1.0f -> 1.2f
                                            else -> 0.8f
                                        }
                                        showMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Line Spacing: ${if (lineSpacingFactor == 1.0f) "Compact" else if (lineSpacingFactor == 1.6f) "Wide" else "Normal"}") },
                                    onClick = {
                                        lineSpacingFactor = when (lineSpacingFactor) {
                                            1.0f -> 1.3f
                                            1.3f -> 1.6f
                                            else -> 1.0f
                                        }
                                        showMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Images: ${if (imageLoadingEnabled) "Enabled" else "Disabled"}") },
                                    onClick = {
                                        imageLoadingEnabled = !imageLoadingEnabled
                                        showMenu = false
                                    }
                                )
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (!isFullscreen) {
                BottomAppBar(
                    actions = {
                        IconButton(onClick = { viewModel.toggleSaveArticle(article) }) {
                            Icon(
                                imageVector = if (article.isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                contentDescription = "Save",
                                tint = if (article.isSaved) MaterialTheme.colorScheme.primary else LocalContentColor.current
                            )
                        }
                        IconButton(onClick = { viewModel.toggleDownloadArticle(article) }) {
                            Icon(
                                imageVector = if (article.isDownloaded) Icons.Filled.DownloadDone else Icons.Filled.Download,
                                contentDescription = "Download Offline",
                                tint = if (article.isDownloaded) MaterialTheme.colorScheme.secondary else LocalContentColor.current
                            )
                        }
                        IconButton(onClick = {
                            if (isSpeaking) {
                                tts?.stop()
                                isSpeaking = false
                            } else {
                                val textToRead = "${article.title}. Published by ${article.sourceName}. ${article.content}"
                                tts?.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, "ArticleReaderTts")
                                isSpeaking = true
                            }
                        }) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Read Aloud (TTS)"
                            )
                        }
                        IconButton(onClick = {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "${article.title}\n\nRead more at: ${article.link}")
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                        }) {
                            Icon(Icons.Filled.Share, contentDescription = "Share")
                        }
                    }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isFullscreen) PaddingValues(0.dp) else padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                if (isFullscreen) {
                    Spacer(modifier = Modifier.height(36.dp))
                }
                
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = (MaterialTheme.typography.headlineMedium.fontSize.value * fontSizeFactor).sp
                    ),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = article.sourceName,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = dateString,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = article.category,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                
                // Hero Image
                if (imageLoadingEnabled) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(MaterialTheme.shapes.medium)
                    ) {
                        if (!article.imageUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = article.imageUrl,
                                contentDescription = article.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            // Fallback gradient matching the article category
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(getCategoryGradient(article.category)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Filled.Image,
                                        contentDescription = "No Image",
                                        tint = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = article.sourceName,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = androidx.compose.ui.graphics.Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = article.content,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = (MaterialTheme.typography.bodyLarge.fontSize.value * fontSizeFactor).sp,
                        lineHeight = (MaterialTheme.typography.bodyLarge.lineHeight.value * lineSpacingFactor).sp
                    )
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                if (isFullscreen) {
                    Button(
                        onClick = { isFullscreen = false },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Icon(Icons.Filled.FullscreenExit, contentDescription = "Exit Fullscreen")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Exit Fullscreen")
                    }
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
            
            // Elegant small float action to exit fullscreen if user is deep in reading
            if (isFullscreen) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    IconButton(
                        onClick = { isFullscreen = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), shape = MaterialTheme.shapes.small)
                    ) {
                        Icon(Icons.Filled.FullscreenExit, contentDescription = "Exit Fullscreen")
                    }
                }
            }
        }
    }
}
