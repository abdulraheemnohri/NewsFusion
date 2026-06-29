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
import com.example.ui.navigation.MainScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  
  @Inject lateinit var viewModel: NewsViewModel
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
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
