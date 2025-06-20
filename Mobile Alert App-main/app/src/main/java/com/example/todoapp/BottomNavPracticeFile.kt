package com.example.todoapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavPractice() {
    var menuOpen by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val navController = rememberNavController()

    // Track current route for highlighting the correct nav item
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Sales")
                },
                actions = {
                    IconButton(onClick = {
                        menuOpen = !menuOpen
                    }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu")
                    }

                    DropdownMenu(
                        expanded = menuOpen,
                        onDismissRequest = { menuOpen = false },
                        scrollState = scrollState
                    ) {
                        DropdownMenuItem(
                            text = { Text("Add Item") },
                            leadingIcon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                            onClick = { menuOpen = false }
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                navigationItems.forEach { item ->
                    val selected = currentRoute == item.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(imageVector = item.icon, contentDescription = item.title)
                        },
                        label = {
                            Text(
                                text = item.title,
                                color = if (selected) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {}) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Screen.Home.route) {
                HomeScreen()
            }
            composable(route = Screen.Profile.route) {
                ProfileScreen()
            }
            composable(route = Screen.Cart.route) {
                CartScreen()
            }
            composable(route = Screen.Setting.route) {
                SettingScreen()
            }
        }
    }
}

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

val navigationItems = listOf(
    NavigationItem(title = "Home", icon = Icons.Default.Home, route = Screen.Home.route),
    NavigationItem(title = "Profile", icon = Icons.Default.Person, route = Screen.Profile.route),
    NavigationItem(title = "Cart", icon = Icons.Default.ShoppingCart, route = Screen.Cart.route),
    NavigationItem(title = "Setting", icon = Icons.Default.Settings, route = Screen.Setting.route)
)

sealed class Screen(val route: String) {
    object Home: Screen("home_screen")
    object Profile: Screen("profile_screen")
    object Cart: Screen("cart_screen")
    object Setting: Screen("setting_screen")
}

@Composable
fun HomeScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Home Screen", style = MaterialTheme.typography.headlineLarge)
    }
}

@Composable
fun ProfileScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Profile Screen", style = MaterialTheme.typography.headlineLarge)
    }
}

@Composable
fun CartScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Cart Screen", style = MaterialTheme.typography.headlineLarge)
    }
}

@Composable
fun SettingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Setting Screen", style = MaterialTheme.typography.headlineLarge)
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PracticePreview() {
    NavigationBar {
        NavigationBarItem(
            onClick = {},
            icon = {Icon(imageVector = Icons.Default.Home, contentDescription = "Home")},
            label = {Text("Home")},
            selected = true
        )
        NavigationBarItem(
            onClick = {},
            icon = {Icon(imageVector = ImageVector.vectorResource(R.drawable.baseline_photo_camera_24), contentDescription = "Camera")},
            label = {Text("Videos")},
            selected = false
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewBottomNavPractice() {
    BottomNavPractice()
}