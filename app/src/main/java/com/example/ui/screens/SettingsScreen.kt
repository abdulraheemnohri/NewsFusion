package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.viewmodel.NewsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: NewsViewModel, onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Observe flows from ViewModel
    val darkTheme by viewModel.darkTheme.collectAsState()
    val amoledMode by viewModel.amoledMode.collectAsState()
    val dynamicColor by viewModel.dynamicColor.collectAsState()
    val fontSize by viewModel.fontSize.collectAsState()
    val defaultReaderMode by viewModel.defaultReaderMode.collectAsState()
    val autoDownloadImages by viewModel.autoDownloadImages.collectAsState()
    val autoMarkRead by viewModel.autoMarkRead.collectAsState()
    val wifiOnly by viewModel.wifiOnly.collectAsState()
    val backgroundUpdates by viewModel.backgroundUpdates.collectAsState()
    val autoCleanup by viewModel.autoCleanup.collectAsState()

    // Dialog control states
    var showOpmlImportDialog by remember { mutableStateOf(false) }
    var opmlImportText by remember { mutableStateOf("") }

    var showBackupDialog by remember { mutableStateOf(false) }
    var backupJsonString by remember { mutableStateOf("") }

    var showRestoreDialog by remember { mutableStateOf(false) }
    var restoreJsonString by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
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
                SettingsCategoryHeader("Appearance")
                OutlinedCard(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)))
                ) {
                    Column {
                        SettingsBadgeItem(
                            title = "Dark Theme",
                            icon = Icons.Filled.DarkMode,
                            badgeColor = Color(0xFF3F51B5),
                            trailing = { Switch(checked = darkTheme, onCheckedChange = { viewModel.setDarkTheme(it) }) },
                            onClick = { viewModel.setDarkTheme(!darkTheme) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        SettingsBadgeItem(
                            title = "AMOLED Mode",
                            icon = Icons.Filled.Opacity,
                            badgeColor = Color(0xFF212121),
                            trailing = { Switch(checked = amoledMode, onCheckedChange = { viewModel.setAmoledMode(it) }) },
                            onClick = { viewModel.setAmoledMode(!amoledMode) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        SettingsBadgeItem(
                            title = "Dynamic Color",
                            icon = Icons.Filled.ColorLens,
                            badgeColor = Color(0xFFE91E63),
                            trailing = { Switch(checked = dynamicColor, onCheckedChange = { viewModel.setDynamicColor(it) }) },
                            onClick = { viewModel.setDynamicColor(!dynamicColor) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        SettingsBadgeItem(
                            title = "Font Size",
                            subtitle = fontSize,
                            icon = Icons.Filled.TextFields,
                            badgeColor = Color(0xFF009688),
                            trailing = { Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                            onClick = {
                                val nextSize = when (fontSize) {
                                    "Small" -> "Medium"
                                    "Medium" -> "Large"
                                    else -> "Small"
                                }
                                viewModel.setFontSize(nextSize)
                            }
                        )
                    }
                }
            }

            item {
                SettingsCategoryHeader("App Features")
                OutlinedCard(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)))
                ) {
                    Column {
                        SettingsBadgeItem(
                            title = "Categories",
                            subtitle = "Manage news categories",
                            icon = Icons.Filled.Category,
                            badgeColor = Color(0xFFFF9800),
                            trailing = { Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                            onClick = { onNavigate("categories") }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        SettingsBadgeItem(
                            title = "Notifications",
                            subtitle = "Alerts and digests",
                            icon = Icons.Filled.Notifications,
                            badgeColor = Color(0xFF9C27B0),
                            trailing = { Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                            onClick = { onNavigate("notifications") }
                        )
                    }
                }
            }

            item {
                SettingsCategoryHeader("Reading")
                OutlinedCard(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)))
                ) {
                    Column {
                        SettingsBadgeItem(
                            title = "Default Reader Mode",
                            icon = Icons.Filled.Book,
                            badgeColor = Color(0xFF00BCD4),
                            trailing = { Switch(checked = defaultReaderMode, onCheckedChange = { viewModel.setDefaultReaderMode(it) }) },
                            onClick = { viewModel.setDefaultReaderMode(!defaultReaderMode) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        SettingsBadgeItem(
                            title = "Auto Download Images",
                            icon = Icons.Filled.Image,
                            badgeColor = Color(0xFF4CAF50),
                            trailing = { Switch(checked = autoDownloadImages, onCheckedChange = { viewModel.setAutoDownloadImages(it) }) },
                            onClick = { viewModel.setAutoDownloadImages(!autoDownloadImages) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        SettingsBadgeItem(
                            title = "Auto Mark Read",
                            icon = Icons.Filled.DoneAll,
                            badgeColor = Color(0xFF8BC34A),
                            trailing = { Switch(checked = autoMarkRead, onCheckedChange = { viewModel.setAutoMarkRead(it) }) },
                            onClick = { viewModel.setAutoMarkRead(!autoMarkRead) }
                        )
                    }
                }
            }

            item {
                SettingsCategoryHeader("Network")
                OutlinedCard(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)))
                ) {
                    Column {
                        SettingsBadgeItem(
                            title = "WiFi Only",
                            icon = Icons.Filled.Wifi,
                            badgeColor = Color(0xFF2196F3),
                            trailing = { Switch(checked = wifiOnly, onCheckedChange = { viewModel.setWifiOnly(it) }) },
                            onClick = { viewModel.setWifiOnly(!wifiOnly) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        SettingsBadgeItem(
                            title = "Background Updates",
                            icon = Icons.Filled.Sync,
                            badgeColor = Color(0xFF673AB7),
                            trailing = { Switch(checked = backgroundUpdates, onCheckedChange = { viewModel.setBackgroundUpdates(it) }) },
                            onClick = { viewModel.setBackgroundUpdates(!backgroundUpdates) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        SettingsBadgeItem(
                            title = "Manual Sync",
                            subtitle = "Tap to sync feeds now",
                            icon = Icons.Filled.Refresh,
                            badgeColor = Color(0xFF03A9F4),
                            trailing = { Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                            onClick = {
                                viewModel.refreshFeeds()
                                Toast.makeText(context, "Syncing feeds...", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }

            item {
                SettingsCategoryHeader("Storage")
                OutlinedCard(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)))
                ) {
                    Column {
                        SettingsBadgeItem(
                            title = "Offline Library",
                            subtitle = "Manage downloaded content",
                            icon = Icons.Filled.DownloadDone,
                            badgeColor = Color(0xFFE91E63),
                            trailing = { Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                            onClick = { onNavigate("offline_library") }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        SettingsBadgeItem(
                            title = "Auto Cleanup",
                            icon = Icons.Filled.CleaningServices,
                            badgeColor = Color(0xFF795548),
                            trailing = { Switch(checked = autoCleanup, onCheckedChange = { viewModel.setAutoCleanup(it) }) },
                            onClick = { viewModel.setAutoCleanup(!autoCleanup) }
                        )
                    }
                }
            }

            item {
                SettingsCategoryHeader("Sources Import & Export")
                OutlinedCard(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)))
                ) {
                    Column {
                        SettingsBadgeItem(
                            title = "Import OPML",
                            subtitle = "Import subscription feeds",
                            icon = Icons.Filled.Backup,
                            badgeColor = Color(0xFF607D8B),
                            trailing = { Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                            onClick = {
                                opmlImportText = ""
                                showOpmlImportDialog = true
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        SettingsBadgeItem(
                            title = "Export OPML Subscriptions",
                            subtitle = "Copy feeds OPML text",
                            icon = Icons.Filled.Restore,
                            badgeColor = Color(0xFF455A64),
                            trailing = { Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                            onClick = {
                                val opml = viewModel.exportOpml()
                                clipboardManager.setText(AnnotatedString(opml))
                                Toast.makeText(context, "OPML copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        SettingsBadgeItem(
                            title = "Backup Data",
                            subtitle = "Export database to JSON",
                            icon = Icons.Filled.CloudUpload,
                            badgeColor = Color(0xFF37474F),
                            trailing = { Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                            onClick = {
                                backupJsonString = viewModel.backupData()
                                showBackupDialog = true
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        SettingsBadgeItem(
                            title = "Restore Backup",
                            subtitle = "Paste backup JSON text",
                            icon = Icons.Filled.CloudDownload,
                            badgeColor = Color(0xFF263238),
                            trailing = { Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                            onClick = {
                                restoreJsonString = ""
                                showRestoreDialog = true
                            }
                        )
                    }
                }
            }

            item {
                SettingsCategoryHeader("Advanced Toggles")
                OutlinedCard(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)))
                ) {
                    Column {
                        SettingsBadgeItem(
                            title = "Source Health Monitor",
                            subtitle = "Working, Slow, Failed, Removed status",
                            icon = Icons.Filled.HealthAndSafety,
                            badgeColor = Color(0xFFE53935),
                            trailing = { Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                            onClick = { onNavigate("source_health") }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        SettingsBadgeItem(
                            title = "Duplicate Detection",
                            subtitle = "Same title, URL, similar body criteria",
                            icon = Icons.Filled.CopyAll,
                            badgeColor = Color(0xFFFB8C00),
                            trailing = { Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                            onClick = { onNavigate("duplicate_detection") }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        SettingsBadgeItem(
                            title = "Smart Organization",
                            subtitle = "Automated rule-based categorization",
                            icon = Icons.Filled.AutoAwesome,
                            badgeColor = Color(0xFF3949AB),
                            trailing = { Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                            onClick = { onNavigate("smart_organization") }
                        )
                    }
                }
            }

            item {
                SettingsCategoryHeader("Privacy Controls")
                OutlinedCard(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)))
                ) {
                    Column {
                        SettingsBadgeItem(
                            title = "Clear History",
                            subtitle = "Reset all read article flags",
                            icon = Icons.Filled.History,
                            badgeColor = Color(0xFFFFB300),
                            trailing = { Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                            onClick = {
                                viewModel.clearHistory()
                                Toast.makeText(context, "History cleared!", Toast.LENGTH_SHORT).show()
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        SettingsBadgeItem(
                            title = "Clear Cache",
                            subtitle = "Remove non-bookmarked items",
                            icon = Icons.Filled.DeleteSweep,
                            badgeColor = Color(0xFFF4511E),
                            trailing = { Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                            onClick = {
                                viewModel.clearCache()
                                Toast.makeText(context, "Cached feeds cleared!", Toast.LENGTH_SHORT).show()
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        SettingsBadgeItem(
                            title = "Delete Database",
                            subtitle = "Reset all app databases completely",
                            icon = Icons.Filled.DeleteForever,
                            badgeColor = Color(0xFFD81B60),
                            trailing = { Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                            onClick = {
                                viewModel.deleteDatabase()
                                Toast.makeText(context, "All databases wiped!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }

            item {
                SettingsCategoryHeader("About")
                OutlinedCard(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)))
                ) {
                    Column {
                        SettingsBadgeItem(
                            title = "Version",
                            subtitle = "1.0.0",
                            icon = Icons.Filled.Info,
                            badgeColor = Color(0xFF00ACC1)
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        SettingsBadgeItem(
                            title = "GitHub Repository",
                            subtitle = "newsfusion-android",
                            icon = Icons.Filled.Code,
                            badgeColor = Color(0xFF5E35B1),
                            trailing = { Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                            onClick = {
                                try {
                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com"))
                                    context.startActivity(browserIntent)
                                } catch (e: Exception) {}
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        SettingsBadgeItem(
                            title = "Donate / Support",
                            subtitle = "Support offline RSS aggregator",
                            icon = Icons.Filled.Favorite,
                            badgeColor = Color(0xFFE91E63),
                            trailing = { Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                            onClick = {
                                try {
                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://ko-fi.com"))
                                    context.startActivity(browserIntent)
                                } catch (e: Exception) {}
                            }
                        )
                    }
                }
            }
        }
    }

    // OPML Import Dialog
    if (showOpmlImportDialog) {
        AlertDialog(
            onDismissRequest = { showOpmlImportDialog = false },
            title = { Text("Import OPML") },
            text = {
                Column {
                    Text("Paste subscription outline OPML XML text below:", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = opmlImportText,
                        onValueChange = { opmlImportText = it },
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        placeholder = { Text("<opml>...</opml>") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (opmlImportText.isNotBlank()) {
                            viewModel.importOpml(opmlImportText)
                            Toast.makeText(context, "OPML feeds imported successfully!", Toast.LENGTH_SHORT).show()
                        }
                        showOpmlImportDialog = false
                    }
                ) {
                    Text("Import")
                }
            },
            dismissButton = {
                TextButton(onClick = { showOpmlImportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Backup Dialog
    if (showBackupDialog) {
        AlertDialog(
            onDismissRequest = { showBackupDialog = false },
            title = { Text("Exported JSON Backup") },
            text = {
                Column {
                    Text("Copy this backup text to keep offline:", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = backupJsonString,
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        readOnly = true,
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    clipboardManager.setText(AnnotatedString(backupJsonString))
                    Toast.makeText(context, "JSON copied to clipboard!", Toast.LENGTH_SHORT).show()
                    showBackupDialog = false
                }) {
                    Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy JSON")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBackupDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Restore Dialog
    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("Restore JSON Backup") },
            text = {
                Column {
                    Text("Paste your exported JSON data below to restore configuration:", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = restoreJsonString,
                        onValueChange = { restoreJsonString = it },
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        placeholder = { Text("{\"sources\":[...]}") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (restoreJsonString.isNotBlank()) {
                            viewModel.restoreBackup(restoreJsonString)
                            Toast.makeText(context, "App configuration restored!", Toast.LENGTH_SHORT).show()
                        }
                        showRestoreDialog = false
                    }
                ) {
                    Text("Restore")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingsCategoryHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 6.dp)
    )
}

@Composable
fun SettingsBadgeItem(
    title: String,
    subtitle: String = "",
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badgeColor: androidx.compose.ui.graphics.Color,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    ListItem(
        modifier = modifier,
        headlineContent = { Text(title, fontWeight = FontWeight.SemiBold) },
        supportingContent = if (subtitle.isNotEmpty()) { { Text(subtitle, style = MaterialTheme.typography.bodyMedium) } } else null,
        leadingContent = {
            Surface(
                modifier = Modifier.size(38.dp),
                shape = MaterialTheme.shapes.medium,
                color = badgeColor.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = badgeColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        trailingContent = trailing,
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}
