package com.example.todoapp

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomSheetScreen() {
    Scaffold {
        innerPadding -> MyBottomSheetContent(modifier = Modifier.padding(innerPadding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBottomSheetContent(modifier: Modifier) {

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column() {
            Text("Username: ${username}", fontSize = 25.sp)
            Text("Email: ${email}", fontSize = 25.sp)
            Text("Password: ${password}", fontSize = 25.sp)
            InnerBottomComponent(modifier = Modifier.fillMaxWidth(), setname = { data -> username = data })
            Button(onClick = {showBottomSheet = !showBottomSheet}) {
                Text("Open Modal")
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {showBottomSheet = !showBottomSheet}
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(
                    label = { Text("Username") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null)
                    },
                    onValueChange = {
                        username = it
                    }, value = username, placeholder = { Text("Enter your name") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.size(10.dp))
                OutlinedTextField(
                    label = { Text("Email") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null)
                    },
                    isError = true,
                    supportingText = {
                        Text("Email must not be empty")
                    },
                    onValueChange = {
                        email = it
                    }, value = email, placeholder = { Text("Enter your name") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.size(10.dp))

                OutlinedTextField(
                    label = { Text("Password") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null)
                    },
                    trailingIcon = {
                        Icon(imageVector = ImageVector.vectorResource(R.drawable.visible_eye), contentDescription = null)
                    },
                    isError = true,
                    supportingText = {
                        Text("Password must be 6 characters")
                    },
                    onValueChange = {
                        password = it
                    }, value = password, placeholder = { Text("Enter your name") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.size(10.dp))

                Button(onClick = {showBottomSheet = !showBottomSheet}, modifier=Modifier.fillMaxWidth()) {
                    Text("Submit Response")
                }
            }
        }
    }
}

@Composable
fun InnerBottomComponent(modifier: Modifier, setname: (String) -> Unit) {

    var name by remember { mutableStateOf("Tom cat") }

    Column {
        Text(text = "This is the name: ${name}", fontSize = 24.sp)
        Spacer(modifier = Modifier.size(10.dp))
        OutlinedButton(onClick = {setname("This is wat")}) {
            Text("Click on this to change name")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ShowBottomSheetPreview() {
    BottomSheetScreen()
}