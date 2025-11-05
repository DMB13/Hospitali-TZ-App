package com.labtrace.tz.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sample_records")
data class SampleRecord(
    @PrimaryKey val sampleId: String,
    val patientInitials: String,
    val sampleType: String,
    val destinationLab: String,
    val status: String,
    val lastUpdated: Long
)
