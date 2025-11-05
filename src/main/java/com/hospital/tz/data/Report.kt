package com.hospital.tz.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Data classes for report data (not stored in DB, used for aggregation)
data class HospitalPerformanceReport(
    val hospitalId: String,
    val hospitalName: String,
    val totalAppointments: Int,
    val completedAppointments: Int,
    val cancelledAppointments: Int,
    val averageWaitTime: Long, // in minutes
    val patientSatisfaction: Double, // 1-5 scale
    val revenue: Double,
    val period: String // e.g., "2024-01"
)

data class PatientStatisticsReport(
    val totalPatients: Int,
    val newPatientsThisMonth: Int,
    val activePatients: Int,
    val appointmentsThisMonth: Int,
    val averageAge: Double,
    val genderDistribution: Map<String, Int>, // MALE -> count, FEMALE -> count
    val topConditions: List<Pair<String, Int>>, // condition -> count
    val period: String
)

data class AppointmentAnalyticsReport(
    val totalAppointments: Int,
    val completedAppointments: Int,
    val cancelledAppointments: Int,
    val noShowAppointments: Int,
    val telemedicineAppointments: Int,
    val averageAppointmentDuration: Long,
    val peakHours: Map<String, Int>, // hour -> count
    val appointmentTypes: Map<String, Int>, // type -> count
    val period: String
)

data class UserActivityReport(
    val totalUsers: Int,
    val activeUsers: Int,
    val newRegistrations: Int,
    val loginFrequency: Map<String, Int>, // daily, weekly, monthly -> count
    val featureUsage: Map<String, Int>, // feature -> usage count
    val deviceTypes: Map<String, Int>, // android, ios -> count
    val period: String
)

// Stored reports for generated/saved reports
@Entity(tableName = "saved_reports")
data class SavedReport(
    @PrimaryKey val id: String,
    val userId: String,
    val reportType: String, // HOSPITAL_PERFORMANCE, PATIENT_STATS, APPOINTMENT_ANALYTICS, USER_ACTIVITY
    val reportData: String, // JSON string of report data
    val generatedAt: Long = System.currentTimeMillis(),
    val period: String,
    val title: String,
    val description: String? = null
)
