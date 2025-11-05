package com.hospital.tz.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "patient_records")
data class PatientRecord(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val patientId: String,
    val patientName: String,
    val dateOfBirth: Long,
    val gender: String,
    val bloodType: String?,
    val allergies: String,
    val emergencyContact: String,
    val emergencyPhone: String,
    val medicalConditions: String,
    val currentMedications: String,
    val insuranceProvider: String?,
    val insuranceNumber: String?,
    val primaryDoctorId: String?,
    val primaryDoctorName: String?,
    val lastVisitDate: Long?,
    val nextAppointmentDate: Long?,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "medical_history")
data class MedicalHistory(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val patientId: String,
    val recordDate: Long,
    val condition: String,
    val diagnosis: String,
    val treatment: String,
    val doctorId: String,
    val doctorName: String,
    val notes: String,
    val attachments: String, // JSON array of attachment URLs
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "prescriptions")
data class Prescription(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val patientId: String,
    val doctorId: String,
    val doctorName: String,
    val medicationName: String,
    val dosage: String,
    val frequency: String,
    val duration: String,
    val instructions: String,
    val prescribedDate: Long,
    val startDate: Long,
    val endDate: Long?,
    val refillsRemaining: Int,
    val pharmacyId: String?,
    val pharmacyName: String?,
    val status: PrescriptionStatus = PrescriptionStatus.ACTIVE,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

enum class PrescriptionStatus {
    ACTIVE,
    COMPLETED,
    CANCELLED,
    EXPIRED
}
