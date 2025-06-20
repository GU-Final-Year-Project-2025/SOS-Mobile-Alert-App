package com.example.todoapp

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.todoapp.local.entities.UserEntity
import com.example.todoapp.viewModel.MyviewModel
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Profile")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back Arrow")
                    }
                }
            )
        }
    ) { innerPadding ->
        ProfileMainContent(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun ProfileMainContent(modifier: Modifier, viewModel: MyviewModel = viewModel()) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // State for current user
    var currentUser by remember { mutableStateOf<UserEntity?>(null) }
    var openDialog by remember { mutableStateOf(false) }

    // Default userId - in a real app, you'd get this from a logged-in session
    val userDetails = getUserData(context)
    val (user, isPolice) = getUserData(context)
    val userId = user?.id

    // Fetch user data when component loads
    LaunchedEffect(userId) {
        currentUser = userId?.let { viewModel.getUserByIdModified(it) }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Profile image
        Surface(
            shape = CircleShape,
            border = BorderStroke(6.dp, MaterialTheme.colorScheme.primary)
        ) {
//            Image(
//                painter = painterResource(id = R.drawable.logo),
//                contentDescription = "Profile Picture",
//                modifier = Modifier.size(150.dp)
//            )
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(Color.Red),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SOS",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 35.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        // User name display
        currentUser?.let {
            Text(text = it.fullName, style = MaterialTheme.typography.titleLarge)
        }

        // Profile info card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(10.dp)
            ) {
                currentUser?.let {
                    CardText("Name:", it.fullName)
                    CardText("Email:", it.email)
                    CardText("Contact:", it.phoneNumber)
                    CardText("Address:", it.address)
                }
            }

            Button(
                onClick = { openDialog = true },
                modifier = Modifier.padding(start = 10.dp, bottom = 10.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Edit Profile")
            }
        }

        // Edit profile dialog
        if (openDialog && currentUser != null) {
            ProfileEditDialog(
                user = currentUser!!,
                onDismiss = { openDialog = false },
                onSave = { updatedUser ->
                    coroutineScope.launch {
                        viewModel.updateProfile(updatedUser)
                        // Refresh the current user data
                        currentUser = userId?.let { viewModel.getUserByIdModified(it) }
                        Toast.makeText(context, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}

@Composable
fun CardText(title: String, value: String) {
    Spacer(modifier = Modifier.height(2.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.size(10.dp))
        Text(text = value, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun ProfileEditDialog(
    user: UserEntity,
    onDismiss: () -> Unit,
    onSave: (UserEntity) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            var name by remember { mutableStateOf(user.fullName) }
            var email by remember { mutableStateOf(user.email) }
            var phoneNumber by remember { mutableStateOf(user.phoneNumber) }
            var address by remember { mutableStateOf(user.address) }

            Column(modifier = Modifier.padding(16.dp)) {
                Text("Edit Profile", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    label = { Text("Name") },
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    label = { Text("Email") },
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phoneNumber,
                    label = { Text("Phone Number") },
                    onValueChange = { phoneNumber = it },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = address,
                    label = { Text("Address") },
                    onValueChange = { address = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val updatedUser = user.copy(
                                fullName = name,
                                email = email,
                                phoneNumber = phoneNumber,
                                address = address,
                                dateModified = Date(),
                                modifiedBy = "User" // In real app, use actual username
                            )
                            onSave(updatedUser)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}