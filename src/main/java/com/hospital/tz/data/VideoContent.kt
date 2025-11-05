package com.hospital.tz.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_content")
data class VideoContent(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val videoUrl: String,
    val thumbnailUrl: String? = null,
    val duration: Long, // in seconds
    val authorId: String,
    val authorName: String,
    val authorType: UserType,
    val uploadDate: Long,
    val views: Int = 0,
    val likes: Int = 0,
    val comments: Int = 0,
    val shares: Int = 0,
    val tags: String, // comma-separated tags
    val isApproved: Boolean = false, // AI moderation result
    val moderationReason: String? = null,
    val isActive: Boolean = true
)

@Entity(tableName = "video_comments")
data class VideoComment(
    @PrimaryKey val id: String,
    val videoId: String,
    val userId: String,
    val userName: String,
    val comment: String,
    val timestamp: Long,
    val likes: Int = 0,
    val isActive: Boolean = true
)

@Entity(tableName = "user_follows")
data class UserFollow(
    @PrimaryKey val id: String,
    val followerId: String,
    val followedId: String,
    val followDate: Long,
    val isActive: Boolean = true
)

@Entity(tableName = "video_likes")
data class VideoLike(
    @PrimaryKey val id: String,
    val videoId: String,
    val userId: String,
    val timestamp: Long
)

@Entity(tableName = "video_saves")
data class VideoSave(
    @PrimaryKey val id: String,
    val videoId: String,
    val userId: String,
    val saveDate: Long
)
