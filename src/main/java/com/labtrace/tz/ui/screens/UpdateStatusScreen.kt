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
fun UpdateStatusScreen(viewModel: SampleViewModel, onBack: () -> Unit) {
    var sampleId by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("") }
    val context = LocalContext.current
    val statuses = listOf(
        stringResource(R.string.collected),
        stringResource(R.string.in_transit),
        stringResource(R.string.received),
        stringResource(R.string.processing),
        stringResource(R.string.results_ready),
        stringResource(R.string.collected_results)
    )

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
                title = { Text(stringResource(R.string.update_status)) },
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
            Text(stringResource(R.string.status))
            statuses.forEach { status ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    RadioButton(
                        selected = selectedStatus == status,
                        onClick = { selectedStatus = status }
                    )
                    Text(status, modifier = Modifier.padding(start = 8.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (sampleId.isNotBlank() && selectedStatus.isNotBlank()) {
                        viewModel.updateStatus(sampleId, selectedStatus)
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
