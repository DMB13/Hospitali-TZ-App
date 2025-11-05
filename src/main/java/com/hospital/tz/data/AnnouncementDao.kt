package com.hospital.tz.data

import androidx.room.*

@Dao
interface AnnouncementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(announcement: Announcement)

    @Query("SELECT * FROM announcements ORDER BY timestamp DESC")
    suspend fun getAllAnnouncements(): List<Announcement>

    @Query("SELECT * FROM announcements WHERE isRead = 0 ORDER BY timestamp DESC")
    suspend fun getUnreadAnnouncements(): List<Announcement>

    @Query("UPDATE announcements SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Delete
    suspend fun delete(announcement: Announcement)
}
