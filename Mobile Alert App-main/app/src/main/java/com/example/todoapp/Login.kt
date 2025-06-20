//package com.example.todoapp
//
//import android.content.Context
//import android.util.Log
//import android.widget.Toast
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Email
//import androidx.compose.material.icons.filled.Lock
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Divider
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedButton
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.vectorResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.text.input.VisualTransformation
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavHostController
//import com.example.todoapp.local.entities.UserEntity
//import com.example.todoapp.viewModel.MyviewModel
//import com.twilio.rest.chat.v1.service.User
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import java.util.Date
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PreviewLogin() {
////    Login()
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun Login(navController: NavHostController) {
//    Scaffold { innerPadding ->
//        LoginContent(modifier = Modifier.padding(innerPadding), navController)
//    }
//}
//
//@Composable
//fun LoginContent(
//    modifier: Modifier = Modifier,
//    navController: NavHostController,
//    viewModel: MyviewModel = viewModel()
//) {
//    // States
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var passwordVisible by remember { mutableStateOf(false) }
//    var loginError by remember { mutableStateOf<String?>(null) }
//    val context = LocalContext.current
//    // Coroutine scope for handling async operations
//    val coroutineScope = rememberCoroutineScope()
//
//    // Check if user is logged in
//    val (userDetails, isPolice) = getUserData(context)
//    if (userDetails != null) {
//        // If the user is already logged in, navigate to the appropriate screen
//        LaunchedEffect(userDetails) {
//            if (isPolice) {
//                navController.navigate("policeDashboard") {
//                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
//                }
//            } else {
//                navController.navigate("sosMessaging") {
//                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
//                }
//            }
//        }
//        return // Return early if user is logged in
//    }
//
//
//    // all users
//    var allUsers = viewModel.allUsers.collectAsState(initial = emptyList())
//
//    Box(
//        modifier = modifier
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.background)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(20.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            // App Logo
//            Box(
//                modifier = Modifier
//                    .size(80.dp)
//                    .clip(CircleShape)
//                    .background(MaterialTheme.colorScheme.primary)
//                    .padding(16.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Lock,
//                    contentDescription = "App Logo",
//                    tint = Color.White,
//                    modifier = Modifier.size(40.dp)
//                )
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Login Header
//            Text(
//                text = "Login to SOS Security Alert",
//                fontSize = 24.sp,
//                fontWeight = FontWeight.Bold,
//                color = MaterialTheme.colorScheme.primary
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Text(
//                text = "Stay safe with real-time security alerts",
//                fontSize = 14.sp,
//                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
//                textAlign = TextAlign.Center
//            )
//
//            // Display login error if exists
//            loginError?.let { error ->
//                Spacer(modifier = Modifier.height(16.dp))
//                Text(
//                    text = error,
//                    color = MaterialTheme.colorScheme.error,
//                    fontSize = 14.sp,
//                    textAlign = TextAlign.Center
//                )
//            }
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // Email field
//            OutlinedTextField(
//                modifier = Modifier.fillMaxWidth(),
//                value = email,
//                onValueChange = {
//                    email = it
//                    loginError = null // Clear error when user starts typing
//                },
//                label = { Text("Email") },
//                leadingIcon = {
//                    Icon(
//                        imageVector = Icons.Default.Email,
//                        contentDescription = "Email Icon"
//                    )
//                },
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
//                shape = RoundedCornerShape(12.dp),
//                isError = loginError != null
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Password field
//            OutlinedTextField(
//                modifier = Modifier.fillMaxWidth(),
//                value = password,
//                onValueChange = {
//                    password = it
//                    loginError = null // Clear error when user starts typing
//                },
//                label = { Text("Password") },
//                leadingIcon = {
//                    Icon(
//                        imageVector = Icons.Default.Lock,
//                        contentDescription = "Password Icon"
//                    )
//                },
//                trailingIcon = {
//                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
//                        Icon(
//                            imageVector = ImageVector.vectorResource(
//                                if (passwordVisible) R.drawable.baseline_visibility_24
//                                else R.drawable.baseline_visibility_off_24
//                            ),
//                            contentDescription = if (passwordVisible) "Hide Password" else "Show Password"
//                        )
//                    }
//                },
//                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
//                shape = RoundedCornerShape(12.dp),
//                isError = loginError != null
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Forgot password
//            TextButton(
//                onClick = {
//                    navController.navigate("forgot password")
//                },
//                modifier = Modifier.align(Alignment.End)
//            ) {
//                Text(
//                    text = "Forgot Password?",
//                    color = MaterialTheme.colorScheme.primary
//                )
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Button(
//                onClick = {
//                    if (email.isBlank() || password.isBlank()) {
//                        loginError = "Please enter both email and password"
//                        return@Button
//                    }
//
//                    coroutineScope.launch(Dispatchers.IO) {
//                        try {
//                            // First check if it's the police login
//                            if (email == "police@gmail.com" && password == "police@123") {
//                                Log.d("Login", "Police login successful")
//
//                                val policeUser = UserEntity(
//                                    fullName = "Uganda Police Force",
//                                    email = "police@gmail.com",
//                                    phoneNumber = "0999999999",
//                                    address = "Police Station",
//                                    password = "police@123", // Should ideally be hashed
//                                    dateCreated = java.util.Date(),
//                                    createdBy = "System",
//                                    dateModified = java.util.Date(),
//                                    modifiedBy = "System"
//                                )
//
//                                withContext(Dispatchers.Main) {
//                                    SaveUserData(context, policeUser, true)
//                                    Toast.makeText(context, "Police Login Successful", Toast.LENGTH_SHORT).show()
//
//                                    // Make sure the route name matches exactly what's in your NavHost
//                                    Log.d("Navigation", "Attempting to navigate to policeDashboard")
//                                    navController.navigate("policeDashboard") {
//                                        // Clear the back stack completely
//                                        popUpTo(0) { inclusive = true }
//                                    }
//                                }
//                            } else {
//                                // Try regular user login
//                                val user = viewModel.getUserByEmailAndPassword(email, password)
//
//                                withContext(Dispatchers.Main) {
//                                    if (user != null) {
//                                        Log.d("Login", "User found: ${user.email}")
//                                        SaveUserData(context, user)
//                                        Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
//                                        navController.navigate("sosMessaging") {
//                                            popUpTo(0) { inclusive = true }
//                                        }
//                                    } else {
//                                        Log.d("Login", "Invalid login attempt for $email")
//                                        loginError = "Invalid email or password"
//                                        Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show()
//                                    }
//                                }
//                            }
//                        } catch (e: Exception) {
//                            Log.e("Login", "Login error", e)
//                            withContext(Dispatchers.Main) {
//                                loginError = "An error occurred. Please try again."
//                                Toast.makeText(context, "Login failed due to an error", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    }
//                },
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(12.dp),
//                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
//            ) {
//                Text(
//                    text = "LOGIN",
//                    modifier = Modifier.padding(vertical = 8.dp),
//                    fontWeight = FontWeight.Bold
//                )
//            }
//
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // Or divider
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Divider(
//                    modifier = Modifier
//                        .weight(1f)
//                        .padding(end = 8.dp)
//                )
//                Text(
//                    text = "OR",
//                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
//                    fontSize = 12.sp
//                )
//                Divider(
//                    modifier = Modifier
//                        .weight(1f)
//                        .padding(start = 8.dp)
//                )
//            }
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // Social Login Buttons
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceEvenly
//            ) {
//                // Google Login
//                OutlinedButton(
//                    onClick = { /* Google login action */ },
//                    modifier = Modifier
//                        .weight(1f)
//                        .padding(end = 8.dp),
//                    shape = RoundedCornerShape(12.dp),
//                    border = ButtonDefaults.outlinedButtonBorder
//                ) {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.Center
//                    ) {
//                        // Replace with actual Google icon
//                        Box(
//                            modifier = Modifier
//                                .size(24.dp)
//                                .background(Color.Red, CircleShape),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Text("G", color = Color.White, fontWeight = FontWeight.Bold)
//                        }
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text("Google")
//                    }
//                }
//
//                // Facebook Login
//                OutlinedButton(
//                    onClick = { /* Facebook login action */ },
//                    modifier = Modifier
//                        .weight(1f)
//                        .padding(start = 8.dp),
//                    shape = RoundedCornerShape(12.dp),
//                    border = ButtonDefaults.outlinedButtonBorder
//                ) {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.Center
//                    ) {
//                        // Replace with actual Facebook icon
//                        Box(
//                            modifier = Modifier
//                                .size(24.dp)
//                                .background(Color.Blue, CircleShape),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Text("f", color = Color.White, fontWeight = FontWeight.Bold)
//                        }
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text("Facebook")
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // Sign up suggestion
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "Don't have an account?",
//                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
//                )
//                TextButton(onClick = {
//                    navController.navigate("signup")
//                }) {
//                    Text(
//                        text = "Sign Up",
//                        fontWeight = FontWeight.Bold,
//                        color = MaterialTheme.colorScheme.primary
//                    )
//                }
//            }
//        }
//    }
//}
//
//fun SaveUserData(context: Context, user: UserEntity, isPolice: Boolean = false) {
//    val sharedPreferences = context.getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
//    val editor = sharedPreferences.edit()
//    editor.putString("userId", user.id.toString())
//    editor.putString("userName", user.fullName)
//    editor.putString("userEmail", user.email)
//    editor.putString("address", user.address)
//    editor.putString("password", user.password)
//    editor.putString("phoneNumber", user.phoneNumber)
//    editor.putBoolean("isPolice", isPolice) // Add this line
//    editor.apply()
//}
//
//fun clearUserData(context: Context) {
//    val sharedPreferences = context.getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
//    sharedPreferences.edit().clear().apply()
//}
//
//fun getUserData(context: Context): Pair<UserEntity?, Boolean> {
//    val sharedPreferences = context.getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
//
//    // Get all the user data
//    val userId = sharedPreferences.getString("userId", null)
//    val userName = sharedPreferences.getString("userName", null)
//    val userEmail = sharedPreferences.getString("userEmail", null)
//    val address = sharedPreferences.getString("address", null)
//    val phoneNumber = sharedPreferences.getString("phoneNumber", null)
//    val password = sharedPreferences.getString("password", null)
//    val isPolice = sharedPreferences.getBoolean("isPolice", false)
//
//    // Return null if any required data is missing
//    if (userId == null || userName == null || userEmail == null ||
//        address == null || phoneNumber == null || password == null) {
//        return Pair(null, false)
//    }
//
//    // Return the user data if all fields are present
//    return Pair(
//        UserEntity(
//            id = userId.toInt(),
//            fullName = userName,
//            email = userEmail,
//            address = address,
//            password = password,
//            phoneNumber = phoneNumber,
//            dateCreated = Date(),
//            createdBy = "defaultCreator",
//            dateModified = Date(),
//            modifiedBy = "defaultModifier"
//        ),
//        isPolice
//    )
//}
//









package com.example.todoapp

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.todoapp.local.entities.UserEntity
import com.example.todoapp.viewModel.MyviewModel
import com.twilio.rest.chat.v1.service.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewLogin() {
//    Login()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(navController: NavHostController) {
    Scaffold { innerPadding ->
        LoginContent(modifier = Modifier.padding(innerPadding), navController)
    }
}

@Composable
fun LoginContent(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: MyviewModel = viewModel()
) {
    // States
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    // Coroutine scope for handling async operations
    val coroutineScope = rememberCoroutineScope()

    // Check if user is logged in
    val (userDetails, userType) = getUserData(context)
    if (userDetails != null) {
        // If the user is already logged in, navigate to the appropriate screen
        LaunchedEffect(userDetails) {
            when (userType) {
                "police" -> {
                    navController.navigate("policeDashboard") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
                "admin" -> {
                    navController.navigate("adminDashboard") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
                else -> {
                    navController.navigate("sosMessaging") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            }
        }
        return // Return early if user is logged in
    }

    // all users
    var allUsers = viewModel.allUsers.collectAsState(initial = emptyList())

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "App Logo",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Login Header
            Text(
                text = "Login to SOS Security Alert",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Stay safe with real-time security alerts",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            // Display login error if exists
            loginError?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Email field
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = email,
                onValueChange = {
                    email = it
                    loginError = null // Clear error when user starts typing
                },
                label = { Text("Email") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email Icon"
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(12.dp),
                isError = loginError != null
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password field
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = password,
                onValueChange = {
                    password = it
                    loginError = null // Clear error when user starts typing
                },
                label = { Text("Password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password Icon"
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(
                                if (passwordVisible) R.drawable.baseline_visibility_24
                                else R.drawable.baseline_visibility_off_24
                            ),
                            contentDescription = if (passwordVisible) "Hide Password" else "Show Password"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(12.dp),
                isError = loginError != null
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Forgot password
            TextButton(
                onClick = {
                    navController.navigate("forgot password")
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = "Forgot Password?",
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        loginError = "Please enter both email and password"
                        return@Button
                    }

                    coroutineScope.launch(Dispatchers.IO) {
                        try {
                            // Check for special accounts first
                            when {
                                // Admin login
                                email == "admin@gmail.com" && password == "admin@123" -> {
                                    Log.d("Login", "Admin login successful")

                                    val adminUser = UserEntity(
                                        fullName = "System Administrator",
                                        email = "admin@gmail.com",
                                        phoneNumber = "0777777777",
                                        address = "Admin Office",
                                        password = "admin@123", // Should ideally be hashed
                                        dateCreated = java.util.Date(),
                                        createdBy = "System",
                                        dateModified = java.util.Date(),
                                        modifiedBy = "System"
                                    )

                                    withContext(Dispatchers.Main) {
                                        SaveUserData(context, adminUser, "admin")
                                        Toast.makeText(context, "Admin Login Successful", Toast.LENGTH_SHORT).show()

                                        Log.d("Navigation", "Attempting to navigate to adminDashboard")
                                        navController.navigate("adminDashboard") {
                                            // Clear the back stack completely
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                }

                                // Police login
                                email == "police@gmail.com" && password == "police@123" -> {
                                    Log.d("Login", "Police login successful")

                                    val policeUser = UserEntity(
                                        fullName = "Uganda Police Force",
                                        email = "police@gmail.com",
                                        phoneNumber = "0999999999",
                                        address = "Police Station",
                                        password = "police@123", // Should ideally be hashed
                                        dateCreated = java.util.Date(),
                                        createdBy = "System",
                                        dateModified = java.util.Date(),
                                        modifiedBy = "System"
                                    )

                                    withContext(Dispatchers.Main) {
                                        SaveUserData(context, policeUser, "police")
                                        Toast.makeText(context, "Police Login Successful", Toast.LENGTH_SHORT).show()

                                        Log.d("Navigation", "Attempting to navigate to policeDashboard")
                                        navController.navigate("policeDashboard") {
                                            // Clear the back stack completely
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                }

                                // Regular user login
                                else -> {
                                    val user = viewModel.getUserByEmailAndPassword(email, password)

                                    withContext(Dispatchers.Main) {
                                        if (user != null) {
                                            Log.d("Login", "User found: ${user.email}")
                                            SaveUserData(context, user, "user")
                                            Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                                            navController.navigate("sosMessaging") {
                                                popUpTo(0) { inclusive = true }
                                            }
                                        } else {
                                            Log.d("Login", "Invalid login attempt for $email")
                                            loginError = "Invalid email or password"
                                            Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("Login", "Login error", e)
                            withContext(Dispatchers.Main) {
                                loginError = "An error occurred. Please try again."
                                Toast.makeText(context, "Login failed due to an error", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Text(
                    text = "LOGIN",
                    modifier = Modifier.padding(vertical = 8.dp),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Or divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                Text(
                    text = "OR",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
                Divider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Social Login Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Google Login
                OutlinedButton(
                    onClick = { /* Google login action */ },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Replace with actual Google icon
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color.Red, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("G", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Google")
                    }
                }

                // Facebook Login
                OutlinedButton(
                    onClick = { /* Facebook login action */ },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Replace with actual Facebook icon
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color.Blue, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("f", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Facebook")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign up suggestion
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account?",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                TextButton(onClick = {
                    navController.navigate("signup")
                }) {
                    Text(
                        text = "Sign Up",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// Updated SaveUserData function to handle user types
fun SaveUserData(context: Context, user: UserEntity, userType: String = "user") {
    val sharedPreferences = context.getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("userId", user.id.toString())
    editor.putString("userName", user.fullName)
    editor.putString("userEmail", user.email)
    editor.putString("address", user.address)
    editor.putString("password", user.password)
    editor.putString("phoneNumber", user.phoneNumber)
    editor.putString("userType", userType) // Store user type instead of just isPolice
    editor.apply()
}

fun clearUserData(context: Context) {
    val sharedPreferences = context.getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().clear().apply()
}

// Updated getUserData function to return user type instead of boolean
fun getUserData(context: Context): Pair<UserEntity?, String> {
    val sharedPreferences = context.getSharedPreferences("userPrefs", Context.MODE_PRIVATE)

    // Get all the user data
    val userId = sharedPreferences.getString("userId", null)
    val userName = sharedPreferences.getString("userName", null)
    val userEmail = sharedPreferences.getString("userEmail", null)
    val address = sharedPreferences.getString("address", null)
    val phoneNumber = sharedPreferences.getString("phoneNumber", null)
    val password = sharedPreferences.getString("password", null)
    val userType = sharedPreferences.getString("userType", "user")

    // Return null if any required data is missing
    if (userId == null || userName == null || userEmail == null ||
        address == null || phoneNumber == null || password == null) {
        return Pair(null, "user")
    }

    // Return the user data if all fields are present
    return Pair(
        UserEntity(
            id = userId.toInt(),
            fullName = userName,
            email = userEmail,
            address = address,
            password = password,
            phoneNumber = phoneNumber,
            dateCreated = Date(),
            createdBy = "defaultCreator",
            dateModified = Date(),
            modifiedBy = "defaultModifier"
        ),
        userType ?: "user"
    )
}