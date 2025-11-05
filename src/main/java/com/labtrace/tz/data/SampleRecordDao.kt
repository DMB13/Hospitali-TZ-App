package com.labtrace.tz.data

import androidx.room.*

@Dao
interface SampleRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: SampleRecord)

    @Query("SELECT * FROM sample_records WHERE sampleId = :id")
    suspend fun getById(id: String): SampleRecord?

    @Query("SELECT * FROM sample_records")
    suspend fun getAll(): List<SampleRecord>

    @Update
    suspend fun update(record: SampleRecord)

    @Delete
    suspend fun delete(record: SampleRecord)
}
