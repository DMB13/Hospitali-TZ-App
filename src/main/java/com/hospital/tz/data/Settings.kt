package com.hospital.tz.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey val userId: String,
    val notificationsEnabled: Boolean = true,
    val appointmentReminders: Boolean = true,
    val messageNotifications: Boolean = true,
    val announcementNotifications: Boolean = true,
    val theme: String = "SYSTEM", // SYSTEM, LIGHT, DARK
    val language: String = "en", // en, sw (Swahili)
    val privacyLevel: String = "STANDARD", // PUBLIC, STANDARD, PRIVATE
    val dataSharing: Boolean = false,
    val biometricEnabled: Boolean = false,
    val autoBackup: Boolean = false,
    val backupFrequency: String = "WEEKLY", // DAILY, WEEKLY, MONTHLY
    val lastBackupDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "emergency_contacts")
data class EmergencyContact(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val relationship: String,
    val phoneNumber: String,
    val email: String? = null,
    val isPrimary: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "emergency_alerts")
data class EmergencyAlert(
    @PrimaryKey val id: String,
    val userId: String,
    val alertType: String, // SOS, MEDICAL_EMERGENCY, FIRE, etc.
    val location: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val message: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "ACTIVE", // ACTIVE, RESOLVED, FALSE_ALARM
    val respondersNotified: Int = 0,
    val responseTime: Long? = null
)

@Entity(tableName = "emergency_protocols")
data class EmergencyProtocol(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val steps: String, // JSON array of steps
    val category: String, // MEDICAL, FIRE, NATURAL_DISASTER, etc.
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
