package com.example.todoapp

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.local.entities.HistoryEntity
import com.example.todoapp.ui.theme.TodoAppTheme
import com.example.todoapp.viewModel.MyviewModel
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import okhttp3.Credentials
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class EmergencyCountdownActivity : ComponentActivity() {
    private val TAG = "EmergencyCountdown"
    lateinit var locationPermissionRequest: ActivityResultLauncher<Array<String>>
    private var audioFilePath: String? = null

    private val smsPermissionRequest: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show()
            }
        }


    // added these lines of codes to enable access to recording features
    private val audioPermissionRequest: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(this, "Audio recording permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    // Receiver for audio recording completion
    private val recordingCompletedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            audioFilePath = intent?.getStringExtra(RecordAudio.AUDIO_FILE_PATH_EXTRA)
            Log.d(TAG, "Recording completed. File path: $audioFilePath")
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register the recording completion receiver

        registerReceiver(
            recordingCompletedReceiver,
            IntentFilter("com.example.todoapp.RECORDING_COMPLETED"),
            RECEIVER_NOT_EXPORTED
        )

        // Request SMS permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            smsPermissionRequest.launch(Manifest.permission.SEND_SMS)
        }

        // Request audio recording permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            audioPermissionRequest.launch(Manifest.permission.RECORD_AUDIO)
        }

        locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.all { it.value }) {
                Log.d(TAG, "Location permissions granted")
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        val countdownSeconds = intent.getIntExtra("countdown_seconds", 5)
        Log.d(TAG, "Starting countdown for $countdownSeconds seconds")

        setContent {
            TodoAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EmergencyCountdownScreen(
                        countdownSeconds = countdownSeconds,
                        onCancel = { cancelEmergency() },
                        getAudioFilePath = { audioFilePath }
                    )
                }
            }
        }
    }

    private fun cancelEmergency() {
        Log.d(TAG, "Emergency countdown cancelled by user")
        finish()
    }

    override fun onBackPressed() {
        cancelEmergency()
        super.onBackPressed()
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(recordingCompletedReceiver)
        } catch (e: Exception) {
            Log.e(TAG, "Error unregistering receiver: ${e.message}")
        }
        super.onDestroy()
    }
}



// Modified EmergencyCountdownScreen - Replace the countdown completion section
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EmergencyCountdownScreen(
    countdownSeconds: Int,
    onCancel: () -> Unit,
    getAudioFilePath: () -> String?,
    viewModel: MyviewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // State variables
    var secondsRemaining by remember { mutableIntStateOf(countdownSeconds) }
    var isTimerInitialized by remember { mutableStateOf(false) }
    var currentLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }
    var smsSendingStatus by remember { mutableStateOf<String?>(null) }
    var isProcessingComplete by remember { mutableStateOf(false) }

    val progress = remember(secondsRemaining, isTimerInitialized) {
        if (!isTimerInitialized) 0f else (1 - secondsRemaining.toFloat() / (secondsRemaining + (countdownSeconds - secondsRemaining)))
    }

    val messageInformation by viewModel.allSOSMessages.collectAsState(initial = emptyList())
    val contactsInformations by viewModel.allContacts.collectAsState(initial = emptyList())

    val audioRecordingIntent = Intent(context, RecordAudio::class.java)

    // Initialize timer from database
    LaunchedEffect(Unit) {
        // Start the audio recording
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            context.startService(audioRecordingIntent)
        } else {
            Log.e("Audio recording starting service", "No permission to record audio")
        }

        val (id) = getUserData(context)
        if (id != null) {
            try {
                val userTimers = viewModel.getTimersByIdForUser(id.id)
                if (userTimers.isNotEmpty()) {
                    secondsRemaining = userTimers[0].seconds
                    isTimerInitialized = true
                }
            } catch (e: Exception) {
                Log.e("EmergencyCountdown", "Failed to get timer: ${e.message}", e)
            }
        }

        if (!context.hasLocationPermission()) {
            (context as EmergencyCountdownActivity).locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

        if (context.hasLocationPermission()) {
            try {
                currentLocation = getCurrentLocation(context)
            } catch (e: Exception) {
                locationError = "Failed to get location: ${e.message}"
            }
        }
    }

    // Countdown and SMS sending logic
    LaunchedEffect(isTimerInitialized) {
        if (isTimerInitialized) {
            while (secondsRemaining > 0) {
                delay(1000)
                secondsRemaining--
            }


            // When countdown completes - Updated section
            if (messageInformation.isNotEmpty() && contactsInformations.isNotEmpty()) {
                scope.launch(Dispatchers.IO) {
                    try {
                        withContext(Dispatchers.Main) {
                            smsSendingStatus = "Processing emergency data..."
                        }

                        // Stop the audio recording
                        context.stopService(audioRecordingIntent)
                        delay(1000) // Wait for recording to complete

                        // Get location
                        var locationText = ""
                        val locationDetails = getCurrentLocation(context)
                        if (locationDetails != null) {
                            val (latitude, longitude) = locationDetails
                            locationText = "Location: https://maps.google.com/?q=${latitude},${longitude}"
                        }

                        // Get audio file path
                        val audioFile = getAudioFilePath()
                        Log.d("Audio Path", "Local audio file: $audioFile")

                        // Upload audio to server
                        withContext(Dispatchers.Main) {
                            smsSendingStatus = "Uploading audio recording..."
                        }

                        val audioUrl = uploadAudioToServer(audioFile)

                        Log.d("AudioUpload", "Upload result: $audioUrl")

                        // Prepare message
                        val baseMessage = messageInformation[0].messageText
                        val audioText = if (audioUrl != null) {
                            "\nEmergency Audio Recording: $audioUrl"
                        } else {
                            "(Audio recording upload failed - please check network connection)"
                        }

                        val timeTaken = getCurrentDateTimeFormatted()

                        val fullMessage = "$timeTaken\nSOS!\n$baseMessage\n$locationText$audioText"

                        Log.d("Full Message", fullMessage)

                        // Send SMS to all contacts
                        withContext(Dispatchers.Main) {
                            smsSendingStatus = "Sending emergency messages to ${contactsInformations.size} contacts..."
                        }


//                        sendEmergencySMSWithOkHttp(
//                            recipientNumber = contactsInformations[0].phoneNumber,
//                            fullMessage
//                        )

                        // OPTION 3: Use the emergency-specific function
                        sendEmergencySOSMessage(
                            recipients = contactsInformations.map { it.phoneNumber },
                            sosMessage = fullMessage,
                            location = locationText
                        )


                        // Toasts should run on the main thread
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "All emergency messages sent successfully!", Toast.LENGTH_LONG).show()
                        }


                        // Create history entity
                        val history = HistoryEntity(
                            message = baseMessage,
                            location = locationText,
                            time = Date(),
                            audioPath = audioUrl ?: audioFile // Use server URL if available, otherwise local path
                        )

                        viewModel.createHistory(history)

                        // Mark processing as complete
                        withContext(Dispatchers.Main) {
                            isProcessingComplete = true
                        }

                        // Wait 3 seconds then close the app
                        delay(3000)

                        withContext(Dispatchers.Main) {
                            // Close the app completely
                            (context as Activity).finishAffinity()
                            Process.killProcess(Process.myPid())
                        }

                    } catch (e: Exception) {
                        Log.e("EmergencyCountdown", "Failed in emergency process: ${e.message}", e)
                        withContext(Dispatchers.Main) {
                            smsSendingStatus = "Emergency process failed: ${e.message}"
                            Toast.makeText(
                                context,
                                "Emergency process failed: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()

                            // Still close the app after error
                            delay(3000)
                            (context as Activity).finishAffinity()
                        }
                    }
                }
            }


        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SOS ALERT EMERGENCY",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = when {
                        secondsRemaining > 0 -> "Sending emergency messages in:"
                        !isProcessingComplete -> "Processing emergency data..."
                        else -> "Emergency complete. App closing..."
                    },
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (secondsRemaining > 0) {
                    Text(
                        text = "$secondsRemaining",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = if (isProcessingComplete) 1f else progress,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (secondsRemaining > 0) {
                    Button(
                        onClick = onCancel,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Text(
                            text = "CANCEL EMERGENCY",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    smsSendingStatus?.let { status ->
                        Text(
                            text = status,
                            fontSize = 14.sp,
                            color = if (status.startsWith("Failed"))
                                MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                    }

                    if (isProcessingComplete) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "App will close automatically...",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                locationError?.let { error ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}


suspend fun getCurrentLocation(context: Context): Pair<Double, Double>? {
    if (!context.hasLocationPermission()) {
        throw SecurityException("Location permission not granted")
    }

    return try {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val cancellationTokenSource = CancellationTokenSource()

        @Suppress("MissingPermission")
        val location = fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).await()

        location?.let { Pair(it.latitude, it.longitude) }
    } catch (e: Exception) {
        throw Exception("Location request failed: ${e.message}")
    }
}

fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}



// This code is the best for sending the messages
/*
* It needs recipient number, messages that will be sent and the account details which is already put in it
* so basically what you will need is to use the recipient number and message to be sent you will call the function with it
* */
suspend fun sendEmergencySMSWithOkHttp(
    recipientNumber: String,
    message: String,
    twilioAccountSid: String = "account_id",
    twilioAuthToken: String = "account_token",
    twilioNumber: String = "+phone_number"
) {
    withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()

            // Format Twilio API URL
            val url = "https://api.twilio.com/2010-04-01/Accounts/$twilioAccountSid/Messages.json"

            // Build request body with SMS parameters
            val requestBody = FormBody.Builder()
                .add("To", recipientNumber)
                .add("From", twilioNumber)
                .add("Body", message)
                .build()

            // Create request with auth header
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Authorization", Credentials.basic(twilioAccountSid, twilioAuthToken))
                .build()

            // Execute request
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                Log.d(
                    "TwilioSMS",
                    "Message sent successfully! Response: ${response.body?.string()}"
                )
            } else {
                val errorBody = response.body?.string() ?: "Unknown error"
                Log.e("TwilioSMS", "Error sending SMS: $errorBody")
                throw Exception("Failed to send SMS: ${response.code} - $errorBody")
            }
        } catch (e: Exception) {
            Log.e("TwilioSMS", "Exception sending SMS: ${e.message}", e)
            throw e
        }
    }
}


/**
 * Sends SMS using Infobip API
 * @param recipients Either a single phone number (String) or list of phone numbers (List<String>)
 * @param message The message text to send
 * @param fromNumber The sender number (default: "447491163443")
 * @param apiKey Your Infobip API key
 * @param baseUrl Your Infobip base URL
 */
suspend fun sendSOSMessage(
    recipients: Any, // Can be String or List<String>
    message: String,
    fromNumber: String = "phone_number",
    apiKey: String = "App api-key",
    baseUrl: String = "https://ypgmk1.api.infobip.com"
) {
    withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            val mediaType = "application/json".toMediaType()

            // Convert recipients to list format
            val recipientList = when (recipients) {
                is String -> listOf(recipients)
                is List<*> -> recipients.filterIsInstance<String>()
                else -> throw IllegalArgumentException("Recipients must be String or List<String>")
            }

            if (recipientList.isEmpty()) {
                throw IllegalArgumentException("No valid recipients provided")
            }

            // Build JSON payload
            val jsonPayload = buildJsonPayload(recipientList, fromNumber, message)

            val requestBody = jsonPayload.toRequestBody(mediaType)

            val request = Request.Builder()
                .url("$baseUrl/sms/2/text/advanced")
                .method("POST", requestBody)
                .addHeader("Authorization", apiKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                Log.d("InfobipSMS", "SMS sent successfully! Response: $responseBody")

                // Parse and log details
                responseBody?.let { parseAndLogResponse(it) }

            } else {
                val errorBody = response.body?.string() ?: "Unknown error"
                Log.e("InfobipSMS", "Error sending SMS: ${response.code} - $errorBody")
                throw Exception("Failed to send SMS: ${response.code} - $errorBody")
            }

        } catch (e: Exception) {
            Log.e("InfobipSMS", "Exception sending SMS: ${e.message}", e)
            throw e
        }
    }
}

/**
 * Builds the JSON payload for Infobip API
 */
private fun buildJsonPayload(recipients: List<String>, fromNumber: String, message: String): String {
    val messagesArray = JSONArray()

    // Create destinations array
    val destinationsArray = JSONArray()
    recipients.forEach { recipient ->
        val destination = JSONObject().put("to", recipient)
        destinationsArray.put(destination)
    }

    // Create message object
    val messageObj = JSONObject()
        .put("destinations", destinationsArray)
        .put("from", fromNumber)
        .put("text", message)

    messagesArray.put(messageObj)

    // Create final payload
    val payload = JSONObject().put("messages", messagesArray)

    return payload.toString()
}

/**
 * Parses and logs the response from Infobip API
 */
private fun parseAndLogResponse(responseBody: String) {
    try {
        val jsonResponse = JSONObject(responseBody)
        val messages = jsonResponse.optJSONArray("messages")

        messages?.let { msgArray ->
            for (i in 0 until msgArray.length()) {
                val message = msgArray.getJSONObject(i)
                val messageId = message.optString("messageId", "N/A")
                val status = message.optJSONObject("status")
                val statusName = status?.optString("name", "Unknown")
                val statusDescription = status?.optString("description", "No description")

                Log.d("InfobipSMS", "Message ID: $messageId, Status: $statusName - $statusDescription")
            }
        }
    } catch (e: Exception) {
        Log.w("InfobipSMS", "Could not parse response details: ${e.message}")
    }
}

/**
 * Alternative function specifically for emergency/SOS messages with predefined settings
 */
suspend fun sendEmergencySOSMessage(
    recipients: Any, // Can be String or List<String>
    sosMessage: String,
    location: String? = null
) {
    val fullMessage = if (location != null) {
        "$sosMessage\n\n$location"
    } else {
        sosMessage
    }

    try {
        sendSOSMessage(
            recipients = recipients,
            message = "EMERGENCY ALERT\n$fullMessage",
            fromNumber = "447491163443" // Your sender number
        )
        Log.d("EmergencySOS", "Emergency SOS message sent successfully")
    } catch (e: Exception) {
        Log.e("EmergencySOS", "Failed to send emergency SOS: ${e.message}", e)
        throw e
    }
}


// Updated uploadAudioToServer function with action parameter
suspend fun uploadAudioToServer(
    audioFilePath: String?,
    serverUrl: String = "https://sosalert.megwavetug.com/api.php" // Replace with your actual server URL
): String? {
    return withContext(Dispatchers.IO) {
        try {
            if (audioFilePath == null) {
                Log.e("AudioUpload", "Audio file path is null")
                return@withContext null
            }

            val audioFile = File(audioFilePath)
            if (!audioFile.exists()) {
                Log.e("AudioUpload", "Audio file does not exist: $audioFilePath")
                return@withContext null
            }

            Log.d("AudioUpload", "Starting upload for file: ${audioFile.name}, Size: ${audioFile.length()} bytes")

            val client = OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .build()

            // Create multipart request body with action parameter
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("action", "upload_audio") // This is the key addition
                .addFormDataPart(
                    "audio_file",
                    audioFile.name,
                    audioFile.asRequestBody("audio/*".toMediaType())
                )
                .build()

            val request = Request.Builder()
                .url(serverUrl)
                .post(requestBody)
                .addHeader("Accept", "application/json")
                .build()

            Log.d("AudioUpload", "Sending request to: $serverUrl")

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                Log.d("AudioUpload", "Upload response: $responseBody")

                responseBody?.let { responseString ->
                    try {
                        val jsonResponse = JSONObject(responseString)
                        val status = jsonResponse.getString("status")

                        if (status == "success") {
                            val data = jsonResponse.getJSONObject("data")
                            val fileUrl = data.getString("file_url")
                            val fileName = data.getString("file_name")
                            val fileSize = data.getString("file_size_formatted")
                            val uploadTime = data.getString("upload_time")

                            Log.d("AudioUpload", "Audio uploaded successfully!")
                            Log.d("AudioUpload", "File URL: $fileUrl")
                            Log.d("AudioUpload", "File Name: $fileName")
                            Log.d("AudioUpload", "File Size: $fileSize")
                            Log.d("AudioUpload", "Upload Time: $uploadTime")

                            return@withContext fileUrl
                        } else {
                            val error = jsonResponse.getString("message")
                            val errorCode = jsonResponse.optString("error_code", "UNKNOWN")
                            Log.e("AudioUpload", "Server error [$errorCode]: $error")
                            return@withContext null
                        }
                    } catch (e: Exception) {
                        Log.e("AudioUpload", "Error parsing JSON response: ${e.message}")
                        Log.e("AudioUpload", "Raw response: $responseString")
                        return@withContext null
                    }
                }
            } else {
                val errorBody = response.body?.string() ?: "Unknown error"
                Log.e("AudioUpload", "HTTP Error: ${response.code} - ${response.message}")
                Log.e("AudioUpload", "Error body: $errorBody")
                return@withContext null
            }

            return@withContext null

        } catch (e: Exception) {
            Log.e("AudioUpload", "Exception during upload: ${e.message}", e)
            return@withContext null
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentDateTimeFormatted(): String {
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("MMM dd HH:mm", Locale.ENGLISH)
    return current.format(formatter)
}