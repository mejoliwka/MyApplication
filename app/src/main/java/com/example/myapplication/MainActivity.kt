package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    val database = Firebase.database
    val myRef = database.getReference("message")

    // Variables to manage input and displayed data
    var messIn by remember { mutableStateOf("") }
    var databaseText by remember { mutableStateOf("Loading...") }

    // Listener for real-time updates
    LaunchedEffect(Unit) {
        myRef.child("messages").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Update displayed text whenever data changes
                databaseText = snapshot.value?.toString() ?: "No data found"
            }

            override fun onCancelled(error: DatabaseError) {
                databaseText = "Error: ${error.message}"
            }
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TextField for user input
        TextField(
            value = messIn,
            onValueChange = { messIn = it },
            label = { Text("Wpisz tekst") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Button to save data
        Button(onClick = {
            if (messIn.isNotBlank()) {
                myRef.child("messages").setValue(messIn).addOnFailureListener { exception ->
                    databaseText = "Error saving data: ${exception.message}"
                }
            }
        }) {
            Text("Wyślij")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display data from Firebase
        Text(
            text = "Dane z bazy danych:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = databaseText,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting()
    }
}
