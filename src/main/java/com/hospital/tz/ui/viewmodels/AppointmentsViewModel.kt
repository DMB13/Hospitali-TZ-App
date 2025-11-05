package com.hospital.tz.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hospital.tz.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class AppointmentsViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val appointmentDao = database.appointmentDao()
    private val userDao = database.userDao()
    private val hospitalDao = database.hospitalDao()

    // Current user ID (would be set from authentication)
    private var currentUserId: String? = null

    // State flows for UI
    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()

    private val _upcomingAppointments = MutableStateFlow<List<Appointment>>(emptyList())
    val upcomingAppointments: StateFlow<List<Appointment>> = _upcomingAppointments.asStateFlow()

    private val _doctorAvailability = MutableStateFlow<List<DoctorAvailability>>(emptyList())
    val doctorAvailability: StateFlow<List<DoctorAvailability>> = _doctorAvailability.asStateFlow()

    private val _availableDoctors = MutableStateFlow<List<User>>(emptyList())
    val availableDoctors: StateFlow<List<User>> = _availableDoctors.asStateFlow()

    private val _hospitals = MutableStateFlow<List<Hospital>>(emptyList())
    val hospitals: StateFlow<List<Hospital>> = _hospitals.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadHospitals()
        loadAvailableDoctors()
    }

    fun setCurrentUser(userId: String) {
        currentUserId = userId
        loadUserAppointments()
        loadUpcomingAppointments()
    }

    private fun loadUserAppointments() {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                try {
                    appointmentDao.getAppointmentsForPatient(userId).collect { appointments ->
                        _appointments.value = appointments
                    }
                } catch (e: Exception) {
                    _error.value = "Failed to load appointments: ${e.message}"
                }
            }
        }
    }

    private fun loadUpcomingAppointments() {
        viewModelScope.launch {
            try {
                appointmentDao.getUpcomingAppointments().collect { appointments ->
                    _upcomingAppointments.value = appointments
                }
            } catch (e: Exception) {
                _error.value = "Failed to load upcoming appointments: ${e.message}"
            }
        }
    }

    private fun loadHospitals() {
        viewModelScope.launch {
            try {
                hospitalDao.getAllHospitals().collect { hospitals ->
                    _hospitals.value = hospitals
                }
            } catch (e: Exception) {
                _error.value = "Failed to load hospitals: ${e.message}"
            }
        }
    }

    private fun loadAvailableDoctors() {
        viewModelScope.launch {
            try {
                userDao.getUsersByType(UserType.DOCTOR).collect { doctors ->
                    _availableDoctors.value = doctors
                }
            } catch (e: Exception) {
                _error.value = "Failed to load doctors: ${e.message}"
            }
        }
    }

    fun loadDoctorAvailability(doctorId: String) {
        viewModelScope.launch {
            try {
                appointmentDao.getDoctorAvailability(doctorId).collect { availability ->
                    _doctorAvailability.value = availability
                }
            } catch (e: Exception) {
                _error.value = "Failed to load doctor availability: ${e.message}"
            }
        }
    }

    fun bookAppointment(
        doctorId: String,
        doctorName: String,
        hospitalId: Long,
        hospitalName: String,
        appointmentDate: Long,
        appointmentTime: String,
        appointmentType: AppointmentType = AppointmentType.CONSULTATION,
        symptoms: String? = null,
        notes: String? = null
    ) {
        currentUserId?.let { patientId ->
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    // Get patient name
                    val patient = userDao.getUserById(patientId)
                    if (patient != null) {
                        val appointment = Appointment(
                            patientId = patientId,
                            patientName = patient.name,
                            doctorId = doctorId,
                            doctorName = doctorName,
                            hospitalId = hospitalId,
                            hospitalName = hospitalName,
                            appointmentDate = appointmentDate,
                            appointmentTime = appointmentTime,
                            appointmentType = appointmentType,
                            symptoms = symptoms,
                            notes = notes
                        )

                        val appointmentId = appointmentDao.insertAppointment(appointment)

                        // Schedule reminders
                        scheduleAppointmentReminders(appointmentId)

                        _error.value = null
                    } else {
                        _error.value = "Patient information not found"
                    }
                } catch (e: Exception) {
                    _error.value = "Failed to book appointment: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        } ?: run {
            _error.value = "User not authenticated"
        }
    }

    private suspend fun scheduleAppointmentReminders(appointmentId: Long) {
        val appointment = appointmentDao.getAppointmentById(appointmentId)
        appointment?.let {
            val appointmentTimeMillis = it.appointmentDate // Assuming this is already in milliseconds

            // 24 hours before
            val reminder24h = AppointmentReminder(
                appointmentId = appointmentId,
                reminderType = "24h_before",
                scheduledTime = appointmentTimeMillis - (24 * 60 * 60 * 1000)
            )
            appointmentDao.insertAppointmentReminder(reminder24h)

            // 1 hour before
            val reminder1h = AppointmentReminder(
                appointmentId = appointmentId,
                reminderType = "1h_before",
                scheduledTime = appointmentTimeMillis - (60 * 60 * 1000)
            )
            appointmentDao.insertAppointmentReminder(reminder1h)

            // 15 minutes before
            val reminder15m = AppointmentReminder(
                appointmentId = appointmentId,
                reminderType = "15min_before",
                scheduledTime = appointmentTimeMillis - (15 * 60 * 1000)
            )
            appointmentDao.insertAppointmentReminder(reminder15m)
        }
    }

    fun updateAppointmentStatus(appointmentId: Long, status: AppointmentStatus) {
        viewModelScope.launch {
            try {
                appointmentDao.updateAppointmentStatus(appointmentId, status)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to update appointment status: ${e.message}"
            }
        }
    }

    fun cancelAppointment(appointmentId: Long) {
        updateAppointmentStatus(appointmentId, AppointmentStatus.CANCELLED)
    }

    fun getAvailableTimeSlots(doctorId: String, date: Long): List<String> {
        val calendar = Calendar.getInstance().apply { timeInMillis = date }
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        val availability = _doctorAvailability.value.find { it.dayOfWeek == dayOfWeek && it.doctorId == doctorId }
        return availability?.let { avail ->
            generateTimeSlots(avail.startTime, avail.endTime, avail.slotDurationMinutes)
        } ?: emptyList()
    }

    private fun generateTimeSlots(startTime: String, endTime: String, durationMinutes: Int): List<String> {
        val slots = mutableListOf<String>()
        val startParts = startTime.split(":").map { it.toInt() }
        val endParts = endTime.split(":").map { it.toInt() }

        val startMinutes = startParts[0] * 60 + startParts[1]
        val endMinutes = endParts[0] * 60 + endParts[1]

        var currentMinutes = startMinutes
        while (currentMinutes + durationMinutes <= endMinutes) {
            val hours = currentMinutes / 60
            val minutes = currentMinutes % 60
            slots.add(String.format("%02d:%02d", hours, minutes))
            currentMinutes += durationMinutes
        }

        return slots
    }

    fun checkSlotAvailability(doctorId: String, date: Long, time: String): Boolean {
        return runBlocking {
            appointmentDao.countAppointmentsAtTime(doctorId, date, time) == 0
        }
    }

    fun getAppointmentStatistics(): Flow<AppointmentStatistics> = flow {
        try {
            val totalAppointments = appointmentDao.countAppointmentsInPeriod(
                System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000), // Last 30 days
                System.currentTimeMillis()
            )

            val completedAppointments = appointmentDao.countAppointmentsByStatus(AppointmentStatus.COMPLETED)
            val cancelledAppointments = appointmentDao.countAppointmentsByStatus(AppointmentStatus.CANCELLED)
            val upcomingAppointments = appointmentDao.getUpcomingAppointments().first().size

            emit(AppointmentStatistics(
                totalAppointments = totalAppointments,
                completedAppointments = completedAppointments,
                cancelledAppointments = cancelledAppointments,
                upcomingAppointments = upcomingAppointments
            ))
        } catch (e: Exception) {
            emit(AppointmentStatistics(0, 0, 0, 0))
        }
    }

    fun clearError() {
        _error.value = null
    }

    // Data class for statistics
    data class AppointmentStatistics(
        val totalAppointments: Int,
        val completedAppointments: Int,
        val cancelledAppointments: Int,
        val upcomingAppointments: Int
    )
}

// Extension function for runBlocking (import kotlinx.coroutines.runBlocking)
import kotlinx.coroutines.runBlocking
