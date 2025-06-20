package com.example.todoapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

/**
 * Info screen that shows documentation or helpful information about the app.
 * Includes a TopAppBar with a back arrow icon.
 *
 * @param navHost Optional NavController used for navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Info(navHost: NavHostController? = null) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Info") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            // Navigate back if NavController is available
                            navHost?.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        InfoContent(modifier = Modifier.padding(innerPadding))
    }
}

/**
 * Displays a list of documentation points about the app.
 * You can edit the list below to update the content.
 *
 * @param modifier Modifier to apply padding and layout customizations.
 */
@Composable
fun InfoContent(modifier: Modifier) {
    val documentationList = listOf(
        "📲 Registration & Login: Start by registering an account and signing in. This gives you access to all features of the app.",

        "👤 Profile: View your user details and manage your account settings.",

        "✉️ Edit SOS Message: Customize the emergency message that will be sent when you trigger an SOS.",

        "📹 Record Video: Capture a short video that will be sent along with your alert during emergencies.",

        "⏲️ Edit Timer: Set the duration (in seconds) for how long the app should wait before sending out your SOS alert after it is triggered.",

        "📜 SOS History: View a history log of all past SOS alerts you've triggered for record keeping and review.",

        "📞 Register Numbers: Add emergency contact phone numbers (e.g., family, friends) who will receive your SOS messages during emergencies.",

        "🆘 Triggering SOS: In a real emergency:\n" +
                "   • Press the Power Button 3 times to send a message to your registered contacts and the police.\n" +
                "   • Press the Power Button 4 times to send a message to your registered contacts, the police, and medical services.",

        "💡 Important Note: Ensure permissions (camera, SMS, contacts, background service) are granted for full functionality of the app.",

        "🔐 Privacy & Security: All your data is kept private. Messages are only sent when you trigger the SOS system.",

        "🛠️ Support: For help or feedback, please contact our support through the app settings or official website."
    )


    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(documentationList) { item ->
            Text(
                text = item,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}


/**
 * Preview of the Info screen.
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ShowInfoPreview() {
    Info()
}
