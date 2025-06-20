package com.example.todoapp

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.todoapp.local.entities.HistoryEntity
import com.example.todoapp.local.entities.VideoCapture
import com.example.todoapp.viewModel.MyviewModel
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.decoration.ThresholdLine
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.component.text.textComponent
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// Navigation routes for Police Screen
sealed class PoliceNavScreens(val route: String) {
    object AlertScreen: PoliceNavScreens("alerts_screen")
    object VideoScreen: PoliceNavScreens("videos_screen")
    object AnalyticScreen: PoliceNavScreens("analytics_screen")
}

// Enhanced navigation item to support both ImageVector and Drawable resources
data class PoliceScreenNavigationItem(
    val iconType: IconType,
    val iconResId: Any, // Can be ImageVector or Int (drawable resource ID)
    val label: String,
    val route: String
)

// Enum to determine what type of icon to display
enum class IconType {
    VECTOR, DRAWABLE
}

// Navigation items with enhanced icon support
val policeNavigationItems = listOf(
    PoliceScreenNavigationItem(
        iconType = IconType.DRAWABLE,
        iconResId = R.drawable.baseline_show_chart_24, // Replace with your analytics drawable
        label = "Analytics",
        route = PoliceNavScreens.AnalyticScreen.route
    ),
    PoliceScreenNavigationItem(

        iconType = IconType.DRAWABLE,
        iconResId = R.drawable.baseline_work_history_24, // Replace with your history drawable
        label = "History",
        route = PoliceNavScreens.AlertScreen.route
    ),
    PoliceScreenNavigationItem(
        iconType = IconType.DRAWABLE,
        iconResId = R.drawable.baseline_video_library_24, // Replace with your video drawable
        label = "Videos",
        route = PoliceNavScreens.VideoScreen.route
    )
)

// Flexible icon composable to handle both vector and drawable resources
@Composable
fun FlexibleIcon(
    iconType: IconType,
    iconResId: Any,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    when (iconType) {
        IconType.VECTOR -> {
            Icon(
                imageVector = iconResId as ImageVector,
                contentDescription = contentDescription,
                modifier = modifier,
                tint = tint
            )
        }
        IconType.DRAWABLE -> {
            Icon(
                painter = painterResource(id = iconResId as Int),
                contentDescription = contentDescription,
                modifier = modifier,
                tint = tint
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoliceScreen(navHostController: NavHostController? = null) {
    val context = LocalContext.current
    var openDropdown by remember { mutableStateOf(false) }

    // Create a nested NavController for the police section
    val policeNavController = rememberNavController()

    // Get current route for bottom navigation highlighting
    val navBackStackEntry by policeNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Police",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { openDropdown = !openDropdown }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More Options"
                        )
                    }
                    DropdownMenu(
                        expanded = openDropdown,
                        onDismissRequest = { openDropdown = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Exit App") },
                            onClick = {
                                // First logout the app
                                (context as? Activity)?.finishAffinity()

                                // Close the dropdown
                                openDropdown = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                // Removes user data after logging out
                                clearUserData(context)

                                // Navigates to the login screen
                                navHostController?.navigate("login") {
                                    popUpTo(0) { inclusive = true } // Clears the entire back stack
                                    launchSingleTop = true // Avoid multiple instances
                                }

                                // Close the dropdown
                                openDropdown = false
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
            )
        },
        bottomBar = {
            NavigationBar {
                policeNavigationItems.forEach { item ->
                    NavigationBarItem(
                        onClick = {
                            if (currentRoute != item.route) {
                                policeNavController.navigate(item.route) {
                                    // Pop up to the start destination to avoid building up a large stack
                                    popUpTo(policeNavController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination
                                    launchSingleTop = true
                                    // Restore state when navigating back
                                    restoreState = true
                                }
                            }
                        },
                        selected = currentRoute == item.route,
                        icon = {
                            FlexibleIcon(
                                iconType = item.iconType,
                                iconResId = item.iconResId,
                                contentDescription = item.label
                            )
                        },
                        label = {
                            Text(item.label)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // Fixed NavHost setup
        NavHost(
            navController = policeNavController,
            startDestination = PoliceNavScreens.AlertScreen.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = PoliceNavScreens.AlertScreen.route) {
                AlertsScreen(
                    modifier = Modifier,
                    navHostController = policeNavController
                )
            }
            composable(route = PoliceNavScreens.AnalyticScreen.route) {
                AnalyticScreen()
            }
            composable(route = PoliceNavScreens.VideoScreen.route) {
                VideoScreen()
            }
        }
    }
}

@Composable
fun AlertsScreen(
    modifier: Modifier = Modifier,
    viewModel: MyviewModel = viewModel(),
    navHostController: NavHostController?
) {
    // Here we are fetching all history data from the backend
    val historyDetails = viewModel.allHistory.collectAsState(initial = emptyList())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 5.dp, bottom = 10.dp)
    ) {
        if (historyDetails.value.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    FlexibleIcon(
                        iconType = IconType.DRAWABLE,
                        iconResId = R.drawable.baseline_add_alert_24,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No alerts available",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(historyDetails.value) { history ->
                    HistoryCard(history, navHostController)
                }
            }
        }
    }
}

//@Composable
//fun VideoScreen(modifier: Modifier = Modifier, viewModel: MyviewModel = viewModel()) {
//    // Get all the videos
//    val videos = viewModel.allVideoCaptures.collectAsState(initial = emptyList())
//
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(start = 16.dp, end = 16.dp)
//    ) {
//
//        if (videos.value.isEmpty()) {
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    FlexibleIcon(
//                        iconType = IconType.DRAWABLE,
//                        iconResId = R.drawable.baseline_photo_camera_24,
//                        contentDescription = null,
//                        modifier = Modifier.size(64.dp),
//                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
//                    )
//                    Spacer(modifier = Modifier.height(16.dp))
//                    Text(
//                        "No video recordings available",
//                        fontSize = 18.sp,
//                        color = Color.Gray
//                    )
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Text(
//                        "Video feeds will appear here when available",
//                        fontSize = 14.sp,
//                        color = Color.Gray
//                    )
//                }
//            }
//        } else {
//            LazyColumn(
//                verticalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                items(videos.value) { video ->
//                    VideoCard(
//                        title = "Camera Recording ${video.id}",
//                        description = video.description ?: "Surveillance footage",
//                        timestamp = video.dateTime ?: Date(),
//                        videoUrl = video.location ?: "",
//                        thumbnailUrl = null
//                    )
//                }
//            }
//        }
//    }
//}
//



@Composable
fun EnhancedLineGraph(
    data: List<Float>,
    title: String = "Chart",
    color: Color = Color(0xFF6200EE)
) {
    val chartEntryModel = entryModelOf(data.mapIndexed { index, value ->
        entryOf(index.toFloat(), value)
    })

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Simple working chart
        Chart(
            chart = lineChart(),
            model = chartEntryModel,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        )

        // Stats row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard("Max", data.maxOrNull()?.toString() ?: "0", Color(0xFF4CAF50))
            StatCard("Min", data.minOrNull()?.toString() ?: "0", Color(0xFFF44336))
            StatCard("Avg", "%.1f".format(data.average()), Color(0xFF2196F3))
        }
    }
}

@Composable
fun StatCard(label: String, value: String, color: Color) {
    Card(
        modifier = Modifier.size(width = 80.dp, height = 60.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = color.copy(alpha = 0.7f)
                )
            )
        }
    }
}




@Composable
fun AnalyticScreen(
    modifier: Modifier = Modifier,
    viewModel: MyviewModel = viewModel()
) {
    // State for history data containing locations
    val historyDetails = viewModel.allHistory.collectAsState(initial = emptyList())
    val context = LocalContext.current

    val salesData = listOf(12f, 19f, 3f, 17f, 28f, 24f, 7f, 34f, 18f, 29f)
    val temperatureData = listOf(22f, 25f, 28f, 24f, 20f, 18f, 16f, 19f, 23f, 27f)
    val stockPrices = listOf(100f, 102f, 98f, 105f, 110f, 108f, 115f, 112f, 118f, 120f)

    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct")

    // Extract categories of emergencies to analyze
    val emergencyCategories = remember(historyDetails.value) {
        historyDetails.value
            .mapNotNull { history ->
                // Extract category from message (assuming format like "Fire emergency", "Medical emergency", etc.)
                val message = history.message.lowercase()
                when {
                    message.contains("fire") -> "Fire"
                    message.contains("medical") -> "Medical"
                    message.contains("accident") -> "Accident"
                    message.contains("police") -> "Police"
                    else -> "Other"
                }
            }
            .groupingBy { it }
            .eachCount()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Emergency Reports Analytics",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Statistics summary
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatCard(
                title = "Total Alerts",
                value = "${historyDetails.value.size}",
                icon = R.drawable.baseline_add_alert_24,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            StatCard(
                title = "Locations Mapped",
                value = "${historyDetails.value.count { it.location.contains(",") }}",
                icon = R.drawable.baseline_add_location_24,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Charts section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    "Location Heatmap",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item {
                        Text("Sales Data", style = MaterialTheme.typography.headlineSmall)
                        EnhancedLineGraph(data = salesData)
                    }

                    item {
                        Text("Temperature", style = MaterialTheme.typography.headlineSmall)
                        EnhancedLineGraph(data = temperatureData)
                    }

                    item {
                        Text("Stock Prices", style = MaterialTheme.typography.headlineSmall)
                        EnhancedLineGraph(data = stockPrices)
                    }

                    item {
                        EnhancedLineGraph(
                            data = salesData,
                            title = "Monthly Sales Performance",
                            color = Color(0xFF6200EE)
                        )
                    }

                    item {
                        EnhancedLineGraph(
                            data = listOf(22f, 25f, 28f, 24f, 20f, 18f, 16f, 19f, 23f, 27f),
                            title = "Temperature Trends",
                            color = Color(0xFFFF9800)
                        )
                    }
                }


                Spacer(modifier = Modifier.height(8.dp))

            }
        }

//        Spacer(modifier = Modifier.height(16.dp))

        // Emergency category breakdown
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(240.dp),
//            colors = CardDefaults.cardColors(
//                containerColor = Color.White
//            )
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp)
//            ) {
//                Text(
//                    "Emergency Categories",
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 16.sp
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//            }
//        }

//        Spacer(modifier = Modifier.height(16.dp))

        // Time distribution chart (line chart)
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(240.dp),
//            colors = CardDefaults.cardColors(
//                containerColor = Color.White
//            )
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp)
//            ) {
//                Text(
//                    "Time Distribution",
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 16.sp
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//            }
//        }
    }
}


@Composable
fun StatCard(
    title: String,
    value: String,
    icon: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                FlexibleIcon(
                    iconType = IconType.DRAWABLE,
                    iconResId = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )

            Text(
                text = title,
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }
    }
}



@Composable
fun VideoCard(
    title: String,
    description: String,
    timestamp: Date,
    videoUrl: String,
    thumbnailUrl: String? = null
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("HH:mm:ss - MMM dd, yyyy", Locale.getDefault())
    val timeString = dateFormat.format(timestamp)

    val backgroundColor = Color(0xFFE0F2FE)
    val borderColor = Color(0xFF0EA5E9)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FlexibleIcon(
                        iconType = IconType.DRAWABLE,
                        iconResId = R.drawable.baseline_photo_camera_24,
                        contentDescription = "Video Recording",
                        tint = borderColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        fontWeight = FontWeight.Medium,
                        color = borderColor
                    )
                }
                Text(
                    text = timeString,
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Video thumbnail or placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(MaterialTheme.shapes.medium)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    FlexibleIcon(
                        iconType = IconType.DRAWABLE,
                        iconResId = R.drawable.baseline_photo_camera_24,
                        contentDescription = "Video",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                // Play button overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    FilledIconButton(
                        onClick = {},
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.7f)
                        )
                    ) {
                        FlexibleIcon(
                            iconType = IconType.DRAWABLE,
                            iconResId = R.drawable.baseline_smart_display_24,
                            contentDescription = "Play",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = description,
                fontSize = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = {
                        // Open video details or additional information
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    FlexibleIcon(
                        iconType = IconType.DRAWABLE,
                        iconResId = R.drawable.baseline_trending_up_24,
                        contentDescription = "Details",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Details", fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                FilledTonalButton(
                    onClick = {
                        Log.d("VIDEO_URL", "Attempting to play: $videoUrl")

                        if (videoUrl.isNotEmpty()) {
                            try {
                                // Handle different URI types
                                if (videoUrl.startsWith("content://")) {
                                    // If it's already a content URI, use it directly
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(Uri.parse(videoUrl), "video/*")
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }

                                    if (intent.resolveActivity(context.packageManager) != null) {
                                        context.startActivity(intent)
                                    } else {
                                        Toast.makeText(context, "No video player app found", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    // If it's a file path, use FileProvider
                                    val videoFile = File(videoUrl)
                                    if (videoFile.exists()) {
                                        // Get content URI using FileProvider
                                        val videoUri = FileProvider.getUriForFile(
                                            context,
                                            "${context.applicationContext.packageName}.provider",
                                            videoFile
                                        )

                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            setDataAndType(videoUri, "video/*")
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }

                                        if (intent.resolveActivity(context.packageManager) != null) {
                                            context.startActivity(intent)
                                        } else {
                                            Toast.makeText(context, "No video player app found", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "Video file not found at: $videoUrl", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("VideoCard", "Error playing video", e)
                                Toast.makeText(context, "Error playing video: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "No video URL available", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    FlexibleIcon(
                        iconType = IconType.DRAWABLE,
                        iconResId = R.drawable.baseline_smart_display_24,
                        contentDescription = "Play Video",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Play Video", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun HistoryCard(history: HistoryEntity, navHostController: NavHostController?) {
    val dateFormat = SimpleDateFormat("HH:mm:ss - MMM dd, yyyy", Locale.getDefault())
    val timeString = dateFormat.format(history.time)
    val context = LocalContext.current
    val backgroundColor = Color(0xFFE0F2FE)
    val borderColor = Color(0xFF0EA5E9)

    // Media player state
    var isPlaying by remember { mutableStateOf(false) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    // Clean up the MediaPlayer when the composable is removed from composition
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
            mediaPlayer = null
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Emergency Alert",
                        tint = borderColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Emergency Alert",
                        fontWeight = FontWeight.Medium,
                        color = borderColor
                    )
                }
                Text(
                    text = timeString,
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = history.message,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = history.location,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }

            // Audio section - only show if audioPath exists
            if (!history.audioPath.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.baseline_photo_camera_24),
                        contentDescription = "Audio recording",
                        tint = Color.DarkGray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Audio Recording",
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.weight(1f)
                    )

                    FilledTonalIconButton(
                        onClick = {
                            if (isPlaying) {
                                // Stop playback
                                mediaPlayer?.apply {
                                    stop()
                                    release()
                                }
                                mediaPlayer = null
                                isPlaying = false
                            } else {
                                // Start playback
                                try {
                                    val audioFile = File(history.audioPath)
                                    if (audioFile.exists()) {
                                        mediaPlayer = MediaPlayer().apply {
                                            setDataSource(history.audioPath)
                                            setOnCompletionListener {
                                                isPlaying = false
                                                it.release()
                                                mediaPlayer = null
                                            }
                                            prepare()
                                            start()
                                        }
                                        isPlaying = true
                                    } else {
                                        Toast.makeText(context, "Audio file not found", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error playing audio: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = if (isPlaying) ImageVector.vectorResource(R.drawable.baseline_stop_circle_24) else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Stop Audio" else "Play Audio"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                FilledTonalButton(
                    onClick = {
                        if (history.location == "Location unavailable") {
                            Toast.makeText(context, "Location unavailable", Toast.LENGTH_SHORT).show()
                        } else {
                            val url = history.location.substringAfter("Location: ").trim()
                            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))
                            context.startActivity(intent)
                        }
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "View location",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View Google Maps", fontSize = 14.sp)
                }
            }
        }
    }
}






























//
//@Composable
//fun VideoScreen(
//    modifier: Modifier = Modifier,
//    viewModel: MyviewModel = viewModel()
//) {
//    // Get all the videos
//    val videos = viewModel.allVideoCaptures.collectAsState(initial = emptyList())
//
//    // State for video player dialog
//    var showVideoPlayer by remember { mutableStateOf(false) }
//    var selectedVideoPath by remember { mutableStateOf<String?>(null) }
//
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(start = 16.dp, end = 16.dp)
//    ) {
//        if (videos.value.isEmpty()) {
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    FlexibleIcon(
//                        iconType = IconType.DRAWABLE,
//                        iconResId = R.drawable.baseline_photo_camera_24,
//                        contentDescription = null,
//                        modifier = Modifier.size(64.dp),
//                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
//                    )
//                    Spacer(modifier = Modifier.height(16.dp))
//                    Text(
//                        "No video recordings available",
//                        fontSize = 18.sp,
//                        color = Color.Gray
//                    )
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Text(
//                        "Video feeds will appear here when available",
//                        fontSize = 14.sp,
//                        color = Color.Gray
//                    )
//                }
//            }
//        } else {
//            LazyColumn(
//                verticalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                items(videos.value) { video ->
//                    VideoCard(
//                        title = "Camera Recording ${video.id}",
//                        description = video.description ?: "Surveillance footage",
//                        timestamp = video.dateTime ?: Date(),
//                        videoUrl = video.location ?: "",
//                        thumbnailUrl = null,
//                        onPlayVideo = { videoPath ->
//                            selectedVideoPath = videoPath
//                            showVideoPlayer = true
//                        }
//                    )
//                }
//            }
//        }
//    }
//
//    // Video Player Dialog
//    if (showVideoPlayer && selectedVideoPath != null) {
//        VideoPlayerDialog(
//            videoPath = selectedVideoPath!!,
//            onDismiss = {
//                showVideoPlayer = false
//                selectedVideoPath = null
//            }
//        )
//    }
//}
//
//@Composable
//fun VideoCard(
//    title: String,
//    description: String,
//    timestamp: Date,
//    videoUrl: String,
//    thumbnailUrl: String?,
//    onPlayVideo: (String) -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable() { onPlayVideo(videoUrl) },
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        shape = RoundedCornerShape(12.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surface
//        )
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        ) {
//            // Header with play icon and title
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Box(
//                    modifier = Modifier
//                        .size(50.dp)
//                        .clip(CircleShape)
//                        .background(MaterialTheme.colorScheme.primary),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.PlayArrow,
//                        contentDescription = "Play Video",
//                        tint = Color.White,
//                        modifier = Modifier.size(24.dp)
//                    )
//                }
//
//                Spacer(modifier = Modifier.width(16.dp))
//
//                Column(
//                    modifier = Modifier.weight(1f)
//                ) {
//                    Text(
//                        text = title,
//                        fontSize = 18.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = MaterialTheme.colorScheme.onSurface
//                    )
//
//                    Text(
//                        text = SimpleDateFormat("MMM dd, yyyy, hh:mm a", Locale.getDefault())
//                            .format(timestamp),
//                        fontSize = 14.sp,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
//            }
//
//            // Description
//            Spacer(modifier = Modifier.height(12.dp))
//            Text(
//                text = description,
//                fontSize = 14.sp,
//                color = MaterialTheme.colorScheme.onSurfaceVariant,
//                modifier = Modifier.padding(horizontal = 8.dp)
//            )
//
//            // Video info
//            Spacer(modifier = Modifier.height(12.dp))
//            HorizontalDivider()
//            Spacer(modifier = Modifier.height(12.dp))
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(
//                        imageVector = ImageVector.vectorResource(R.drawable.baseline_video_library_24),
//                        contentDescription = "Video File",
//                        tint = MaterialTheme.colorScheme.primary,
//                        modifier = Modifier.size(16.dp)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        text = "Video Recording",
//                        fontSize = 12.sp,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
//
//                Button(
//                    onClick = { onPlayVideo(videoUrl) },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = MaterialTheme.colorScheme.primary
//                    ),
//                    shape = RoundedCornerShape(20.dp),
//                    modifier = Modifier.height(32.dp),
//                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.PlayArrow,
//                        contentDescription = "Play",
//                        modifier = Modifier.size(16.dp)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        text = "Play",
//                        fontSize = 12.sp
//                    )
//                }
//            }
//        }
//    }
//}






























@Composable
fun VideoScreen(
    modifier: Modifier = Modifier,
    viewModel: MyviewModel = viewModel()
) {
    // Get all the videos
    val videos = viewModel.allVideoCaptures.collectAsState(initial = emptyList())

    // State for video player dialog (same as ActivitiesScreen)
    var showVideoPlayer by remember { mutableStateOf(false) }
    var selectedVideoPath by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp)
    ) {
        if (videos.value.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    FlexibleIcon(
                        iconType = IconType.DRAWABLE,
                        iconResId = R.drawable.baseline_photo_camera_24,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No video recordings available",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Video feeds will appear here when available",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(videos.value) { video ->
                    // Use the same VideoCard structure as IncidentReportItem but simplified for VideoScreen
                    VideoRecordingCard(
                        videoCapture = video,
                        onPlayVideo = { videoPath ->
                            selectedVideoPath = videoPath
                            showVideoPlayer = true
                        }
                    )
                }
            }
        }
    }

    // Video Player Dialog - EXACT same implementation as ActivitiesScreen
    if (showVideoPlayer && selectedVideoPath != null) {
        VideoPlayerDialog(
            videoPath = selectedVideoPath!!,
            onDismiss = {
                showVideoPlayer = false
                selectedVideoPath = null
            }
        )
    }
}

@Composable
fun VideoRecordingCard(
    videoCapture: VideoCapture,
    onPlayVideo: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with video icon and title (similar to IncidentReportItem)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.baseline_video_camera_back_24),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Camera Recording #${videoCapture.id}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = SimpleDateFormat("MMM dd, yyyy, hh:mm a", Locale.getDefault())
                            .format(videoCapture.dateTime ?: Date()),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "Surveillance footage",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            // Description if available (same as IncidentReportItem)
            if (!videoCapture.description.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = videoCapture.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Play button (same logic as IncidentReportItem)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        // Same logic as IncidentReportItem - check if location exists
                        videoCapture.location?.let { path ->
                            onPlayVideo(path)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play Video",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Play Video", fontSize = 16.sp)
                }
            }
        }
    }
}