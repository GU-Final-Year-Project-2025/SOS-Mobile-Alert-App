package com.example.todoapp

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.todoapp.local.entities.UserEntity
import com.example.todoapp.viewModel.MyviewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.util.Date

@Composable
fun SignUp(
    navHostController: NavHostController? = null,
    viewModel: MyviewModel = viewModel()
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isRegistrationSuccessful by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Snackbar state
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Sign Up", fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Full Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Full Name")
                }
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = "Email")
                }
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "")
                },
                trailingIcon = {
                    Image(
                        imageVector = if (passwordVisible) ImageVector.vectorResource(id = R.drawable.baseline_visibility_24)
                        else ImageVector.vectorResource(id = R.drawable.baseline_visibility_off_24),
                        contentDescription = "Toggle Password",
                        modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                    )
//                    Icon(imageVector = ImageVector.vectorResource(if (passwordVisible) R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_off_24), contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = "Phone Number")
                }
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))

//            Button(
//                onClick = {
//                    // Validate input fields
//                    if (validateInput(username, email, password, phoneNumber)) {
//                        val currentDate = Date()
//                        val user = UserEntity(
//                            fullName = username,
//                            email = email,
//                            phoneNumber = phoneNumber,
//                            address = address,
//                            dateCreated = currentDate,
//                            createdBy = "SignUp",
//                            dateModified = currentDate,
//                            modifiedBy = "SignUp"
//                        )
//
//                        // Add user to database
//                        viewModel.addUser(user)
//
//                        // Show success message
//                        isRegistrationSuccessful = true
//
//                        // Optional: Navigate to next screen or show success dialog
//                        navHostController?.navigate("login")
//                    } else {
//                        // Show error message
//                        // Using coroutine to show snackbar
//                        viewModel.viewModelScope.launch {
//                            snackbarHostState.showSnackbar(
//                                message = "Please fill all required fields correctly",
//                                duration = SnackbarDuration.Short
//                            )
//                        }
//                    }
//                },
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(10.dp)
//            ) {
//                Text("Sign Up")
//            }

            Button(
                onClick = {
                    // Validate input fields
                    if (validateInput(username, email, password, phoneNumber)) {
                        val currentDate = Date()
                        val user = UserEntity(
                            fullName = username,
                            email = email,
                            phoneNumber = phoneNumber,
                            address = address,
                            password = password,
                            dateCreated = currentDate,
                            createdBy = "SignUp",
                            dateModified = currentDate,
                            modifiedBy = "SignUp"
                        )

                        viewModel.viewModelScope.launch(Dispatchers.IO) {
                            try {
                                viewModel.addUser(user)
                                withContext(Dispatchers.Main) {
                                    // UI-related operations like navigation
                                    Toast.makeText(context, "Registered successfully", Toast.LENGTH_SHORT).show()
                                    navHostController?.navigate("login")
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    snackbarHostState.showSnackbar(
                                        message = "Registration failed: ${e.localizedMessage}",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        }

                    } else {
                        // Show error message for invalid input
                        viewModel.viewModelScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Please fill all required fields correctly",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Sign Up")
            }
        }
    }
}

// Input validation function
private fun validateInput(
    username: String,
    email: String,
    password: String,
    phoneNumber: String
): Boolean {
    return username.isNotBlank() &&
            email.isNotBlank() &&
            email.contains("@") &&
            password.length >= 6 &&
            phoneNumber.isNotBlank()
}

@Preview(showBackground = true)
@Composable
fun SignUpPreview() {
    SignUp()
}