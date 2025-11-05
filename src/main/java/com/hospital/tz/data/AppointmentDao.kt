package com.hospital.tz.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {

    // Appointment CRUD operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: Appointment): Long

    @Update
    suspend fun updateAppointment(appointment: Appointment)

    @Delete
    suspend fun deleteAppointment(appointment: Appointment)

    @Query("SELECT * FROM appointments WHERE id = :appointmentId")
    suspend fun getAppointmentById(appointmentId: Long): Appointment?

    @Query("SELECT * FROM appointments WHERE patientId = :patientId ORDER BY appointmentDate DESC")
    fun getAppointmentsForPatient(patientId: String): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE doctorId = :doctorId ORDER BY appointmentDate DESC")
    fun getAppointmentsForDoctor(doctorId: String): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE hospitalId = :hospitalId ORDER BY appointmentDate DESC")
    fun getAppointmentsForHospital(hospitalId: Long): Flow<List<Appointment>>

    @Query("""
        SELECT * FROM appointments
        WHERE appointmentDate >= :startDate AND appointmentDate <= :endDate
        ORDER BY appointmentDate ASC
    """)
    fun getAppointmentsInDateRange(startDate: Long, endDate: Long): Flow<List<Appointment>>

    @Query("""
        SELECT * FROM appointments
        WHERE doctorId = :doctorId AND appointmentDate >= :startDate AND appointmentDate <= :endDate
        ORDER BY appointmentDate ASC
    """)
    fun getDoctorAppointmentsInDateRange(doctorId: String, startDate: Long, endDate: Long): Flow<List<Appointment>>

    @Query("""
        SELECT * FROM appointments
        WHERE patientId = :patientId AND appointmentDate >= :startDate AND appointmentDate <= :endDate
        ORDER BY appointmentDate ASC
    """)
    fun getPatientAppointmentsInDateRange(patientId: String, startDate: Long, endDate: Long): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE status = :status ORDER BY appointmentDate DESC")
    fun getAppointmentsByStatus(status: AppointmentStatus): Flow<List<Appointment>>

    @Query("UPDATE appointments SET status = :status, updatedAt = :updatedAt WHERE id = :appointmentId")
    suspend fun updateAppointmentStatus(appointmentId: Long, status: AppointmentStatus, updatedAt: Long = System.currentTimeMillis())

    // Doctor Availability operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoctorAvailability(availability: DoctorAvailability): Long

    @Update
    suspend fun updateDoctorAvailability(availability: DoctorAvailability)

    @Delete
    suspend fun deleteDoctorAvailability(availability: DoctorAvailability)

    @Query("SELECT * FROM doctor_availability WHERE doctorId = :doctorId ORDER BY dayOfWeek, startTime")
    fun getDoctorAvailability(doctorId: String): Flow<List<DoctorAvailability>>

    @Query("SELECT * FROM doctor_availability WHERE doctorId = :doctorId AND dayOfWeek = :dayOfWeek")
    suspend fun getDoctorAvailabilityForDay(doctorId: String, dayOfWeek: Int): List<DoctorAvailability>

    @Query("SELECT * FROM doctor_availability WHERE hospitalId = :hospitalId")
    fun getHospitalAvailability(hospitalId: Long): Flow<List<DoctorAvailability>>

    // Check if doctor is available at specific time
    @Query("""
        SELECT COUNT(*) FROM appointments
        WHERE doctorId = :doctorId
        AND appointmentDate = :date
        AND appointmentTime = :time
        AND status NOT IN (:excludedStatuses)
    """)
    suspend fun countAppointmentsAtTime(
        doctorId: String,
        date: Long,
        time: String,
        excludedStatuses: List<AppointmentStatus> = listOf(AppointmentStatus.CANCELLED, AppointmentStatus.NO_SHOW)
    ): Int

    // Get available time slots for a doctor on a specific date
    @Query("""
        SELECT da.* FROM doctor_availability da
        WHERE da.doctorId = :doctorId
        AND da.dayOfWeek = :dayOfWeek
        AND da.isAvailable = 1
    """)
    suspend fun getAvailableSlotsForDoctor(doctorId: String, dayOfWeek: Int): List<DoctorAvailability>

    // Appointment Reminder operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointmentReminder(reminder: AppointmentReminder): Long

    @Update
    suspend fun updateAppointmentReminder(reminder: AppointmentReminder)

    @Query("SELECT * FROM appointment_reminders WHERE appointmentId = :appointmentId")
    fun getRemindersForAppointment(appointmentId: Long): Flow<List<AppointmentReminder>>

    @Query("SELECT * FROM appointment_reminders WHERE sent = 0 AND scheduledTime <= :currentTime")
    suspend fun getPendingReminders(currentTime: Long = System.currentTimeMillis()): List<AppointmentReminder>

    @Query("UPDATE appointment_reminders SET sent = 1, sentAt = :sentAt WHERE id = :reminderId")
    suspend fun markReminderAsSent(reminderId: Long, sentAt: Long = System.currentTimeMillis())

    // Complex queries for appointment management
    @Query("""
        SELECT a.*, h.name as hospitalName, h.location as hospitalLocation
        FROM appointments a
        INNER JOIN hospitals h ON a.hospitalId = h.id
        WHERE a.patientId = :patientId
        ORDER BY a.appointmentDate DESC
    """)
    fun getPatientAppointmentsWithHospital(patientId: String): Flow<List<AppointmentWithHospital>>

    @Query("""
        SELECT a.*, u.name as patientName, h.name as hospitalName
        FROM appointments a
        INNER JOIN users u ON a.patientId = u.id
        INNER JOIN hospitals h ON a.hospitalId = h.id
        WHERE a.doctorId = :doctorId
        ORDER BY a.appointmentDate DESC
    """)
    fun getDoctorAppointmentsWithDetails(doctorId: String): Flow<List<AppointmentWithDetails>>

    // Statistics queries
    @Query("SELECT COUNT(*) FROM appointments WHERE status = :status")
    suspend fun countAppointmentsByStatus(status: AppointmentStatus): Int

    @Query("SELECT COUNT(*) FROM appointments WHERE doctorId = :doctorId AND status = :status")
    suspend fun countDoctorAppointmentsByStatus(doctorId: String, status: AppointmentStatus): Int

    @Query("SELECT COUNT(*) FROM appointments WHERE patientId = :patientId")
    suspend fun countPatientAppointments(patientId: String): Int

    @Query("""
        SELECT COUNT(*) FROM appointments
        WHERE appointmentDate >= :startDate AND appointmentDate <= :endDate
    """)
    suspend fun countAppointmentsInPeriod(startDate: Long, endDate: Long): Int

    // Search appointments
    @Query("""
        SELECT * FROM appointments
        WHERE (patientName LIKE '%' || :query || '%' OR doctorName LIKE '%' || :query || '%')
        ORDER BY appointmentDate DESC
    """)
    fun searchAppointments(query: String): Flow<List<Appointment>>

    // Get upcoming appointments
    @Query("""
        SELECT * FROM appointments
        WHERE appointmentDate >= :currentTime
        AND status IN (:activeStatuses)
        ORDER BY appointmentDate ASC
        LIMIT :limit
    """)
    fun getUpcomingAppointments(
        currentTime: Long = System.currentTimeMillis(),
        activeStatuses: List<AppointmentStatus> = listOf(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED),
        limit: Int = 10
    ): Flow<List<Appointment>>

    // Get appointments requiring attention (overdue, etc.)
    @Query("""
        SELECT * FROM appointments
        WHERE appointmentDate < :currentTime
        AND status IN (:pendingStatuses)
        ORDER BY appointmentDate DESC
    """)
    fun getOverdueAppointments(
        currentTime: Long = System.currentTimeMillis(),
        pendingStatuses: List<AppointmentStatus> = listOf(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED)
    ): Flow<List<Appointment>>
}

// Data classes for complex queries
data class AppointmentWithHospital(
    val id: Long,
    val patientId: String,
    val patientName: String,
    val doctorId: String,
    val doctorName: String,
    val hospitalId: Long,
    val hospitalName: String,
    val hospitalLocation: String,
    val appointmentDate: Long,
    val appointmentTime: String,
    val durationMinutes: Int,
    val appointmentType: AppointmentType,
    val status: AppointmentStatus,
    val symptoms: String?,
    val notes: String?,
    val telemedicineLink: String?,
    val roomNumber: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val reminderSent: Boolean,
    val followUpAppointmentId: Long?
)

data class AppointmentWithDetails(
    val id: Long,
    val patientId: String,
    val patientName: String,
    val doctorId: String,
    val doctorName: String,
    val hospitalId: Long,
    val hospitalName: String,
    val appointmentDate: Long,
    val appointmentTime: String,
    val durationMinutes: Int,
    val appointmentType: AppointmentType,
    val status: AppointmentStatus,
    val symptoms: String?,
    val notes: String?,
    val telemedicineLink: String?,
    val roomNumber: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val reminderSent: Boolean,
    val followUpAppointmentId: Long?
)
