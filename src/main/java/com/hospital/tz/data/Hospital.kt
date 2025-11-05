package com.hospital.tz.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hospitals")
data class Hospital(
    @PrimaryKey val id: String,
    val name: String,
    val type: HospitalType, // HOSPITAL, DISPENSARY, PHARMACY
    val region: String,
    val district: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val phone: String? = null,
    val email: String? = null,
    val services: String, // comma-separated services
    val isActive: Boolean = true
)

enum class HospitalType {
    HOSPITAL, DISPENSARY, PHARMACY
}
