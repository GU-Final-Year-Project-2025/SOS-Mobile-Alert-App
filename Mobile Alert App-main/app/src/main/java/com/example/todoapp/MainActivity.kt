//package com.example.todoapp
//import android.app.Activity
//import android.content.Intent
//import android.content.IntentFilter
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.lazy.grid.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.AccountCircle
//import androidx.compose.material.icons.filled.Call
//import androidx.compose.material.icons.filled.DateRange
//import androidx.compose.material.icons.filled.Edit
//import androidx.compose.material.icons.filled.ExitToApp
//import androidx.compose.material.icons.filled.Favorite
//import androidx.compose.material.icons.filled.Info
//import androidx.compose.material.icons.filled.MoreVert
//import androidx.compose.material.icons.filled.Settings
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.vectorResource
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import com.example.todoapp.ui.theme.TodoAppTheme
//
//class MainActivity : ComponentActivity() {
//
//    private lateinit var powerButtonReceiver: PowerButtonReceiver
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        enableEdgeToEdge()
//
//        powerButtonReceiver = PowerButtonReceiver()
//
//        val intentFilter = IntentFilter().apply {
//            addAction(Intent.ACTION_SCREEN_OFF)
//            addAction(Intent.ACTION_SCREEN_ON)
//        }
//        registerReceiver(powerButtonReceiver, intentFilter)
//
//        // Register the receiver for power button events
////        registerReceiver(
////            powerButtonReceiver,
////            IntentFilter(Intent.ACTION_SCREEN_OFF)
////        )
//
//        setContent {
//            TodoAppTheme {
//                AppNavigation()
//            }
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        unregisterReceiver(powerButtonReceiver)
//    }
//}
//
//@Composable
//fun AppNavigation() {
//    val navController = rememberNavController()
//
//    NavHost(navController = navController, startDestination = "login") {
//        composable("login") { Login(navController) }
//        composable("sosMessaging") {SOSMessagingScreen(navController)}
//        composable("profile") {ProfileScreen(navController)}
//        composable("contacts") {ContactsScreen(navController)}
//        composable("activities") {ActivitiesScreen(navController)}
//        composable("settings") {SettingsScreen(navController)}
//        composable("logsDetails") { LogsDetails(navController) }
//        composable("editSOSMessaging") { EditSOSMessage(navController) }
//        composable("timer") { TimerSettingsScreen(navController) }
//        composable("forgot password") { ForgotPassword(navController) }
//        composable("signup") { SignUp(navController) }
//        composable("policeDashboard") { PoliceScreen(navController) }
//        composable("history") { History(navController) }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SOSMessagingScreen(navController: NavHostController) {
//
//    val context = LocalContext.current
//    // get the loggedIn user details
//    val userDetails = getUserData(context)
//    val userName = userDetails?.first?.fullName ?: "Guest"
//    var toggleDropdown by remember { mutableStateOf(false) }
//
//    Scaffold(
//        modifier = Modifier.fillMaxSize(),
//        topBar = {
//            TopAppBar(
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                    titleContentColor = MaterialTheme.colorScheme.primary,
//                ),
//                title = {
//                    Text(text = "Hi ${userName}", style = MaterialTheme.typography.titleLarge)
//                }, actions = {
//                    IconButton(onClick = {
//                        toggleDropdown = !toggleDropdown
//                    }) {
//                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More Details")
//                    }
//
//                    DropdownMenu(
//                        expanded = toggleDropdown,
//                        onDismissRequest = {
//                            // this is to dismiss the dropdown when tap outside
//                            toggleDropdown = !toggleDropdown
//                        }
//                    ) {
//                        DropdownMenuItem(
//                            text = {
//                                Text("Exit App")
//                            },
//                            leadingIcon = {
//                                Icon(Icons.Default.ExitToApp, contentDescription = "Exit")
//                            },
//                            onClick = {
//
//                                // this part is to dismiss and exit the app
//                                (context as? Activity)?.finishAffinity()
//
//                                // dismiss dropdown later after the
//                                toggleDropdown = !toggleDropdown
//                            }
//                        )
//
//                        DropdownMenuItem(
//                            text = {
//                                Text("Logout")
//                            },
//                            leadingIcon = {
//                                Icon(imageVector = ImageVector.vectorResource(R.drawable.baseline_exit_to_app_24), contentDescription = "Logout App")
//                            },
//                            onClick = {
//
//                                // remove everything from the SharedPreferences
//                                clearUserData(context)
//
//                                // navigate to the login page
//                                navController.navigate("login") {
//                                    popUpTo(0) { inclusive = true } // Clears the entire back stack
//                                    launchSingleTop = true // Avoid multiple instances
//                                }
//                            }
//                        )
//                    }
//                }
//            )
//        }
//    ) { innerPadding ->
//        LazyColumn(
//            modifier = Modifier
//                .padding(innerPadding)
//                .padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            // SOS Message Card
//            item {
//                Card(
//                    modifier = Modifier.fillMaxWidth(),
//                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//                ) {
//                    Row(
//                        modifier = Modifier
//                            .padding(16.dp)
//                            .fillMaxWidth(),
//                        verticalAlignment = Alignment.Top,
//                        horizontalArrangement = Arrangement.spacedBy(20.dp)
//                    ) {
//                        Box(modifier = Modifier.clip(RoundedCornerShape(10.dp))) {
//                            Image(
//                                painter = painterResource(id = R.drawable.logo),
//                                contentDescription = "SOS Icon",
//                                modifier = Modifier.size(100.dp)
//                            )
//                        }
//                        Column {
//                            Text(
//                                text = "SOS Alert",
//                                style = MaterialTheme.typography.titleLarge
//                            )
//                            Text(
//                                text = "be safe, be alert",
//                                style = MaterialTheme.typography.titleMedium
//                            )
//                        }
//                    }
//                }
//            }
//
//            // Text
//            item { Text(text = "All Menu") }
//
//            // Menu Grid
//            item {
//                LazyVerticalGrid(
//                    columns = GridCells.Fixed(2),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .heightIn(max = 800.dp),
//                    horizontalArrangement = Arrangement.spacedBy(16.dp),
//                    verticalArrangement = Arrangement.spacedBy(16.dp),
//                    userScrollEnabled = false
//                ) {
//                    val menuItems = listOf(
//                        MenuItem("Profile", Icons.Default.AccountCircle, "profile"),
//                        MenuItem("Contacts", Icons.Default.Call, "contacts"),
//                        MenuItem("Report", Icons.Default.Info, "activities"),
//                        MenuItem("History", Icons.Default.Favorite, "history"),
//                        MenuItem("Edit Timer", Icons.Default.DateRange, "timer"),
//                        MenuItem("Edit SOS Messages", Icons.Default.Edit, "editSOSMessaging")
//                    )
//
//                    items(menuItems) { item ->
//                        MenuCard(item, navController)
//                    }
//
//                   item { Spacer(modifier = Modifier.height(10.dp)) }
//                }
//            }
//        }
//    }
//}
//
//data class MenuItem(
//    val title: String,
//    val icon: ImageVector,
//    val route: String
//)
//
//@Composable
//fun MenuCard(item: MenuItem, navController: NavHostController) {
//    Card(
//        onClick = {
//            navController.navigate(item.route)
//        },
//        modifier = Modifier
//            .fillMaxWidth()
//            .aspectRatio(1f),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxSize(),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Icon(
//                imageVector = item.icon,
//                contentDescription = item.title,
//                modifier = Modifier.size(55.dp),
//                tint = MaterialTheme.colorScheme.primary
//            )
//            Spacer(modifier = Modifier.height(15.dp))
//            Text(
//                text = item.title,
//                style = MaterialTheme.typography.bodyLarge
//            )
//        }
//    }
//}








package com.example.todoapp
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todoapp.ui.theme.TodoAppTheme

class MainActivity : ComponentActivity() {

    private lateinit var powerButtonReceiver: PowerButtonReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        powerButtonReceiver = PowerButtonReceiver()

        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
        }
        registerReceiver(powerButtonReceiver, intentFilter)

        setContent {
            TodoAppTheme {
                AppNavigation()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(powerButtonReceiver)
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { Login(navController) }
        composable("sosMessaging") { SOSMessagingScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("contacts") { ContactsScreen(navController) }
        composable("activities") { ActivitiesScreen(navController) }
        composable("settings") { SettingsScreen(navController) }
        composable("logsDetails") { LogsDetails(navController) }
        composable("editSOSMessaging") { EditSOSMessage(navController) }
        composable("timer") { TimerSettingsScreen(navController) }
        composable("forgot password") { ForgotPassword(navController) }
        composable("signup") { SignUp(navController) }
        composable("policeDashboard") { PoliceScreen(navController) }
        composable("history") { History(navController) }
        composable("informations") { Info(navController) }
        composable("adminDashboard") { AdminDashboard(navController) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SOSMessagingScreen(navController: NavHostController) {

    val context = LocalContext.current
    // get the loggedIn user details
    val userDetails = getUserData(context)
    val userName = userDetails?.first?.fullName ?: "Guest"
    var toggleDropdown by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(text = "Hi ${userName}", style = MaterialTheme.typography.titleLarge)
                }, actions = {
                    IconButton(onClick = {
                        toggleDropdown = !toggleDropdown
                    }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More Details")
                    }

                    DropdownMenu(
                        expanded = toggleDropdown,
                        onDismissRequest = {
                            // this is to dismiss the dropdown when tap outside
                            toggleDropdown = !toggleDropdown
                        }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text("Exit App")
                            },
                            leadingIcon = {
                                Icon(Icons.Default.ExitToApp, contentDescription = "Exit")
                            },
                            onClick = {

                                // this part is to dismiss and exit the app
                                (context as? Activity)?.finishAffinity()

                                // dismiss dropdown later after the
                                toggleDropdown = !toggleDropdown
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text("Logout")
                            },
                            leadingIcon = {
                                Icon(imageVector = ImageVector.vectorResource(R.drawable.baseline_exit_to_app_24), contentDescription = "Logout App")
                            },
                            onClick = {

                                // remove everything from the SharedPreferences
                                clearUserData(context)

                                // navigate to the login page
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true } // Clears the entire back stack
                                    launchSingleTop = true // Avoid multiple instances
                                }
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                contentColor = Color.Black
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = { /* Handle feedback */ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_thumb_up_24),
                                contentDescription = "Feedback"
                            )
                        }
                        Text("Feedback", fontSize = 12.sp)
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = { /* Handle share */ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_share_24),
                                contentDescription = "Share"
                            )
                        }
                        Text("Share", fontSize = 12.sp)
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = { /* Handle rate us */ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_star_rate_24),
                                contentDescription = "Rate us"
                            )
                        }
                        Text("Rate us", fontSize = 12.sp)
                    }
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        IconButton(onClick = { /* Handle contribute */ }) {
//                            Icon(
//                                painter = painterResource(id = R.drawable.baseline_thumb_up_off_alt_24),
//                                contentDescription = "Contribute"
//                            )
//                        }
//                        Text("Contribute", fontSize = 12.sp)
//                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // SOS Alert Card
            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Red
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
//                        Image(
//                            painter = painterResource(id = R.drawable.logo),
//                            contentDescription = "SOS Icon",
//                            modifier = Modifier.size(50.dp)
//                        )
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color.Red),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "SOS",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "SOS ALERT",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "be safe, be alert",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Menu Grid - Fixed with a specific height instead of using LazyVerticalGrid inside LazyColumn
            val menuItems = listOf(
                MenuItem("Info", MenuIcon.DrawableIcon(R.drawable.baseline_info_outline_24), "informations"),
                MenuItem("Edit Profile", MenuIcon.DrawableIcon(R.drawable.user), "profile"),
                MenuItem("Register Number", MenuIcon.DrawableIcon(R.drawable.baseline_add_circle_24), "contacts"),
                MenuItem("Record Video", MenuIcon.DrawableIcon(R.drawable.baseline_video_camera_back_24), "activities"),
                MenuItem("Edit SOS Message", MenuIcon.DrawableIcon(R.drawable.edit), "editSOSMessaging"),
                MenuItem("Edit Timer", MenuIcon.DrawableIcon(R.drawable.baseline_timer_24), "timer"),
                MenuItem("SOS History", MenuIcon.DrawableIcon(R.drawable.baseline_work_history_24), "history")
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                userScrollEnabled = false
            ) {
                items(menuItems) { item ->
                    MenuCard(item, navController)
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Contribute Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.Red),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "SOS",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text(
                            text = "CONTRIBUTE",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "YOUR SUPPORT MATTERS—CONTRIBUTE TODAY!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

sealed class MenuIcon {
    data class VectorIcon(val imageVector: ImageVector) : MenuIcon()
    data class DrawableIcon(val drawableId: Int) : MenuIcon()
}

data class MenuItem(
    val title: String,
    val icon: MenuIcon,
    val route: String
)

@Composable
fun MenuCard(item: MenuItem, navController: NavHostController) {
    Card(
        onClick = {
            navController.navigate(item.route)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp).padding(bottom = 5.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Green),
                contentAlignment = Alignment.Center
            ) {
                when(item.icon) {
                    is MenuIcon.VectorIcon -> {
                        Icon(
                            imageVector = item.icon.imageVector,
                            contentDescription = item.title,
                            tint = Color.White
                        )
                    }
                    is MenuIcon.DrawableIcon -> {
                        Icon(
                            painter = painterResource(id = item.icon.drawableId),
                            contentDescription = item.title,
                            tint = Color.White
                        )
                    }
                }
            }
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}