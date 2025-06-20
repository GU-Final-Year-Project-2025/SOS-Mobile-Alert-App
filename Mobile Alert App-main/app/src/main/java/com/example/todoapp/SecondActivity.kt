package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todoapp.ui.theme.TodoAppTheme

class SecondActivity: ComponentActivity () {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoAppTheme {
                CreateUI("Watmon Reagans", 24, school = "Gulu University")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Create() {
    CreateUI("Watmon Reagans", 24, school = "Gulu University")
}

@Composable
fun CreateUI(name: String, age: Int, school: String) {
    Column (
        modifier = Modifier.fillMaxSize()
    ){
        Text(text = "Age: $age")
        Spacer(modifier = Modifier.size(10.dp))
        Text(text = "Name: $name")
        Spacer(modifier = Modifier.size(10.dp))
        Text(text = "School: $school")
    }
}