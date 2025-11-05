package com.labtrace.tz.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feedback")
data class Feedback(
    @PrimaryKey val id: String,
    val userType: String,
    val service: String,
    val rating: Int,
    val comment: String,
    val timestamp: Long
)
