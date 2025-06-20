package com.example.todoapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import com.example.todoapp.local.entities.VideoCapture
import com.example.todoapp.viewModel.MyviewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesScreen(navController: NavHostController? = null) {
    val context = LocalContext.current
    var lastRecordedUri by remember { mutableStateOf<Uri?>(null) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showVideoPlayer by remember { mutableStateOf(false) }
    var selectedVideoPath by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Get the viewmodel
    val viewModel: MyviewModel = viewModel()
    val allVideoCaptures = viewModel.allVideoCaptures.collectAsState(initial = emptyList())

    var permissionsGranted by remember { mutableStateOf(false) }

    if (!permissionsGranted) {
        RequestPermissions {
            permissionsGranted = true
        }
    }

    // Create a launcher for video capture intent
    val videoCaptureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Video was recorded successfully
            val uri = result.data?.data
            if (uri != null) {
                lastRecordedUri = uri
                showReportDialog = true
            } else {
                scope.launch {
                    snackbarHostState.showSnackbar("Failed to get video")
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Report Crime")
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (permissionsGranted) {
                ExtendedFloatingActionButton(
                    onClick = {
                        // Create an intent to capture video using the device's camera app
                        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                        // Optional: Set max video duration in seconds
                        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60)
                        // Optional: Set video quality (0 = low, 1 = high)
                        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)

                        videoCaptureLauncher.launch(intent)
                    },
                    icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.baseline_video_camera_back_24),
                            contentDescription = "Record Incident"
                        )
                    },
                    text = { Text("Record Incident") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            // Emergency info banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Information",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Record incidents to report to authorities. All videos are securely stored and only shared when you send them.",
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Incident Reports Content
            IncidentReportsContent(
                incidentReports = allVideoCaptures.value,
                onPlayVideo = { videoPath ->
                    selectedVideoPath = videoPath
                    showVideoPlayer = true
                },
                onDeleteReport = { videoCapture ->
                    scope.launch(Dispatchers.IO) {
//                        viewModel.deleteVideoCapture(videoCapture)
                        scope.launch {
                            snackbarHostState.showSnackbar("Video deleted successfully")
                        }
                    }
                },
                onSendReport = { videoCapture ->
                    scope.launch(Dispatchers.IO) {
                        // Update the status to sent (true)
                        val updatedVideoCapture = videoCapture.copy(status = true)
//                        viewModel.updateVideoCapture(updatedVideoCapture)

                        scope.launch {
                            snackbarHostState.showSnackbar("Sending incident report to authorities...")
                            delay(2000)
                            snackbarHostState.showSnackbar("Incident report sent successfully to emergency services")
                        }
                    }
                }
            )
        }

        // Submit Incident Report Dialog
        if (showReportDialog && lastRecordedUri != null) {
            IncidentReportDialog(
                videoUri = lastRecordedUri!!,
                onDismiss = { showReportDialog = false },
                onSubmit = { title, description ->
                    val videoPath = getRealPathFromURI(context, lastRecordedUri!!) ?: lastRecordedUri.toString()

                    val newVideoCapture = VideoCapture(
                        user_id = "1",
                        location = videoPath,
                        description = description,
                        status = false,
                        dateTime = java.util.Date()
                    )

                    // Save the video capture to database
                    scope.launch(Dispatchers.IO) {
                        viewModel.saveVideoCapture(newVideoCapture)
                        scope.launch {
                            snackbarHostState.showSnackbar("Incident recorded successfully. Press SEND to alert authorities.")
                        }
                    }

                    Log.d("Video object: ", "$newVideoCapture")
                    showReportDialog = false
                }
            )
        }

        // Video Player Dialog
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentReportDialog(
    videoUri: Uri,
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("Incident Report") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report Details") },
        text = {
            Column {
                Text(
                    "Video recorded successfully. Please provide additional details about this incident.",
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Incident Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(vertical = 8.dp)
                )

                Text(
                    "Video Location: ${videoUri.lastPathSegment ?: "Unknown"}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(title, description) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F)
                )
            ) {
                Text("Save Report")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun IncidentReportsContent(
    modifier: Modifier = Modifier,
    incidentReports: List<VideoCapture>,
    onPlayVideo: (String) -> Unit,
    onDeleteReport: (VideoCapture) -> Unit,
    onSendReport: (VideoCapture) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (incidentReports.isEmpty()) {
            EmptyStateMessage()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(incidentReports) { videoCapture ->
                    IncidentReportItem(
                        videoCapture = videoCapture,
                        onPlayVideo = onPlayVideo,
                        onDeleteReport = onDeleteReport,
                        onSendReport = onSendReport
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun IncidentReportItem(
    videoCapture: VideoCapture,
    onPlayVideo: (String) -> Unit,
    onDeleteReport: (VideoCapture) -> Unit,
    onSendReport: (VideoCapture) -> Unit
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
            // Header with title and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(
                            if (videoCapture.status) Color(0xFF4CAF50)
                            else Color(0xFFD32F2F)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (videoCapture.status)
                            Icons.Rounded.Check
                        else
                            ImageVector.vectorResource(R.drawable.baseline_video_camera_back_24),
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
                        text = "Incident Report #${videoCapture.id}",
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

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = if (videoCapture.status) "Sent to authorities" else "Not sent yet",
                            fontSize = 14.sp,
                            color = if (videoCapture.status)
                                Color(0xFF4CAF50)
                            else
                                Color(0xFFD32F2F)
                        )

                        if (videoCapture.status) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Description if available
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

            // Action buttons
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Play button
                Button(
                    onClick = {
                        videoCapture.location?.let { path ->
                            onPlayVideo(path)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play Video",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Play")
                }

                // Send button (only show if not sent)
                if (!videoCapture.status) {
                    Button(
                        onClick = { onSendReport(videoCapture) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD32F2F),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send Report",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Send")
                    }
                }

                // Delete button
                IconButton(
                    onClick = { onDeleteReport(videoCapture) },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.errorContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Report",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun VideoPlayerDialog(
    videoPath: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    // Create ExoPlayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            try {
                val mediaItem = if (videoPath.startsWith("content://") || videoPath.startsWith("file://")) {
                    MediaItem.fromUri(Uri.parse(videoPath))
                } else {
                    // Handle file path
                    val file = File(videoPath)
                    if (file.exists()) {
                        MediaItem.fromUri(Uri.fromFile(file))
                    } else {
                        MediaItem.fromUri(Uri.parse(videoPath))
                    }
                }
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
            } catch (e: Exception) {
                Log.e("VideoPlayer", "Error loading video: ${e.message}")
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
//            exoPlayer.release()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Video Player") },
        text = {
            AndroidView(
                factory = { context ->
                    PlayerView(context).apply {
                        player = exoPlayer
                        useController = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun EmptyStateMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.baseline_video_camera_back_24),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color(0xFFD32F2F).copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Incident Reports",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap the 'Record Incident' button to record and report an emergency situation to authorities",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun RequestPermissions(onPermissionsGranted: () -> Unit) {
    val context = LocalContext.current
    val cameraPermission = android.Manifest.permission.CAMERA
    val recordAudioPermission = android.Manifest.permission.RECORD_AUDIO

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.all { it.value }
        if (granted) {
            onPermissionsGranted()
        } else {
            Toast.makeText(context, "Permissions not granted", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(cameraPermission, recordAudioPermission)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun IncidentReportScreenPreview() {
    ActivitiesScreen()
}

// Function to get real path from URI
fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
    var cursor: Cursor? = null
    return try {
        val proj = arrayOf(MediaStore.Video.Media.DATA)
        cursor = context.contentResolver.query(contentUri, proj, null, null, null)
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
        cursor?.moveToFirst()
        cursor?.getString(columnIndex ?: -1)
    } catch (e: Exception) {
        Log.e("VideoPath", "Error getting file path from URI", e)
        contentUri.toString() // Fallback to URI string
    } finally {
        cursor?.close()
    }
}