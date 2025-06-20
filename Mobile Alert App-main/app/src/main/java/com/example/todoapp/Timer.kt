package com.example.todoapp

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.Dispatcher
import com.example.todoapp.local.entities.TimerEntity
import com.example.todoapp.viewModel.MyviewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerSettingsScreen(navHostController: NavHostController?) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Timer Settings") },
                navigationIcon = {
                    IconButton(onClick = { navHostController?.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        TimerSettingsContent(Modifier.padding(innerPadding))
    }
}

@Composable
fun TimerSettingsContent(modifier: Modifier = Modifier, viewModel: MyviewModel = viewModel()) {
    var minutes by remember { mutableStateOf("") }
    var seconds by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val (user, isPolice) = getUserData(context)
    val userId = user?.id

    var timerDurations: TimerEntity? = null

    LaunchedEffect(Unit) {
        // get timerdurations for a user
        val timersList = userId?.let { viewModel.getTimersByIdForUser(userId = it) }
        if (timersList != null && timersList.isNotEmpty()) {
            timerDurations = timersList[0]
            minutes = timerDurations!!.minutes.toString()
            seconds = timerDurations!!.seconds.toString()
        } else {
            // Set default values or handle empty timer list case
            minutes = "0"
            seconds = "0"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Set Timer Duration", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = minutes,
                onValueChange = { minutes = it },
                label = { Text("Minutes") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
            )
            OutlinedTextField(
                value = seconds,
                onValueChange = { seconds = it },
                label = { Text("Seconds") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {

            coroutineScope.launch {

                if (userId !== null){
                    val previousTimings = withContext(Dispatchers.IO) {
                        viewModel.getTimersByIdForUser(userId)

                    }

                    val timerToSave = if (previousTimings.isNotEmpty()) {
                        previousTimings[0].copy(
                            minutes = minutes.toInt(),
                            seconds = seconds.toInt(),
                            dateModified = java.util.Date(),
                            modifiedBy = "User"
                        )
                    } else {
                        TimerEntity(
                            userId = userId,
                            minutes = minutes.toInt(),
                            seconds = seconds.toInt(),
                            dateCreated = java.util.Date(),
                            createdBy = "User",
                            dateModified = java.util.Date(),
                            modifiedBy = "User"
                        )
                    }

                    viewModel.insertOrUpdateTimer(timerToSave)

                    Toast.makeText(context, "Timers updated successfully", Toast.LENGTH_SHORT).show()
                }


            }

        }) {
            Text(text = "Save changes")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewTimerSettings() {
    TimerSettingsScreen(navHostController = null)
}
