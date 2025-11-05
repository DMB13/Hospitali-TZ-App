package com.labtrace.tz.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Feedback
import com.labtrace.tz.R
import com.labtrace.tz.ui.theme.LabTraceTZTheme
import com.labtrace.tz.viewmodel.SampleViewModel
import com.labtrace.tz.viewmodel.FeedbackViewModel
import com.labtrace.tz.ui.screens.CreateSampleScreen
import com.labtrace.tz.ui.screens.UpdateStatusScreen
import com.labtrace.tz.ui.screens.ViewStatusScreen
import com.labtrace.tz.ui.screens.ExportCsvScreen

class MainActivity : ComponentActivity() {
    private val viewModel: SampleViewModel by viewModels()
    private val feedbackViewModel: FeedbackViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userType = intent.getStringExtra("user_type") ?: "patient"
        setContent {
            LabTraceTZTheme {
                MainScreen(viewModel, userType)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: SampleViewModel, userType: String) {
    var currentScreen by remember { mutableStateOf("main") }
    val context = LocalContext.current

    when (currentScreen) {
        "create" -> CreateSampleScreen(viewModel) { currentScreen = "main" }
        "update" -> UpdateStatusScreen(viewModel) { currentScreen = "main" }
        "view" -> ViewStatusScreen(viewModel) { currentScreen = "main" }
        "export" -> ExportCsvScreen(viewModel) { currentScreen = "main" }
        "reminders" -> RemindersScreen(viewModel) { currentScreen = "main" }
        "feedback" -> FeedbackScreen(feedbackViewModel, userType) { currentScreen = "main" }
        else -> {
            Scaffold(
                topBar = { TopAppBar(title = { Text(stringResource(R.string.app_name)) }) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (userType == "staff") {
                        Button(onClick = { currentScreen = "create" }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Filled.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.create_sample))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { currentScreen = "update" }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Filled.Edit, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.update_status))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { currentScreen = "view" }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Filled.List, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.view_status))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { currentScreen = "export" }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Filled.FileDownload, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.export_csv))
                        }
                    } else {
                        Button(onClick = { currentScreen = "view" }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Filled.List, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.view_status))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { currentScreen = "reminders" }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Filled.Notifications, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reminders")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { currentScreen = "feedback" }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Filled.Feedback, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Feedback")
                        }
                    }
                }
            }
        }
    }
}
