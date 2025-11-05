package com.hospital.tz.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hospital.tz.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val announcementDao = database.announcementDao()
    private val hospitalDao = database.hospitalDao()
    private val patientProgressDao = database.patientProgressDao()

    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    val announcements: StateFlow<List<Announcement>> = _announcements

    private val _hospitals = MutableStateFlow<List<Hospital>>(emptyList())
    val hospitals: StateFlow<List<Hospital>> = _hospitals

    private val _patientProgress = MutableStateFlow<PatientProgress?>(null)
    val patientProgress: StateFlow<PatientProgress?> = _patientProgress

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _patientIdQuery = MutableStateFlow("")
    val patientIdQuery: StateFlow<String> = _patientIdQuery

    init {
        loadAnnouncements()
        loadHospitals()
        // Load sample data for demo
        loadSampleData()
    }

    private fun loadAnnouncements() {
        viewModelScope.launch {
            _announcements.value = announcementDao.getAllAnnouncements()
        }
    }

    private fun loadHospitals() {
        viewModelScope.launch {
            _hospitals.value = hospitalDao.getAllActiveHospitals()
        }
    }

    private fun loadSampleData() {
        viewModelScope.launch {
            // Add sample announcements
            val sampleAnnouncements = listOf(
                Announcement(
                    id = "ann1",
                    title = "New COVID-19 Vaccination Center",
                    content = "Muhimbili National Hospital has opened a new vaccination center. Walk-ins welcome.",
                    hospitalName = "Muhimbili National Hospital",
                    timestamp = System.currentTimeMillis() - 86400000 // 1 day ago
                ),
                Announcement(
                    id = "ann2",
                    title = "Free Health Screening",
                    content = "Bugando Medical Centre is offering free health screenings this weekend.",
                    hospitalName = "Bugando Medical Centre",
                    timestamp = System.currentTimeMillis() - 172800000 // 2 days ago
                )
            )
            sampleAnnouncements.forEach { announcementDao.insert(it) }

            // Add sample hospitals
            val sampleHospitals = listOf(
                Hospital(
                    id = "hosp1",
                    name = "Muhimbili National Hospital",
                    type = HospitalType.HOSPITAL,
                    region = "Dar es Salaam",
                    district = "Ilala",
                    latitude = -6.7924,
                    longitude = 39.2083,
                    address = "United Nations Road, Upanga West, Dar es Salaam",
                    phone = "+255 22 2151362",
                    email = "info@muhimbili.go.tz",
                    services = "Emergency Care, Surgery, Maternity, Pediatrics"
                ),
                Hospital(
                    id = "hosp2",
                    name = "Bugando Medical Centre",
                    type = HospitalType.HOSPITAL,
                    region = "Mwanza",
                    district = "Nyamagana",
                    latitude = -2.5167,
                    longitude = 32.9000,
                    address = "Mwanza, Tanzania",
                    phone = "+255 28 2500054",
                    services = "Teaching Hospital, Research, Specialized Care"
                )
            )
            hospitalDao.insertAll(sampleHospitals)

            // Add sample patient progress
            val sampleProgress = PatientProgress(
                patientId = "PT-001",
                patientName = "John Doe",
                diagnosis = "Malaria",
                status = "Admitted",
                hospitalName = "Muhimbili National Hospital",
                doctorName = "Dr. Sarah Johnson",
                roomNumber = "201",
                admissionDate = System.currentTimeMillis() - 259200000, // 3 days ago
                lastUpdate = System.currentTimeMillis(),
                nextAppointment = System.currentTimeMillis() + 604800000, // 1 week from now
                notes = "Patient responding well to treatment"
            )
            patientProgressDao.insert(sampleProgress)

            // Reload data
            loadAnnouncements()
            loadHospitals()
        }
    }

    fun searchHospitals(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            _hospitals.value = if (query.isBlank()) {
                hospitalDao.getAllActiveHospitals()
            } else {
                hospitalDao.searchHospitals(query)
            }
        }
    }

    fun searchPatientProgress(patientId: String) {
        _patientIdQuery.value = patientId
        viewModelScope.launch {
            _patientProgress.value = patientProgressDao.getProgressByPatientId(patientId)
        }
    }

    fun markAnnouncementAsRead(announcementId: String) {
        viewModelScope.launch {
            announcementDao.markAsRead(announcementId)
            loadAnnouncements() // Refresh list
        }
    }
}
