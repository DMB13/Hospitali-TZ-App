package com.hospital.tz.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(userId: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to Hospital TZ", style = MaterialTheme.typography.h4)
        Spacer(modifier = Modifier.height(16.dp))
        Text("User ID: $userId")
        Spacer(modifier = Modifier.height(32.dp))

        // Large search button for tracing progress
        Button(
            onClick = { /* TODO: Implement search */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Search Hospital Progress")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hospital search functionality placeholder
        OutlinedTextField(
            value = "",
            onValueChange = { /* TODO */ },
            label = { Text("Search Hospitals") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
