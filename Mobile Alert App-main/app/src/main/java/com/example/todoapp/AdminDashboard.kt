package com.example.todoapp

import MedicalRegistrationSection
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(navController: NavHostController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedSection by remember { mutableStateOf("Dashboard") }
    val context = LocalContext.current

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AdminDrawerContent(
                selectedSection = selectedSection,
                onSectionSelected = { section ->
                    selectedSection = section
                    scope.launch { drawerState.close() }
                },
                onLogout = {
                    clearUserData(context)
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                AdminTopBar(
                    onMenuClick = { scope.launch { drawerState.open() } },
                    title = selectedSection
                )
            }
        ) { paddingValues ->
            AdminContent(
                modifier = Modifier.padding(paddingValues),
                selectedSection = selectedSection,
                navController = navController
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTopBar(
    onMenuClick: () -> Unit,
    title: String
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_segment_24),
                    contentDescription = "Menu"
                )
            }
        },
        actions = {
            IconButton(onClick = { /* Handle notifications */ }) {
                Badge {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_add_alert_24),
                        contentDescription = "Notifications"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
fun AdminDrawerContent(
    selectedSection: String,
    onSectionSelected: (String) -> Unit,
    onLogout: () -> Unit
) {
    val menuItems = listOf(
        DrawerMenuItem("Dashboard", R.drawable.baseline_space_dashboard_24),
        DrawerMenuItem("Register Users", R.drawable.baseline_add_location_24),
        DrawerMenuItem("Register Police", R.drawable.baseline_security_24),
        DrawerMenuItem("Register Medical Units", R.drawable.baseline_miscellaneous_services_24),
        DrawerMenuItem("Manage Users", R.drawable.baseline_supervised_user_circle_24),
        DrawerMenuItem("Reports", R.drawable.baseline_stacked_line_chart_24),
        DrawerMenuItem("Settings", R.drawable.baseline_settings_24)
    )

    ModalDrawerSheet(
        modifier = Modifier.width(280.dp)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_settings_24),
                        contentDescription = "Admin",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(35.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "System Administrator",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "admin@gmail.com",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Menu Items
        LazyColumn {
            items(menuItems) { item ->
                NavigationDrawerItem(
                    label = { Text(item.title) },
                    icon = {
                        Icon(
                            painter = painterResource(id = item.iconRes),
                            contentDescription = item.title
                        )
                    },
                    selected = selectedSection == item.title,
                    onClick = { onSectionSelected(item.title) },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))

                // Logout
                NavigationDrawerItem(
                    label = { Text("Logout") },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_exit_to_app_24),
                            contentDescription = "Logout"
                        )
                    },
                    selected = false,
                    onClick = onLogout,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedIconColor = MaterialTheme.colorScheme.error,
                        unselectedTextColor = MaterialTheme.colorScheme.error
                    )
                )
            }
        }
    }
}

@Composable
fun AdminContent(
    modifier: Modifier = Modifier,
    selectedSection: String,
    navController: NavHostController
) {
    when (selectedSection) {
        "Dashboard" -> DashboardOverview(modifier, navController)
        "Register Users" -> UserRegistrationSection(modifier)
        "Register Police" -> PoliceRegistrationSection(modifier)
        "Register Medical Units" -> MedicalRegistrationSection(modifier)
        "Manage Users" -> UserManagementSection(modifier)
        "Reports" -> ReportsSection(modifier)
        "Settings" -> SettingsSection(modifier)
    }
}

@Composable
fun DashboardOverview(modifier: Modifier = Modifier, navController: NavHostController) {
    val statsCards = listOf(
        StatsCard("Total Users", "1,234", R.drawable.user, Color(0xFF4CAF50)),
        StatsCard("Police Units", "45", R.drawable.baseline_security_24, Color(0xFF2196F3)),
        StatsCard("Medical Units", "23", R.drawable.baseline_miscellaneous_services_24, Color(0xFFF44336)),
        StatsCard("Active Alerts", "8", R.drawable.baseline_warning_24, Color(0xFFFF9800))
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Admin Dashboard Overview",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(200.dp)
            ) {
                items(statsCards) { card ->
                    StatsCardItem(card)
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Quick Actions",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    QuickActionButton(
                        text = "Register New User",
                        iconRes = R.drawable.baseline_person_add_alt_1_24,
                        onClick = { /* Navigate or handle action */ }
                    )

                    QuickActionButton(
                        text = "Add Police Unit",
                        iconRes = R.drawable.baseline_security_24,
                        onClick = { /* Navigate or handle action */ }
                    )

                    QuickActionButton(
                        text = "Add Medical Unit",
                        iconRes = R.drawable.baseline_miscellaneous_services_24,
                        onClick = { navController.navigate("") }
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Recent Activity",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    ActivityItem("User John Doe registered", "2 hours ago")
                    ActivityItem("Police Unit #34 added", "4 hours ago")
                    ActivityItem("Medical alert resolved", "6 hours ago")
                }
            }
        }
    }
}

@Composable
fun UserRegistrationSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Register New User",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("User registration form will be implemented here")
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { /* Implement user registration */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Register User")
                }
            }
        }
    }
}

@Composable
fun PoliceRegistrationSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Register Police Unit",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Police unit registration form will be implemented here")
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { /* Implement police registration */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Register Police Unit")
                }
            }
        }
    }
}


@Composable
fun UserManagementSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "User Management",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("User management interface will be implemented here")
                Text("- View all users")
                Text("- Edit user details")
                Text("- Deactivate/activate users")
                Text("- View user activity")
            }
        }
    }
}

@Composable
fun ReportsSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Reports & Analytics",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Reports and analytics will be implemented here")
                Text("- User registration trends")
                Text("- Alert statistics")
                Text("- Response times")
                Text("- System usage reports")
            }
        }
    }
}

@Composable
fun SettingsSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "System Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("System settings will be implemented here")
                Text("- App configuration")
                Text("- Notification settings")
                Text("- Security settings")
                Text("- Backup & restore")
            }
        }
    }
}

@Composable
fun StatsCardItem(card: StatsCard) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = card.color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = card.title,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Icon(
                    painter = painterResource(id = card.iconRes),
                    contentDescription = card.title,
                    tint = card.color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = card.value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = card.color
            )
        }
    }
}

@Composable
fun QuickActionButton(
    text: String,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = text,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}

@Composable
fun ActivityItem(
    activity: String,
    time: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = activity,
            modifier = Modifier.weight(1f),
            fontSize = 14.sp
        )
        Text(
            text = time,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

// Data classes
data class DrawerMenuItem(
    val title: String,
    @DrawableRes val iconRes: Int
)

data class StatsCard(
    val title: String,
    val value: String,
    @DrawableRes val iconRes: Int,
    val color: Color
)