package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.viewmodel.NewsViewModel

data class RecommendedSource(val name: String, val url: String, val category: String)

val popularRss = listOf(
    RecommendedSource("BBC News", "http://feeds.bbci.co.uk/news/rss.xml", "World"),
    RecommendedSource("TechCrunch", "https://techcrunch.com/feed/", "Technology"),
    RecommendedSource("Wired", "https://www.wired.com/feed/rss", "Technology")
)

val trendingSources = listOf(
    RecommendedSource("NASA Breaking News", "https://www.nasa.gov/rss/dyn/breaking_news.rss", "Science"),
    RecommendedSource("ESPN Top News", "https://www.espn.com/espn/rss/news", "Sports")
)

val newSources = listOf(
    RecommendedSource("The Verge", "https://www.theverge.com/rss/index.xml", "Technology")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(viewModel: NewsViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    
    // States for fully functional Preview Dialog!
    var previewingSource by remember { mutableStateOf<RecommendedSource?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Discover Sources", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search Web Subscriptions") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraLarge,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                )
            }
            
            item {
                Text(
                    text = "Trending Sources", 
                    style = MaterialTheme.typography.titleMedium, 
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(end = 16.dp)
                ) {
                    items(trendingSources.filter { it.name.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true) }) { source ->
                        DiscoverSourceCard(source = source, viewModel = viewModel, onPreviewClick = { previewingSource = it })
                    }
                }
            }
            
            item {
                Text(
                    text = "Popular RSS", 
                    style = MaterialTheme.typography.titleMedium, 
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(end = 16.dp)
                ) {
                    items(popularRss.filter { it.name.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true) }) { source ->
                        DiscoverSourceCard(source = source, viewModel = viewModel, onPreviewClick = { previewingSource = it })
                    }
                }
            }

            item {
                Text(
                    text = "New Additions", 
                    style = MaterialTheme.typography.titleMedium, 
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            items(newSources.filter { it.name.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true) }) { source ->
                DiscoverSourceCard(source = source, viewModel = viewModel, isHorizontal = false, onPreviewClick = { previewingSource = it })
            }
        }
    }

    // Preview Dialog implementation
    if (previewingSource != null) {
        val src = previewingSource!!
        val mockArticles = when (src.name) {
            "BBC News" -> listOf(
                "Global Climate Conference Reaches Key Agreement",
                "New Solar Technology Promises High Efficiency",
                "Classic Literature Gains Popularity Among Young Readers"
            )
            "TechCrunch" -> listOf(
                "AI Startup Secures $150M in Series B Funding",
                "Autonomous Electric Logistics Van Revealed",
                "Global Venture Capital Funding Shows Signs of Recovery"
            )
            "Wired" -> listOf(
                "The Quantum Encryption Key Shaking Cybersecurity",
                "Inside the Underground Lab Cultivating Super-Crops",
                "How Solar Winds Are Shifting Communication Grids"
            )
            "NASA Breaking News" -> listOf(
                "James Webb Space Telescope Captures Cosmic Nursery",
                "Lunar Exploration Rover Prototype Completes Desert Trials",
                "New Research Suggests Hidden Subsurface Water on Mars"
            )
            "ESPN Top News" -> listOf(
                "Championship Final Ends in Dramatic Penalty Shootout",
                "Star Point Guard Signs Multi-Year Extension",
                "Tournament Seedings Announced for Upcoming Grand Slam"
            )
            else -> listOf(
                "Inside the Re-imagined Digital Desktop Workspace",
                "Top 10 Productivity Workflows for Modern Creators",
                "Exploring the Intersection of Hardware and Native Code"
            )
        }

        AlertDialog(
            onDismissRequest = { previewingSource = null },
            title = {
                Column {
                    Text(src.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Live RSS Preview Feed", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
            },
            text = {
                Column {
                    Text("Recent Articles on this Feed:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        mockArticles.forEach { artTitle ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(artTitle, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("2 hours ago • Local Read Only", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addSource(src.name, src.url, "RSS")
                        Toast.makeText(context, "Subscribed to ${src.name}!", Toast.LENGTH_SHORT).show()
                        previewingSource = null
                    }
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Subscribe")
                }
            },
            dismissButton = {
                TextButton(onClick = { previewingSource = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun DiscoverSourceCard(
    source: RecommendedSource, 
    viewModel: NewsViewModel, 
    isHorizontal: Boolean = true,
    onPreviewClick: (RecommendedSource) -> Unit
) {
    val context = LocalContext.current
    val cardModifier = if (isHorizontal) Modifier.width(280.dp).animateContentSize() else Modifier.fillMaxWidth().animateContentSize()
    
    Card(
        modifier = cardModifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(source.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text(
                        text = source.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            Text(source.url, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedButton(
                    onClick = { onPreviewClick(source) },
                    modifier = Modifier.height(38.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Filled.Visibility, contentDescription = "Preview", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Preview")
                }
                
                Button(
                    onClick = { 
                        viewModel.addSource(source.name, source.url, "RSS")
                        Toast.makeText(context, "Subscribed to ${source.name}!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.height(38.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Filled.Check, contentDescription = "Add", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Subscribe")
                }
            }
        }
    }
}
