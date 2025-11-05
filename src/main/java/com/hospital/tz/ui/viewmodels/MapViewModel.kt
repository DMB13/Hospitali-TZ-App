package com.hospital.tz.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hospital.tz.data.AppDatabase
import com.hospital.tz.data.Hospital
import com.hospital.tz.data.HospitalDao
import com.hospital.tz.data.HospitalType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val hospitalDao = database.hospitalDao()

    private val _hospitals = MutableStateFlow<List<Hospital>>(emptyList())
    val hospitals: StateFlow<List<Hospital>> = _hospitals

    private val _selectedHospital = MutableStateFlow<Hospital?>(null)
    val selectedHospital: StateFlow<Hospital?> = _selectedHospital

    private val _userLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val userLocation: StateFlow<Pair<Double, Double>?> = _userLocation

    private val _isOfflineMode = MutableStateFlow(false)
    val isOfflineMode: StateFlow<Boolean> = _isOfflineMode

    init {
        loadHospitals()
        loadSampleHospitals()
    }

    private fun loadHospitals() {
        viewModelScope.launch {
            _hospitals.value = hospitalDao.getAllActiveHospitals()
        }
    }

    private fun loadSampleHospitals() {
        viewModelScope.launch {
            // Add more sample hospitals across Tanzania
            val sampleHospitals = listOf(
                Hospital(
                    id = "hosp3",
                    name = "Kilimanjaro Christian Medical Centre",
                    type = HospitalType.HOSPITAL,
                    region = "Kilimanjaro",
                    district = "Moshi",
                    latitude = -3.3333,
                    longitude = 37.3333,
                    address = "Moshi, Kilimanjaro Region",
                    phone = "+255 27 2752096",
                    services = "Specialized Care, Research, Training"
                ),
                Hospital(
                    id = "hosp4",
                    name = "Aga Khan Hospital",
                    type = HospitalType.HOSPITAL,
                    region = "Dar es Salaam",
                    district = "Kinondoni",
                    latitude = -6.7667,
                    longitude = 39.2833,
                    address = "Ocean Road, Dar es Salaam",
                    phone = "+255 22 2115151",
                    services = "Private Healthcare, Specialized Services"
                ),
                Hospital(
                    id = "hosp5",
                    name = "Tanga General Hospital",
                    type = HospitalType.HOSPITAL,
                    region = "Tanga",
                    district = "Tanga",
                    latitude = -5.0667,
                    longitude = 39.1000,
                    address = "Tanga, Tanzania",
                    phone = "+255 27 2640460",
                    services = "General Medicine, Surgery, Maternity"
                ),
                Hospital(
                    id = "disp1",
                    name = "Upanga Health Center",
                    type = HospitalType.DISPENSARY,
                    region = "Dar es Salaam",
                    district = "Ilala",
                    latitude = -6.7833,
                    longitude = 39.2667,
                    address = "Upanga, Dar es Salaam",
                    phone = "+255 22 2150000",
                    services = "Primary Care, Vaccinations, Basic Treatments"
                ),
                Hospital(
                    id = "pharm1",
                    name = "Duka la Dawa Baridi",
                    type = HospitalType.PHARMACY,
                    region = "Dar es Salaam",
                    district = "Kinondoni",
                    latitude = -6.7500,
                    longitude = 39.2333,
                    address = "Kinondoni, Dar es Salaam",
                    phone = "+255 22 2770000",
                    services = "Prescription Drugs, OTC Medicines, Health Supplies"
                )
            )

            // Insert only if they don't exist
            sampleHospitals.forEach { hospital ->
                val existing = hospitalDao.getHospitalById(hospital.id)
                if (existing == null) {
                    hospitalDao.insert(hospital)
                }
            }

            loadHospitals() // Refresh the list
        }
    }

    fun selectHospital(hospital: Hospital) {
        _selectedHospital.value = hospital
    }

    fun clearSelection() {
        _selectedHospital.value = null
    }

    fun updateUserLocation(latitude: Double, longitude: Double) {
        _userLocation.value = Pair(latitude, longitude)
    }

    fun toggleOfflineMode() {
        _isOfflineMode.value = !_isOfflineMode.value
    }

    fun getHospitalsByType(type: HospitalType): List<Hospital> {
        return _hospitals.value.filter { it.type == type }
    }

    fun getNearestHospitals(userLat: Double, userLng: Double, limit: Int = 5): List<Hospital> {
        return _hospitals.value
            .map { hospital ->
                val distance = calculateDistance(userLat, userLng, hospital.latitude, hospital.longitude)
                Pair(hospital, distance)
            }
            .sortedBy { it.second }
            .take(limit)
            .map { it.first }
    }

    private fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val earthRadius = 6371.0 // kilometers

        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLng / 2) * Math.sin(dLng / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return earthRadius * c
    }

    fun searchHospitalsByRegion(region: String): List<Hospital> {
        return _hospitals.value.filter { it.region.contains(region, ignoreCase = true) }
    }
}
