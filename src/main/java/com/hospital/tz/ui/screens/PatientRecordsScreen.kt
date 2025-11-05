package com.hospital.tz.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hospital.tz.ui.viewmodels.PatientRecordsViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.LocalHospital

@Composable
fun PatientRecordsScreen(
    userId: String,
    userType: String,
    viewModel: PatientRecordsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(userId, userType) {
        viewModel.setCurrentUser(userId, userType)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Patient Records") },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Patient")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    if (it.isNotEmpty()) {
                        viewModel.searchPatients(it)
                    } else {
                        viewModel.setCurrentUser(userId, userType) // Reload all
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search patients...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                singleLine = true
            )

            // Content
            when (uiState) {
                is PatientRecordsViewModel.PatientRecordsUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is PatientRecordsViewModel.PatientRecordsUiState.Success -> {
                    val records = (uiState as PatientRecordsViewModel.PatientRecordsUiState.Success).records
                    if (records.isEmpty()) {
                        EmptyState()
                    } else {
                        PatientRecordsList(records, userType, viewModel)
                    }
                }
                is PatientRecordsViewModel.PatientRecordsUiState.Error -> {
                    ErrorState((uiState as PatientRecordsViewModel.PatientRecordsUiState.Error).message)
                }
            }
        }
    }

    if (showAddDialog) {
        AddPatientDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { patientData ->
                viewModel.createPatientRecord(
                    patientId = patientData.patientId,
                    patientName = patientData.patientName,
                    dateOfBirth = patientData.dateOfBirth,
                    gender = patientData.gender,
                    bloodType = patientData.bloodType,
                    allergies = patientData.allergies,
                    emergencyContact = patientData.emergencyContact,
                    emergencyPhone = patientData.emergencyPhone,
                    medicalConditions = patientData.medicalConditions,
                    currentMedications = patientData.currentMedications,
                    insuranceProvider = patientData.insuranceProvider,
                    insuranceNumber = patientData.insuranceNumber
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
fun PatientRecordsList(
    records: List<com.hospital.tz.data.PatientRecord>,
    userType: String,
    viewModel: PatientRecordsViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(records) { record ->
            PatientRecordCard(record, userType, viewModel)
        }
    }
}

@Composable
fun PatientRecordCard(
    record: com.hospital.tz.data.PatientRecord,
    userType: String,
    viewModel: PatientRecordsViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Navigate to detail view */ },
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = record.patientName,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("ID: ${record.patientId}", style = MaterialTheme.typography.body2)
                    Text("Gender: ${record.gender}", style = MaterialTheme.typography.body2)
                    Text("Blood Type: ${record.bloodType ?: "Unknown"}", style = MaterialTheme.typography.body2)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Emergency: ${record.emergencyContact}", style = MaterialTheme.typography.body2)
                    Text("Phone: ${record.emergencyPhone}", style = MaterialTheme.typography.body2)
                    if (record.primaryDoctorName != null) {
                        Text("Doctor: ${record.primaryDoctorName}", style = MaterialTheme.typography.body2)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { /* View medical history */ }) {
                    Icon(Icons.Default.MedicalServices, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("History")
                }
                TextButton(onClick = { /* View prescriptions */ }) {
                    Icon(Icons.Default.LocalHospital, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Prescriptions")
                }
            }
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No patient records found",
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Add your first patient record using the + button",
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
    }
}

@Composable
fun ErrorState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Error loading patient records",
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            message,
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center,
            color = Color.Red
        )
    }
}

data class PatientFormData(
    val patientId: String = "",
    val patientName: String = "",
    val dateOfBirth: Long = System.currentTimeMillis(),
    val gender: String = "",
    val bloodType: String? = null,
    val allergies: String = "",
    val emergencyContact: String = "",
    val emergencyPhone: String = "",
    val medicalConditions: String = "",
    val currentMedications: String = "",
    val insuranceProvider: String? = null,
    val insuranceNumber: String? = null
)

@Composable
fun AddPatientDialog(
    onDismiss: () -> Unit,
    onConfirm: (PatientFormData) -> Unit
) {
    var formData by remember { mutableStateOf(PatientFormData()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Patient") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = formData.patientId,
                    onValueChange = { formData = formData.copy(patientId = it) },
                    label = { Text("Patient ID") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = formData.patientName,
                    onValueChange = { formData = formData.copy(patientName = it) },
                    label = { Text("Patient Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = formData.gender,
                    onValueChange = { formData = formData.copy(gender = it) },
                    label = { Text("Gender") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = formData.bloodType ?: "",
                    onValueChange = { formData = formData.copy(bloodType = it.takeIf { it.isNotEmpty() }) },
                    label = { Text("Blood Type (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = formData.allergies,
                    onValueChange = { formData = formData.copy(allergies = it) },
                    label = { Text("Allergies") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = formData.emergencyContact,
                    onValueChange = { formData = formData.copy(emergencyContact = it) },
                    label = { Text("Emergency Contact") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = formData.emergencyPhone,
                    onValueChange = { formData = formData.copy(emergencyPhone = it) },
                    label = { Text("Emergency Phone") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = formData.medicalConditions,
                    onValueChange = { formData = formData.copy(medicalConditions = it) },
                    label = { Text("Medical Conditions") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                OutlinedTextField(
                    value = formData.currentMedications,
                    onValueChange = { formData = formData.copy(currentMedications = it) },
                    label = { Text("Current Medications") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(formData) },
                enabled = formData.patientId.isNotEmpty() && formData.patientName.isNotEmpty()
            ) {
                Text("Add Patient")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
