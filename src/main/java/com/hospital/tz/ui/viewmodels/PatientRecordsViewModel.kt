package com.hospital.tz.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hospital.tz.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PatientRecordsViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val patientRecordDao = database.patientRecordDao()

    private val _uiState = MutableStateFlow<PatientRecordsUiState>(PatientRecordsUiState.Loading)
    val uiState: StateFlow<PatientRecordsUiState> = _uiState.asStateFlow()

    private var currentUserId: String = ""
    private var currentUserType: String = ""

    fun setCurrentUser(userId: String, userType: String) {
        currentUserId = userId
        currentUserType = userType
        loadPatientRecords()
    }

    private fun loadPatientRecords() {
        viewModelScope.launch {
            try {
                _uiState.value = PatientRecordsUiState.Loading
                val records = when (currentUserType) {
                    "Doctor" -> patientRecordDao.getPatientRecordsForDoctor(currentUserId)
                    "Patient" -> patientRecordDao.getPatientRecordByPatientId(currentUserId)?.let { listOf(it) } ?: emptyList()
                    else -> patientRecordDao.getAllPatientRecords()
                }
                records.collect { patientRecords ->
                    _uiState.value = PatientRecordsUiState.Success(patientRecords)
                }
            } catch (e: Exception) {
                _uiState.value = PatientRecordsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun createPatientRecord(
        patientId: String,
        patientName: String,
        dateOfBirth: Long,
        gender: String,
        bloodType: String?,
        allergies: String,
        emergencyContact: String,
        emergencyPhone: String,
        medicalConditions: String,
        currentMedications: String,
        insuranceProvider: String?,
        insuranceNumber: String?
    ) {
        viewModelScope.launch {
            try {
                val record = PatientRecord(
                    patientId = patientId,
                    patientName = patientName,
                    dateOfBirth = dateOfBirth,
                    gender = gender,
                    bloodType = bloodType,
                    allergies = allergies,
                    emergencyContact = emergencyContact,
                    emergencyPhone = emergencyPhone,
                    medicalConditions = medicalConditions,
                    currentMedications = currentMedications,
                    insuranceProvider = insuranceProvider,
                    insuranceNumber = insuranceNumber,
                    primaryDoctorId = if (currentUserType == "Doctor") currentUserId else null,
                    primaryDoctorName = if (currentUserType == "Doctor") "Dr. Current User" else null
                )
                patientRecordDao.insertPatientRecord(record)
                loadPatientRecords()
            } catch (e: Exception) {
                _uiState.value = PatientRecordsUiState.Error("Failed to create patient record: ${e.message}")
            }
        }
    }

    sealed class PatientRecordsUiState {
        object Loading : PatientRecordsUiState()
        data class Success(val records: List<PatientRecord>) : PatientRecordsUiState()
        data class Error(val message: String) : PatientRecordsUiState()
    }
}
