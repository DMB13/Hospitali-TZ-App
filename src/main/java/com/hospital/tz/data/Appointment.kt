package com.hospital.tz.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

enum class AppointmentStatus {
    SCHEDULED,
    CONFIRMED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    NO_SHOW
}

enum class AppointmentType {
    IN_PERSON,
    TELEMEDICINE,
    FOLLOW_UP,
    EMERGENCY,
    CONSULTATION
}

@Entity(
    tableName = "appointments",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["doctorId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Hospital::class,
            parentColumns = ["id"],
            childColumns = ["hospitalId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Appointment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientId: String,
    val patientName: String,
    val doctorId: String,
    val doctorName: String,
    val hospitalId: Long,
    val hospitalName: String,
    val appointmentDate: Long, // Timestamp
    val appointmentTime: String, // e.g., "09:00"
    val durationMinutes: Int = 30,
    val appointmentType: AppointmentType = AppointmentType.CONSULTATION,
    val status: AppointmentStatus = AppointmentStatus.SCHEDULED,
    val symptoms: String? = null,
    val notes: String? = null,
    val telemedicineLink: String? = null,
    val roomNumber: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val reminderSent: Boolean = false,
    val followUpAppointmentId: Long? = null
)

@Entity(
    tableName = "doctor_availability",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["doctorId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Hospital::class,
            parentColumns = ["id"],
            childColumns = ["hospitalId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DoctorAvailability(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val doctorId: String,
    val hospitalId: Long,
    val dayOfWeek: Int, // 1 = Sunday, 2 = Monday, etc.
    val startTime: String, // e.g., "09:00"
    val endTime: String, // e.g., "17:00"
    val slotDurationMinutes: Int = 30,
    val isAvailable: Boolean = true,
    val maxAppointmentsPerDay: Int = 8,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "appointment_reminders",
    foreignKeys = [
        ForeignKey(
            entity = Appointment::class,
            parentColumns = ["id"],
            childColumns = ["appointmentId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AppointmentReminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val appointmentId: Long,
    val reminderType: String, // "24h_before", "1h_before", "15min_before"
    val scheduledTime: Long,
    val sent: Boolean = false,
    val sentAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
