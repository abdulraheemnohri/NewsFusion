package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.data.local.AppDatabase
import com.example.data.repository.NewsRepository
import com.example.ui.navigation.MainScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.NewsViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    val database = AppDatabase.getDatabase(this)
    val repository = NewsRepository(database.newsDao())
    val viewModel = NewsViewModel(repository)
    
    setContent {
      val darkTheme by viewModel.darkTheme.collectAsState()
      val amoledMode by viewModel.amoledMode.collectAsState()
      val dynamicColor by viewModel.dynamicColor.collectAsState()
      
      MyApplicationTheme(
          darkTheme = darkTheme,
          amoledMode = amoledMode,
          dynamicColor = dynamicColor
      ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            MainScreen(viewModel = viewModel)
        }
      }
    }
  }
}
