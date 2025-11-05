package com.labtrace.tz.data

import androidx.room.*

@Dao
interface FeedbackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(feedback: Feedback)

    @Query("SELECT * FROM feedback")
    suspend fun getAll(): List<Feedback>
}
