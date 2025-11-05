package com.hospital.tz.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "announcements")
data class Announcement(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val hospitalName: String,
    val timestamp: Long,
    val isRead: Boolean = false
)
