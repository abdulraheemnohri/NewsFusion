package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.viewmodel.NewsViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourcesScreen(viewModel: NewsViewModel, onDiscoverClick: () -> Unit, onAddSourceClick: () -> Unit) {
    val context = LocalContext.current
    val sources by viewModel.sources.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Subscriptions", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(
                        onClick = onDiscoverClick,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    ) {
                        Icon(Icons.Filled.Explore, contentDescription = "Discover", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddSourceClick,
                icon = { Icon(Icons.Filled.Add, contentDescription = "Add Source") },
                text = { Text("Add Source") }
            )
        }
    ) { padding ->
        if (sources.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.RssFeed,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "No sources subscribed yet",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Build your custom news universe by subscribing to standard RSS feeds, allowed websites, or DuckDuckGo query trackers.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Button(onClick = onDiscoverClick) {
                        Icon(Icons.Filled.Explore, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Discover Global Feeds")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(sources) { source ->
                    val hashColor = source.name.hashCode().toLong() and 0xFFFFFFFFL
                    val r = ((hashColor and 0xFF0000) shr 16).toInt()
                    val g = ((hashColor and 0x00FF00) shr 8).toInt()
                    val b = (hashColor and 0x0000FF).toInt()
                    val badgeColor = Color(red = r, green = g, blue = b, alpha = 255)
                    
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = if (source.isEnabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            width = 1.dp, 
                            brush = androidx.compose.ui.graphics.SolidColor(
                                if (source.isEnabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f)
                            )
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Circular Source Badge
                            Surface(
                                modifier = Modifier.size(44.dp),
                                shape = androidx.compose.foundation.shape.CircleShape,
                                color = badgeColor.copy(alpha = 0.12f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = source.name.firstOrNull()?.toString()?.uppercase(Locale.ROOT) ?: "?",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = badgeColor
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = source.name, 
                                    style = MaterialTheme.typography.titleMedium, 
                                    fontWeight = FontWeight.Bold,
                                    color = if (source.isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = if (source.isEnabled) 1f else 0.4f),
                                        shape = MaterialTheme.shapes.small
                                    ) {
                                        Text(
                                            text = source.type, 
                                            style = MaterialTheme.typography.labelSmall, 
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = "Sync: 5m ago", 
                                        style = MaterialTheme.typography.bodySmall, 
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (source.isEnabled) 1f else 0.5f)
                                    )
                                }
                            }
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Switch(
                                    checked = source.isEnabled,
                                    onCheckedChange = { 
                                        viewModel.toggleSourceState(source) 
                                        val stateMsg = if (!source.isEnabled) "Enabled" else "Disabled"
                                        Toast.makeText(context, "${source.name} $stateMsg", Toast.LENGTH_SHORT).show()
                                    }
                                )
                                IconButton(
                                    onClick = { 
                                        viewModel.deleteSource(source.id)
                                        Toast.makeText(context, "Unsubscribed from ${source.name}", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Delete Source",
                                        tint = MaterialTheme.colorScheme.error.copy(alpha = if (source.isEnabled) 1f else 0.5f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
