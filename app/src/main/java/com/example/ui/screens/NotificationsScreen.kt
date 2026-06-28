package com.example.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(onBack: () -> Unit) {
    var breakingNews by remember { mutableStateOf(true) }
    var favoriteSources by remember { mutableStateOf(true) }
    var keywords by remember { mutableStateOf(false) }
    var morningDigest by remember { mutableStateOf(true) }
    var eveningDigest by remember { mutableStateOf(false) }
    var quietHours by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notification Center", fontWeight = FontWeight.Bold) },
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
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Text(
                    text = "Immediate Alerts",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 6.dp)
                )
                
                OutlinedCard(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)))
                ) {
                    Column {
                        ListItem(
                            headlineContent = { Text("Breaking News Alerts", fontWeight = FontWeight.SemiBold) },
                            supportingContent = { Text("M3 push notifications for vital topics") },
                            leadingContent = {
                                Surface(
                                    modifier = Modifier.size(38.dp),
                                    shape = MaterialTheme.shapes.medium,
                                    color = Color(0xFFFF5252).copy(alpha = 0.12f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Filled.Campaign, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(20.dp))
                                    }
                                }
                            },
                            trailingContent = { Switch(checked = breakingNews, onCheckedChange = { breakingNews = it }) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        ListItem(
                            headlineContent = { Text("Favorite Sources Daily Digest", fontWeight = FontWeight.SemiBold) },
                            supportingContent = { Text("Only get alerts from starred RSS list") },
                            leadingContent = {
                                Surface(
                                    modifier = Modifier.size(38.dp),
                                    shape = MaterialTheme.shapes.medium,
                                    color = Color(0xFFE91E63).copy(alpha = 0.12f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Filled.Favorite, contentDescription = null, tint = Color(0xFFE91E63), modifier = Modifier.size(20.dp))
                                    }
                                }
                            },
                            trailingContent = { Switch(checked = favoriteSources, onCheckedChange = { favoriteSources = it }) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        ListItem(
                            headlineContent = { Text("Keyword Trigger Phrases", fontWeight = FontWeight.SemiBold) },
                            supportingContent = { Text("Mute unless matching personalized text rules") },
                            leadingContent = {
                                Surface(
                                    modifier = Modifier.size(38.dp),
                                    shape = MaterialTheme.shapes.medium,
                                    color = Color(0xFF009688).copy(alpha = 0.12f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Filled.Keyboard, contentDescription = null, tint = Color(0xFF009688), modifier = Modifier.size(20.dp))
                                    }
                                }
                            },
                            trailingContent = { Switch(checked = keywords, onCheckedChange = { keywords = it }) }
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Scheduled Summaries",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 6.dp)
                )
                
                OutlinedCard(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)))
                ) {
                    Column {
                        ListItem(
                            headlineContent = { Text("Morning Digest summary", fontWeight = FontWeight.SemiBold) },
                            supportingContent = { Text("Offline notification pack at 8:00 AM") },
                            leadingContent = {
                                Surface(
                                    modifier = Modifier.size(38.dp),
                                    shape = MaterialTheme.shapes.medium,
                                    color = Color(0xFFFF9800).copy(alpha = 0.12f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Filled.LightMode, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(20.dp))
                                    }
                                }
                            },
                            trailingContent = { Switch(checked = morningDigest, onCheckedChange = { morningDigest = it }) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        ListItem(
                            headlineContent = { Text("Evening Digest summary", fontWeight = FontWeight.SemiBold) },
                            supportingContent = { Text("Offline notification pack at 6:00 PM") },
                            leadingContent = {
                                Surface(
                                    modifier = Modifier.size(38.dp),
                                    shape = MaterialTheme.shapes.medium,
                                    color = Color(0xFF673AB7).copy(alpha = 0.12f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Filled.DarkMode, contentDescription = null, tint = Color(0xFF673AB7), modifier = Modifier.size(20.dp))
                                    }
                                }
                            },
                            trailingContent = { Switch(checked = eveningDigest, onCheckedChange = { eveningDigest = it }) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        ListItem(
                            headlineContent = { Text("Quiet Night Hours", fontWeight = FontWeight.SemiBold) },
                            supportingContent = { Text("Mute all alarms 10:00 PM - 7:00 AM") },
                            leadingContent = {
                                Surface(
                                    modifier = Modifier.size(38.dp),
                                    shape = MaterialTheme.shapes.medium,
                                    color = Color(0xFF607D8B).copy(alpha = 0.12f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Filled.NotificationsOff, contentDescription = null, tint = Color(0xFF607D8B), modifier = Modifier.size(20.dp))
                                    }
                                }
                            },
                            trailingContent = { Switch(checked = quietHours, onCheckedChange = { quietHours = it }) }
                        )
                    }
                }
            }
        }
    }
}
