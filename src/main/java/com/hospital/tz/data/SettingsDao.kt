package com.hospital.tz.data

import androidx.room.*

@Dao
interface SettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: Settings)

    @Query("SELECT * FROM settings WHERE userId = :userId")
    suspend fun getSettingsForUser(userId: String): Settings?

    @Query("UPDATE settings SET updatedAt = :timestamp WHERE userId = :userId")
    suspend fun updateLastModified(userId: String, timestamp: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteSettings(settings: Settings)

    // Emergency Contacts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmergencyContact(contact: EmergencyContact)

    @Query("SELECT * FROM emergency_contacts WHERE userId = :userId ORDER BY isPrimary DESC, name ASC")
    suspend fun getEmergencyContactsForUser(userId: String): List<EmergencyContact>

    @Query("SELECT * FROM emergency_contacts WHERE id = :contactId")
    suspend fun getEmergencyContactById(contactId: String): EmergencyContact?

    @Update
    suspend fun updateEmergencyContact(contact: EmergencyContact)

    @Delete
    suspend fun deleteEmergencyContact(contact: EmergencyContact)

    // Emergency Alerts
    @Insert
    suspend fun insertEmergencyAlert(alert: EmergencyAlert)

    @Query("SELECT * FROM emergency_alerts WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getEmergencyAlertsForUser(userId: String): List<EmergencyAlert>

    @Query("SELECT * FROM emergency_alerts WHERE status = 'ACTIVE' ORDER BY timestamp DESC")
    suspend fun getActiveEmergencyAlerts(): List<EmergencyAlert>

    @Query("UPDATE emergency_alerts SET status = :status, responseTime = :responseTime WHERE id = :alertId")
    suspend fun updateAlertStatus(alertId: String, status: String, responseTime: Long? = null)

    // Emergency Protocols
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmergencyProtocol(protocol: EmergencyProtocol)

    @Query("SELECT * FROM emergency_protocols WHERE isActive = 1 ORDER BY title ASC")
    suspend fun getActiveEmergencyProtocols(): List<EmergencyProtocol>

    @Query("SELECT * FROM emergency_protocols WHERE category = :category AND isActive = 1")
    suspend fun getEmergencyProtocolsByCategory(category: String): List<EmergencyProtocol>

    @Update
    suspend fun updateEmergencyProtocol(protocol: EmergencyProtocol)

    @Delete
    suspend fun deleteEmergencyProtocol(protocol: EmergencyProtocol)
}
