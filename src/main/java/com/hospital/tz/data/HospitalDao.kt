package com.hospital.tz.data

import androidx.room.*

@Dao
interface HospitalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(hospital: Hospital)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(hospitals: List<Hospital>)

    @Query("SELECT * FROM hospitals WHERE isActive = 1 ORDER BY name ASC")
    suspend fun getAllActiveHospitals(): List<Hospital>

    @Query("SELECT * FROM hospitals WHERE name LIKE '%' || :query || '%' AND isActive = 1 ORDER BY name ASC")
    suspend fun searchHospitals(query: String): List<Hospital>

    @Query("SELECT * FROM hospitals WHERE region = :region AND isActive = 1 ORDER BY name ASC")
    suspend fun getHospitalsByRegion(region: String): List<Hospital>

    @Query("SELECT * FROM hospitals WHERE type = :type AND isActive = 1 ORDER BY name ASC")
    suspend fun getHospitalsByType(type: String): List<Hospital>

    @Query("SELECT * FROM hospitals WHERE id = :id")
    suspend fun getHospitalById(id: String): Hospital?

    @Update
    suspend fun update(hospital: Hospital)

    @Delete
    suspend fun delete(hospital: Hospital)
}
