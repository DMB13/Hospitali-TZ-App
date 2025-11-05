package com.hospital.tz.data

import androidx.room.*

@Dao
interface PatientProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progress: PatientProgress)

    @Query("SELECT * FROM patient_progress WHERE patientId = :patientId")
    suspend fun getProgressByPatientId(patientId: String): PatientProgress?

    @Query("SELECT * FROM patient_progress ORDER BY lastUpdate DESC")
    suspend fun getAllProgress(): List<PatientProgress>

    @Query("SELECT * FROM patient_progress WHERE hospitalName = :hospitalName ORDER BY lastUpdate DESC")
    suspend fun getProgressByHospital(hospitalName: String): List<PatientProgress>

    @Update
    suspend fun update(progress: PatientProgress)

    @Delete
    suspend fun delete(progress: PatientProgress)
}
