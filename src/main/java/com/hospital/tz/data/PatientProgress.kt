package com.hospital.tz.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patient_progress")
data class PatientProgress(
    @PrimaryKey val patientId: String,
    val patientName: String,
    val diagnosis: String,
    val status: String, // Admitted, In Treatment, Discharged, etc.
    val hospitalName: String,
    val doctorName: String? = null,
    val roomNumber: String? = null,
    val admissionDate: Long,
    val lastUpdate: Long,
    val nextAppointment: Long? = null,
    val notes: String? = null
)
