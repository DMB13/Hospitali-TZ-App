package com.labtrace.tz.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.labtrace.tz.R
import com.labtrace.tz.utils.BarcodeScanner
import com.labtrace.tz.viewmodel.SampleViewModel

@Composable
fun ViewStatusScreen(viewModel: SampleViewModel, onBack: () -> Unit) {
    var sampleId by remember { mutableStateOf("") }
    var sample by remember { mutableStateOf<com.labtrace.tz.data.SampleRecord?>(null) }
    val context = LocalContext.current

    val scanner = remember { BarcodeScanner(context as androidx.activity.ComponentActivity) }
    val scanLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val scanned = scanner.parseScanResult(result.resultCode, result.resultCode, result.data)
        if (scanned != null && scanned != "Cancelled") {
            sampleId = scanned
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.view_status)) },
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
            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = sampleId,
                    onValueChange = { sampleId = it },
                    label = { Text(stringResource(R.string.sample_id)) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { scanLauncher.launch(scanner.initiateScan()) }) {
                    Text(stringResource(R.string.scan_barcode))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    sample = viewModel.getSampleById(sampleId)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Check Status")
            }
            Spacer(modifier = Modifier.height(16.dp))
            sample?.let {
                Text("${stringResource(R.string.patient_initials)}: ${it.patientInitials}")
                Text("${stringResource(R.string.sample_type)}: ${it.sampleType}")
                Text("${stringResource(R.string.destination_lab)}: ${it.destinationLab}")
                Text("${stringResource(R.string.status)}: ${it.status}")
            } ?: run {
                if (sampleId.isNotBlank()) {
                    Text("Sample not found")
                }
            }
        }
    }
}
