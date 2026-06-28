package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Category
import com.example.viewmodel.NewsViewModel
import java.util.Locale

@Composable
private fun getCategoryGradient(category: String): Brush {
    val colors = when (category.lowercase(Locale.ROOT)) {
        "technology", "tech" -> listOf(Color(0xFF00C6FF), Color(0xFF0072FF))
        "science" -> listOf(Color(0xFF7F00FF), Color(0xFFE100FF))
        "world", "news" -> listOf(Color(0xFF0575E6), Color(0xFF00F260))
        "sports" -> listOf(Color(0xFFF12711), Color(0xFFF5AF19))
        "gaming" -> listOf(Color(0xFF11998E), Color(0xFF38EF7D))
        "business" -> listOf(Color(0xFF8A2387), Color(0xFFE94057), Color(0xFFF27121))
        "health" -> listOf(Color(0xFFFF0844), Color(0xFFFFB199))
        else -> listOf(Color(0xFF0A2463), Color(0xFF00E5FF))
    }
    return Brush.linearGradient(colors)
}

fun getCategoryIcon(name: String): ImageVector {
    return when (name.lowercase(Locale.ROOT)) {
        "technology", "tech" -> Icons.Filled.Computer
        "science" -> Icons.Filled.Science
        "sports" -> Icons.Filled.Sports
        "business" -> Icons.Filled.BusinessCenter
        "gaming" -> Icons.Filled.Gamepad
        "health" -> Icons.Filled.HealthAndSafety
        "world" -> Icons.Filled.Public
        else -> Icons.Filled.Public
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(viewModel: NewsViewModel, onBack: () -> Unit) {
    val categories by viewModel.categories.collectAsState()
    val context = LocalContext.current

    var showAddDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    var showEditDialog by remember { mutableStateOf<Category?>(null) }
    var editCategoryName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categories Manager", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { 
                newCategoryName = ""
                showAddDialog = true 
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Category")
            }
        }
    ) { padding ->
        if (categories.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No custom categories yet. Tap + to add one!")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categories) { category ->
                    CategoryCard(
                        category = category,
                        onRenameClick = {
                            editCategoryName = category.name
                            showEditDialog = category
                        },
                        onDeleteClick = {
                            viewModel.deleteCategory(category.id)
                            Toast.makeText(context, "Deleted category ${category.name}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        // Add Category Dialog
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Create Category") },
                text = {
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        label = { Text("Category Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newCategoryName.isNotBlank()) {
                                viewModel.addCategory(newCategoryName.trim())
                                Toast.makeText(context, "Created category ${newCategoryName.trim()}", Toast.LENGTH_SHORT).show()
                            }
                            showAddDialog = false
                        }
                    ) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Edit Category Dialog
        if (showEditDialog != null) {
            val targetCategory = showEditDialog!!
            AlertDialog(
                onDismissRequest = { showEditDialog = null },
                title = { Text("Rename Category") },
                text = {
                    OutlinedTextField(
                        value = editCategoryName,
                        onValueChange = { editCategoryName = it },
                        label = { Text("Category Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (editCategoryName.isNotBlank()) {
                                viewModel.updateCategory(targetCategory.copy(name = editCategoryName.trim()))
                                Toast.makeText(context, "Renamed category to ${editCategoryName.trim()}", Toast.LENGTH_SHORT).show()
                            }
                            showEditDialog = null
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: Category,
    onRenameClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().aspectRatio(1f),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(getCategoryGradient(category.name))
        ) {
            // Subtle overlay gradient for rich contrast and depth
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.15f), Color.Black.copy(alpha = 0.35f))
                        )
                    )
            )

            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = getCategoryIcon(category.name),
                    contentDescription = category.name,
                    modifier = Modifier.size(44.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )
            }
            
            Box(modifier = Modifier.align(Alignment.TopEnd)) {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "More", tint = Color.White)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Rename") },
                        onClick = { 
                            showMenu = false
                            onRenameClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = { 
                            showMenu = false
                            onDeleteClick()
                        }
                    )
                }
            }
        }
    }
}
