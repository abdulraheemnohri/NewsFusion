package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineLibraryScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var isClearing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Offline Library", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        Toast.makeText(context, "Scanning library storage...", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Filled.Checklist, contentDescription = "Select")
                    }
                    IconButton(onClick = { 
                        Toast.makeText(context, "Cleaning up expired images...", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Filled.CleaningServices, contentDescription = "Cleanup")
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Local Storage Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("10.7% Used", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        LinearProgressIndicator(
                            progress = { 2.4f / 22.4f },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f)
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).background(MaterialTheme.colorScheme.primary, shape = androidx.compose.foundation.shape.CircleShape))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("NewsFusion: 2.4 GB", style = MaterialTheme.typography.labelMedium)
                            }
                            Text("Free: 20 GB", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            item {
                Text(
                    "Cached Content Types",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)))
                ) {
                    Column {
                        ListItem(
                            headlineContent = { Text("Articles Cache", fontWeight = FontWeight.SemiBold) },
                            supportingContent = { Text("154 text articles downloaded locally") },
                            leadingContent = {
                                Surface(
                                    modifier = Modifier.size(38.dp),
                                    shape = MaterialTheme.shapes.medium,
                                    color = Color(0xFF3F51B5).copy(alpha = 0.12f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Filled.Newspaper, contentDescription = null, tint = Color(0xFF3F51B5), modifier = Modifier.size(20.dp))
                                    }
                                }
                            },
                            trailingContent = { Text("1.2 GB", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        ListItem(
                            headlineContent = { Text("Media & Images Cache", fontWeight = FontWeight.SemiBold) },
                            supportingContent = { Text("Pre-fetched visual story banners") },
                            leadingContent = {
                                Surface(
                                    modifier = Modifier.size(38.dp),
                                    shape = MaterialTheme.shapes.medium,
                                    color = Color(0xFFE91E63).copy(alpha = 0.12f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Filled.Image, contentDescription = null, tint = Color(0xFFE91E63), modifier = Modifier.size(20.dp))
                                    }
                                }
                            },
                            trailingContent = { Text("800 MB", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        ListItem(
                            headlineContent = { Text("Full Offline Pages", fontWeight = FontWeight.SemiBold) },
                            supportingContent = { Text("Parsed readable HTML captures") },
                            leadingContent = {
                                Surface(
                                    modifier = Modifier.size(38.dp),
                                    shape = MaterialTheme.shapes.medium,
                                    color = Color(0xFF009688).copy(alpha = 0.12f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Filled.Language, contentDescription = null, tint = Color(0xFF009688), modifier = Modifier.size(20.dp))
                                    }
                                }
                            },
                            trailingContent = { Text("400 MB", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        isClearing = true
                        Toast.makeText(context, "All non-saved caches cleared successfully!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Icon(Icons.Filled.DeleteSweep, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Empty Offline Cache")
                }
            }
        }
    }
}
