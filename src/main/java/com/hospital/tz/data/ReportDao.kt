package com.hospital.tz.data

import androidx.room.*

@Dao
interface ReportDao {
    // Saved Reports
    @Insert
    suspend fun saveReport(report: SavedReport)

    @Query("SELECT * FROM saved_reports WHERE userId = :userId ORDER BY generatedAt DESC")
    suspend fun getSavedReportsForUser(userId: String): List<SavedReport>

    @Query("SELECT * FROM saved_reports WHERE id = :reportId")
    suspend fun getReportById(reportId: String): SavedReport?

    @Delete
    suspend fun deleteReport(report: SavedReport)

    // Aggregation queries for reports
    @Query("""
        SELECT h.id as hospitalId, h.name as hospitalName,
               COUNT(a.id) as totalAppointments,
               SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END) as completedAppointments,
               SUM(CASE WHEN a.status = 'CANCELLED' THEN 1 ELSE 0 END) as cancelledAppointments,
               AVG(a.durationMinutes) as averageAppointmentDuration
        FROM hospitals h
        LEFT JOIN appointments a ON h.id = a.hospitalId
        WHERE a.appointmentDate >= :startDate AND a.appointmentDate <= :endDate
        GROUP BY h.id, h.name
    """)
    suspend fun getHospitalPerformanceData(startDate: Long, endDate: Long): List<HospitalPerformanceData>

    @Query("""
        SELECT COUNT(DISTINCT pr.patientId) as totalPatients,
               COUNT(DISTINCT CASE WHEN pr.createdAt >= :monthStart THEN pr.patientId END) as newPatientsThisMonth,
               COUNT(DISTINCT CASE WHEN pr.updatedAt >= :monthStart THEN pr.patientId END) as activePatients,
               COUNT(a.id) as appointmentsThisMonth
        FROM patient_records pr
        LEFT JOIN appointments a ON pr.patientId = a.patientId AND a.appointmentDate >= :monthStart
    """)
    suspend fun getPatientStatisticsData(monthStart: Long): PatientStatisticsData

    @Query("""
        SELECT COUNT(*) as totalAppointments,
               SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) as completedAppointments,
               SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END) as cancelledAppointments,
               SUM(CASE WHEN status = 'NO_SHOW' THEN 1 ELSE 0 END) as noShowAppointments,
               SUM(CASE WHEN telemedicineLink IS NOT NULL THEN 1 ELSE 0 END) as telemedicineAppointments,
               AVG(durationMinutes) as averageAppointmentDuration
        FROM appointments
        WHERE appointmentDate >= :startDate AND appointmentDate <= :endDate
    """)
    suspend fun getAppointmentAnalyticsData(startDate: Long, endDate: Long): AppointmentAnalyticsData

    @Query("""
        SELECT COUNT(*) as totalUsers,
               COUNT(CASE WHEN lastLoginAt >= :monthStart THEN 1 END) as activeUsers,
               COUNT(CASE WHEN createdAt >= :monthStart THEN 1 END) as newRegistrations
        FROM users
    """)
    suspend fun getUserActivityData(monthStart: Long): UserActivityData

    // Helper data classes for aggregation results
    data class HospitalPerformanceData(
        val hospitalId: String,
        val hospitalName: String,
        val totalAppointments: Int,
        val completedAppointments: Int,
        val cancelledAppointments: Int,
        val averageAppointmentDuration: Double?
    )

    data class PatientStatisticsData(
        val totalPatients: Int,
        val newPatientsThisMonth: Int,
        val activePatients: Int,
        val appointmentsThisMonth: Int
    )

    data class AppointmentAnalyticsData(
        val totalAppointments: Int,
        val completedAppointments: Int,
        val cancelledAppointments: Int,
        val noShowAppointments: Int,
        val telemedicineAppointments: Int,
        val averageAppointmentDuration: Double?
    )

    data class UserActivityData(
        val totalUsers: Int,
        val activeUsers: Int,
        val newRegistrations: Int
    )
}
