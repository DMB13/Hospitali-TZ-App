package com.labtrace.tz.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.labtrace.tz.R
import com.labtrace.tz.viewmodel.SampleViewModel

@Composable
fun CreateSampleScreen(viewModel: SampleViewModel, onBack: () -> Unit) {
    var patientInitials by remember { mutableStateOf("") }
    var sampleType by remember { mutableStateOf("") }
    var destinationLab by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.create_sample)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("<")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            TextField(
                value = patientInitials,
                onValueChange = { patientInitials = it },
                label = { Text(stringResource(R.string.patient_initials)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = sampleType,
                onValueChange = { sampleType = it },
                label = { Text(stringResource(R.string.sample_type)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = destinationLab,
                onValueChange = { destinationLab = it },
                label = { Text(stringResource(R.string.destination_lab)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (patientInitials.isNotBlank() && sampleType.isNotBlank() && destinationLab.isNotBlank()) {
                        viewModel.createSample(patientInitials, sampleType, destinationLab)
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}
