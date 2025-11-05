package com.labtrace.tz.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.labtrace.tz.viewmodel.SampleViewModel
import com.labtrace.tz.data.SampleRecord

@Composable
fun RemindersScreen(viewModel: SampleViewModel, onBack: () -> Unit) {
    val samples by viewModel.samples.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reminders") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            items(samples) { sample ->
                Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Sample ID: ${sample.sampleId}")
                        Text("Status: ${sample.status}")
                        Text("Last Updated: ${java.util.Date(sample.lastUpdated).toString()}")
                    }
                }
            }
        }
    }
}
