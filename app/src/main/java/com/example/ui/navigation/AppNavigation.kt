package com.example.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Source
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Source
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.ArticleDetailScreen
import com.example.ui.screens.DiscoverScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NewsScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SavedScreen
import com.example.ui.screens.SourcesScreen
import com.example.viewmodel.NewsViewModel

sealed class Screen(val route: String, val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    object Splash : Screen("splash", "Splash", Icons.Filled.Home, Icons.Outlined.Home)
    object Onboarding : Screen("onboarding", "Onboarding", Icons.Filled.Home, Icons.Outlined.Home)
    object Discover : Screen("discover", "Discover", Icons.Filled.Home, Icons.Outlined.Home)
    object Saved : Screen("saved", "Saved", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder)
    object Home : Screen("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object News : Screen("news", "News", Icons.Filled.Newspaper, Icons.Outlined.Newspaper)
    object Search : Screen("search", "Search", Icons.Filled.Search, Icons.Outlined.Search)
    object Sources : Screen("sources", "Sources", Icons.Filled.Source, Icons.Outlined.Source)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.News,
    Screen.Search,
    Screen.Sources,
    Screen.Settings
)

@Composable
fun MainScreen(viewModel: NewsViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            // Only show bottom navigation on main tabs
            if (bottomNavItems.any { it.route == currentRoute }) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (currentRoute == screen.route) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.title
                                )
                            },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) },
            popEnterTransition = { fadeIn(animationSpec = tween(300)) },
            popExitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            composable(Screen.Splash.route) {
                val sources by viewModel.sources.collectAsState()
                
                com.example.ui.screens.SplashScreen(onNavigateToHome = {
                    val destination = if (sources.isEmpty()) Screen.Onboarding.route else Screen.Home.route
                    navController.navigate(destination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Onboarding.route) {
                OnboardingScreen(viewModel = viewModel, onFinish = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Home.route) { 
                HomeScreen(
                    viewModel = viewModel, 
                    onArticleClick = { articleId -> 
                        navController.navigate("article/$articleId") 
                    },
                    onSavedClick = {
                        navController.navigate(Screen.Saved.route)
                    },
                    onSearchClick = {
                        navController.navigate(Screen.Search.route)
                    }
                ) 
            }
            composable(Screen.News.route) { 
                NewsScreen(viewModel, onArticleClick = { articleId -> 
                    navController.navigate("article/$articleId") 
                }) 
            }
            composable(Screen.Search.route) { 
                SearchScreen(viewModel, onArticleClick = { articleId -> 
                    navController.navigate("article/$articleId") 
                }) 
            }
            composable(Screen.Sources.route) { 
                SourcesScreen(
                    viewModel = viewModel, 
                    onDiscoverClick = {
                        navController.navigate(Screen.Discover.route)
                    },
                    onAddSourceClick = {
                        navController.navigate("add_source")
                    }
                ) 
            }
            composable(Screen.Discover.route) {
                DiscoverScreen(viewModel = viewModel, onBack = {
                    navController.popBackStack()
                })
            }
            composable("add_source") {
                com.example.ui.screens.AddSourceScreen(viewModel = viewModel, onBack = {
                    navController.popBackStack()
                })
            }
            composable(Screen.Saved.route) {
                SavedScreen(viewModel = viewModel, onBack = {
                    navController.popBackStack()
                }, onArticleClick = { articleId ->
                    navController.navigate("article/$articleId")
                })
            }
            composable(Screen.Settings.route) { 
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigate = { route -> navController.navigate(route) }
                ) 
            }
            
            composable("categories") {
                com.example.ui.screens.CategoriesScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            
            composable("notifications") {
                com.example.ui.screens.NotificationsScreen(onBack = { navController.popBackStack() })
            }
            
            composable("offline_library") {
                com.example.ui.screens.OfflineLibraryScreen(onBack = { navController.popBackStack() })
            }
            
            composable("source_health") {
                com.example.ui.screens.SourceHealthScreen(onBack = { navController.popBackStack() })
            }
            
            composable("duplicate_detection") {
                com.example.ui.screens.DuplicateDetectionScreen(onBack = { navController.popBackStack() })
            }
            
            composable("smart_organization") {
                com.example.ui.screens.SmartOrganizationScreen(onBack = { navController.popBackStack() })
            }
            
            composable(
                route = "article/{articleId}",
                arguments = listOf(navArgument("articleId") { type = NavType.LongType })
            ) { backStackEntry ->
                val articleId = backStackEntry.arguments?.getLong("articleId") ?: return@composable
                ArticleDetailScreen(
                    articleId = articleId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
