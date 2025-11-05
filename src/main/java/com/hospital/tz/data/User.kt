package com.hospital.tz.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val email: String,
    val name: String,
    val userType: UserType,
    val isVerified: Boolean = false,
    val nationalId: String? = null,
    val professionLicense: String? = null,
    val profilePicture: String? = null,
    val description: String? = null,
    val permanentResident: Boolean? = null,
    val locationAccessGranted: Boolean? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis()
)

enum class UserType {
    NORMAL, STAFF
}
