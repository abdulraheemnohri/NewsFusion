package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import java.util.Locale

// Data class for Smart Category Rules
data class SmartRule(val name: String, val condition: String, val targetCategory: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceHealthScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Source Health Monitor", fontWeight = FontWeight.Bold) },
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HealthStatCard("Working", "12", Icons.Filled.CheckCircle, Color(0xFF4CAF50), Modifier.weight(1f))
                    HealthStatCard("Slow", "2", Icons.Filled.Warning, Color(0xFFFF9800), Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HealthStatCard("Failed", "1", Icons.Filled.Error, Color(0xFFE53935), Modifier.weight(1f))
                    HealthStatCard("Removed", "0", Icons.Filled.Cancel, Color(0xFF757575), Modifier.weight(1f))
                }
            }
            
            item {
                Text(
                    "Network Sync Status",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            item {
                HealthIssueCard(
                    title = "TechCrunch RSS Feed",
                    status = "Failed to parse XML",
                    detail = "Syntax exception at character 142. Connection refused by server. Last successful sync 2 days ago.",
                    iconColor = Color(0xFFE53935),
                    icon = Icons.Filled.Error
                )
            }
            
            item {
                HealthIssueCard(
                    title = "NASA Space Science XML",
                    status = "Slow Sync Speed",
                    detail = "Average latency: 4.2 seconds. High volume of media payload elements.",
                    iconColor = Color(0xFFFF9800),
                    icon = Icons.Filled.Warning
                )
            }

            item {
                HealthIssueCard(
                    title = "Hacker News RSS Feed",
                    status = "Working Healthy",
                    detail = "Average response time: 210ms. All latest 30 articles parsed perfectly.",
                    iconColor = Color(0xFF4CAF50),
                    icon = Icons.Filled.CheckCircle
                )
            }

            item {
                HealthIssueCard(
                    title = "NYT World News",
                    status = "Working Healthy",
                    detail = "Average response time: 540ms. Filtered duplicates automatically.",
                    iconColor = Color(0xFF4CAF50),
                    icon = Icons.Filled.CheckCircle
                )
            }
        }
    }
}

@Composable
fun HealthStatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.08f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = color.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
            Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun HealthIssueCard(title: String, status: String, detail: String, iconColor: Color, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = MaterialTheme.shapes.medium,
                color = iconColor.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
                }
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(status, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = iconColor)
                Spacer(modifier = Modifier.height(4.dp))
                Text(detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuplicateDetectionScreen(onBack: () -> Unit) {
    var sameTitle by remember { mutableStateOf(true) }
    var sameUrl by remember { mutableStateOf(true) }
    var sameContent by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Duplicate Detection", fontWeight = FontWeight.Bold) },
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Smart Merge Engine", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Private offline RSS feed merging. Automatically collapse identical or highly similar news articles in your timeline to prevent list clutter.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("34 Articles", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text("Merged Today", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("14.5 MB", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text("Storage Saved", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("98.4%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text("Accuracy", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    "Merge Strategy Toggles",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
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
                            headlineContent = { Text("Compare Article Titles", fontWeight = FontWeight.SemiBold) },
                            supportingContent = { Text("Detect and combine articles with identical headings") },
                            leadingContent = {
                                Surface(
                                    modifier = Modifier.size(36.dp),
                                    shape = MaterialTheme.shapes.medium,
                                    color = Color(0xFF4CAF50).copy(alpha = 0.12f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Filled.TextFields, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(18.dp))
                                    }
                                }
                            },
                            trailingContent = { Switch(checked = sameTitle, onCheckedChange = { sameTitle = it }) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        ListItem(
                            headlineContent = { Text("Check Target URLs", fontWeight = FontWeight.SemiBold) },
                            supportingContent = { Text("Filter items linking to exactly identical URLs") },
                            leadingContent = {
                                Surface(
                                    modifier = Modifier.size(36.dp),
                                    shape = MaterialTheme.shapes.medium,
                                    color = Color(0xFF2196F3).copy(alpha = 0.12f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Filled.Link, contentDescription = null, tint = Color(0xFF2196F3), modifier = Modifier.size(18.dp))
                                    }
                                }
                            },
                            trailingContent = { Switch(checked = sameUrl, onCheckedChange = { sameUrl = it }) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        ListItem(
                            headlineContent = { Text("Fuzzy Body Comparison", fontWeight = FontWeight.SemiBold) },
                            supportingContent = { Text("De-duplicate using local description analysis") },
                            leadingContent = {
                                Surface(
                                    modifier = Modifier.size(36.dp),
                                    shape = MaterialTheme.shapes.medium,
                                    color = Color(0xFF9C27B0).copy(alpha = 0.12f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Filled.Compare, contentDescription = null, tint = Color(0xFF9C27B0), modifier = Modifier.size(18.dp))
                                    }
                                }
                            },
                            trailingContent = { Switch(checked = sameContent, onCheckedChange = { sameContent = it }) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartOrganizationScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    
    // Mutable State list of rules to make it fully interactive!
    val rulesList = remember { mutableStateListOf(
        SmartRule("Rule 1", "title contains \"Android\"", "Technology"),
        SmartRule("Rule 2", "source is \"NASA Breaking News\"", "Science"),
        SmartRule("Rule 3", "content contains \"Stock Market\"", "Business")
    ) }

    var ruleNameInput by remember { mutableStateOf("") }
    var ruleConditionInput by remember { mutableStateOf("") }
    var ruleCategoryInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Smart Organization", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { 
                    ruleNameInput = "Rule ${rulesList.size + 1}"
                    ruleConditionInput = ""
                    ruleCategoryInput = ""
                    showAddDialog = true 
                },
                icon = { Icon(Icons.Filled.Add, contentDescription = "Add Rule") },
                text = { Text("Add Rule") }
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
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Automatic Categorizer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Automatically assign categories to incoming RSS articles. Rules match title, body content, or specific RSS feeds local metadata.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Categorization Rules",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${rulesList.size} Rules",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (rulesList.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        Text("No categorization rules set up. Add one below!")
                    }
                }
            } else {
                items(rulesList) { rule ->
                    Card(
                        modifier = Modifier.fillMaxWidth().animateContentSize(),
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Rule, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(rule.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }
                                IconButton(
                                    onClick = { 
                                        rulesList.remove(rule)
                                        Toast.makeText(context, "Removed ${rule.name}", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "IF  ${rule.condition}", 
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "THEN Move → ${rule.targetCategory.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }}", 
                                style = MaterialTheme.typography.bodyMedium, 
                                fontWeight = FontWeight.Bold, 
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Create Classification Rule") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = ruleNameInput,
                        onValueChange = { ruleNameInput = it },
                        label = { Text("Rule Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = ruleConditionInput,
                        onValueChange = { ruleConditionInput = it },
                        label = { Text("IF Condition (e.g., title contains \"AI\")") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("title contains \"AI\"") }
                    )
                    OutlinedTextField(
                        value = ruleCategoryInput,
                        onValueChange = { ruleCategoryInput = it },
                        label = { Text("THEN Category Destination") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Technology") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (ruleNameInput.isNotBlank() && ruleConditionInput.isNotBlank() && ruleCategoryInput.isNotBlank()) {
                            rulesList.add(SmartRule(ruleNameInput, ruleConditionInput, ruleCategoryInput))
                            Toast.makeText(context, "Created custom rule $ruleNameInput", Toast.LENGTH_SHORT).show()
                            showAddDialog = false
                        } else {
                            Toast.makeText(context, "Please fill out all fields!", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Add Rule")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
