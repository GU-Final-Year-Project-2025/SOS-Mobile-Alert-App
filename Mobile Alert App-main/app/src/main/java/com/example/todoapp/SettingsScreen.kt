package com.example.todoapp

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.wear.compose.material.OutlinedButton

@OptIn(ExperimentalMaterial3Api::class)
//@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SettingsScreen(navigation: NavHostController) {
    Scaffold (
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Settings")
                },
                navigationIcon = {
                    IconButton(onClick = {navigation.popBackStack()}) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Arrow Back")
                    }
                }
            )
        }
    ) {
        innerPadding -> SettingsContent(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun SettingsContent(modifier: Modifier) {

    val context = LocalContext.current

    Column (modifier = modifier) {
        Spacer(modifier = Modifier.height(10.dp))
        TextWithMaterialTheme("Emergency Contacts")
        Spacer(modifier = Modifier.height(10.dp))
        CardWithCheckBox("Max Contacts to send SOS")
        Spacer(modifier = Modifier.height(10.dp))
        TextWithMaterialTheme("Audio Capture")
        CardWithCheckBox("Enable Audio Recording")
        CardWithCheckBox("Recording Duration")

        Row (horizontalArrangement = Arrangement.SpaceAround) {
            OutlinedButton(onClick = {
                Toast.makeText(context, "This is a toast", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, NotificationService::class.java)
                ContextCompat.startForegroundService(context, intent)
            }) {
                Text("Start Service")
            }
            OutlinedButton(onClick = {
//                context.stopService()
            }) {
                Text("Stop Service")
            }
        }

    }
}

@Composable
fun TextWithMaterialTheme(text: String) {
   Row{
       Spacer(modifier = Modifier.size(10.dp))
       Text(text, style = MaterialTheme.typography.titleLarge)
   }
}

@Composable
fun CardWithCheckBox(text: String) {

    var checked by remember { mutableStateOf(false) }

    Card (modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)) {
       Row (modifier = Modifier
           .padding(10.dp)
           .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
           Text(text = text, style = MaterialTheme.typography.bodyLarge)
           Checkbox(checked = checked, onCheckedChange = { checked = !checked })
       }
    }
}