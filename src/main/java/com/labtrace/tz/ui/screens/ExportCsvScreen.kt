package com.labtrace.tz.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.labtrace.tz.R
import com.labtrace.tz.viewmodel.SampleViewModel
import java.io.File
import java.io.FileWriter

@Composable
fun ExportCsvScreen(viewModel: SampleViewModel, onBack: () -> Unit) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.export_csv)) },
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
            Button(
                onClick = {
                    exportToCsv(context, viewModel.samples.value)
                    Toast.makeText(context, "CSV exported to Downloads", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.export_csv))
            }
        }
    }
}

fun exportToCsv(context: Context, samples: List<com.labtrace.tz.data.SampleRecord>) {
    val csvFile = File(context.getExternalFilesDir(null), "samples.csv")
    val writer = FileWriter(csvFile)
    writer.append("Sample ID,Patient Initials,Sample Type,Destination Lab,Status,Last Updated\n")
    samples.forEach {
        writer.append("${it.sampleId},${it.patientInitials},${it.sampleType},${it.destinationLab},${it.status},${it.lastUpdated}\n")
    }
    writer.flush()
    writer.close()
}
