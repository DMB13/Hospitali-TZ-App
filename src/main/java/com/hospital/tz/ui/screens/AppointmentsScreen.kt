package com.hospital.tz.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hospital.tz.data.Appointment
import com.hospital.tz.data.AppointmentStatus
import com.hospital.tz.data.AppointmentType
import com.hospital.tz.data.Hospital
import com.hospital.tz.data.User
import com.hospital.tz.ui.viewmodels.AppointmentsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(
    viewModel: AppointmentsViewModel = viewModel(),
    currentUserId: String
) {
    val appointments by viewModel.appointments.collectAsState()
    val upcomingAppointments by viewModel.upcomingAppointments.collectAsState()
    val hospitals by viewModel.hospitals.collectAsState()
    val availableDoctors by viewModel.availableDoctors.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var showBookAppointmentDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(currentUserId) {
        viewModel.setCurrentUser(currentUserId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Appointments") },
                actions = {
                    IconButton(onClick = { showBookAppointmentDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Book Appointment")
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
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Upcoming") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("All Appointments") }
                )
            }

            when (selectedTab) {
                0 -> AppointmentsList(upcomingAppointments, viewModel)
                1 -> AppointmentsList(appointments, viewModel)
            }
        }

        if (showBookAppointmentDialog) {
            BookAppointmentDialog(
                hospitals = hospitals,
                doctors = availableDoctors,
                viewModel = viewModel,
                onDismiss = { showBookAppointmentDialog = false },
                onBookAppointment = { doctorId, doctorName, hospitalId, hospitalName, date, time, type, symptoms, notes ->
                    viewModel.bookAppointment(doctorId, doctorName, hospitalId, hospitalName, date, time, type, symptoms, notes)
                    showBookAppointmentDialog = false
                }
            )
        }

        error?.let {
            AlertDialog(
                onDismissRequest = { viewModel.clearError() },
                title = { Text("Error") },
                text = { Text(it) },
                confirmButton = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("OK")
                    }
                }
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun AppointmentsList(
    appointments: List<Appointment>,
    viewModel: AppointmentsViewModel
) {
    if (appointments.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No appointments found")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(appointments) { appointment ->
                AppointmentCard(appointment, viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentCard(
    appointment: Appointment,
    viewModel: AppointmentsViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Dr. ${appointment.doctorName}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = appointment.hospitalName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatAppointmentDateTime(appointment.appointmentDate, appointment.appointmentTime),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                StatusChip(appointment.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = appointment.appointmentType.name,
                style = MaterialTheme.typography.bodyMedium
            )

            appointment.symptoms?.let {
                Text(
                    text = "Symptoms: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            appointment.notes?.let {
                Text(
                    text = "Notes: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (appointment.status == AppointmentStatus.SCHEDULED) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { viewModel.cancelAppointment(appointment.id) }) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: AppointmentStatus) {
    val (backgroundColor, textColor) = when (status) {
        AppointmentStatus.SCHEDULED -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        AppointmentStatus.CONFIRMED -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        AppointmentStatus.COMPLETED -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        AppointmentStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        AppointmentStatus.NO_SHOW -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }

    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookAppointmentDialog(
    hospitals: List<Hospital>,
    doctors: List<User>,
    viewModel: AppointmentsViewModel,
    onDismiss: () -> Unit,
    onBookAppointment: (
        doctorId: String,
        doctorName: String,
        hospitalId: Long,
        hospitalName: String,
        date: Long,
        time: String,
        type: AppointmentType,
        symptoms: String?,
        notes: String?
    ) -> Unit
) {
    var selectedHospital by remember { mutableStateOf<Hospital?>(null) }
    var selectedDoctor by remember { mutableStateOf<User?>(null) }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var selectedTime by remember { mutableStateOf<String?>(null) }
    var appointmentType by remember { mutableStateOf(AppointmentType.CONSULTATION) }
    var symptoms by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val availableTimes = remember(selectedDoctor?.id, selectedDate) {
        if (selectedDoctor != null && selectedDate != null) {
            viewModel.getAvailableTimeSlots(selectedDoctor!!.id, selectedDate!!)
        } else {
            emptyList()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Book Appointment") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Hospital selection
                Text("Select Hospital", style = MaterialTheme.typography.titleSmall)
                hospitals.forEach { hospital ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedHospital = hospital }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedHospital == hospital,
                            onClick = { selectedHospital = hospital }
                        )
                        Text(hospital.name, modifier = Modifier.padding(start = 8.dp))
                    }
                }

                // Doctor selection
                if (selectedHospital != null) {
                    Text("Select Doctor", style = MaterialTheme.typography.titleSmall)
                    doctors.forEach { doctor ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedDoctor = doctor }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedDoctor == doctor,
                                onClick = { selectedDoctor = doctor }
                            )
                            Text(doctor.name, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }

                // Date selection
                if (selectedDoctor != null) {
                    Text("Select Date", style = MaterialTheme.typography.titleSmall)
                    // Date picker would go here - simplified for now
                    Text("Date picker placeholder")
                }

                // Time selection
                if (availableTimes.isNotEmpty()) {
                    Text("Select Time", style = MaterialTheme.typography.titleSmall)
                    availableTimes.forEach { time ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedTime = time }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedTime == time,
                                onClick = { selectedTime = time }
                            )
                            Text(time, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }

                // Appointment type
                Text("Appointment Type", style = MaterialTheme.typography.titleSmall)
                AppointmentType.values().forEach { type ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { appointmentType = type }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = appointmentType == type,
                            onClick = { appointmentType = type }
                        )
                        Text(type.name, modifier = Modifier.padding(start = 8.dp))
                    }
                }

                // Symptoms
                OutlinedTextField(
                    value = symptoms,
                    onValueChange = { symptoms = it },
                    label = { Text("Symptoms (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (selectedDoctor != null && selectedHospital != null && selectedDate != null && selectedTime != null) {
                        onBookAppointment(
                            selectedDoctor!!.id,
                            selectedDoctor!!.name,
                            selectedHospital!!.id.toLong(),
                            selectedHospital!!.name,
                            selectedDate!!,
                            selectedTime!!,
                            appointmentType,
                            symptoms.takeIf { it.isNotBlank() },
                            notes.takeIf { it.isNotBlank() }
                        )
                    }
                },
                enabled = selectedDoctor != null && selectedHospital != null && selectedDate != null && selectedTime != null
            ) {
                Text("Book")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun formatAppointmentDateTime(date: Long, time: String): String {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val dateStr = dateFormat.format(Date(date))
    return "$dateStr at $time"
}
