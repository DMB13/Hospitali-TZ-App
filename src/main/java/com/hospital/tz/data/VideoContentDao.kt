package com.hospital.tz.data

import androidx.room.*

@Dao
interface VideoContentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(video: VideoContent)

    @Query("SELECT * FROM video_content WHERE isActive = 1 ORDER BY uploadDate DESC")
    suspend fun getAllVideos(): List<VideoContent>

    @Query("SELECT * FROM video_content WHERE authorId = :authorId AND isActive = 1 ORDER BY uploadDate DESC")
    suspend fun getVideosByAuthor(authorId: String): List<VideoContent>

    @Query("SELECT * FROM video_content WHERE id = :videoId")
    suspend fun getVideoById(videoId: String): VideoContent?

    @Query("SELECT * FROM video_content WHERE tags LIKE '%' || :tag || '%' AND isActive = 1 ORDER BY uploadDate DESC")
    suspend fun searchVideosByTag(tag: String): List<VideoContent>

    @Query("UPDATE video_content SET views = views + 1 WHERE id = :videoId")
    suspend fun incrementViews(videoId: String)

    @Query("UPDATE video_content SET likes = likes + 1 WHERE id = :videoId")
    suspend fun incrementLikes(videoId: String)

    @Query("UPDATE video_content SET comments = comments + 1 WHERE id = :videoId")
    suspend fun incrementComments(videoId: String)

    @Update
    suspend fun update(video: VideoContent)

    @Delete
    suspend fun delete(video: VideoContent)
}

@Dao
interface VideoCommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comment: VideoComment)

    @Query("SELECT * FROM video_comments WHERE videoId = :videoId AND isActive = 1 ORDER BY timestamp DESC")
    suspend fun getCommentsForVideo(videoId: String): List<VideoComment>

    @Update
    suspend fun update(comment: VideoComment)

    @Delete
    suspend fun delete(comment: VideoComment)
}

@Dao
interface UserFollowDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(follow: UserFollow)

    @Query("SELECT * FROM user_follows WHERE followerId = :userId AND isActive = 1")
    suspend fun getFollowing(userId: String): List<UserFollow>

    @Query("SELECT * FROM user_follows WHERE followedId = :userId AND isActive = 1")
    suspend fun getFollowers(userId: String): List<UserFollow>

    @Query("SELECT COUNT(*) FROM user_follows WHERE followerId = :followerId AND followedId = :followedId AND isActive = 1")
    suspend fun isFollowing(followerId: String, followedId: String): Int

    @Query("UPDATE user_follows SET isActive = 0 WHERE followerId = :followerId AND followedId = :followedId")
    suspend fun unfollow(followerId: String, followedId: String)

    @Delete
    suspend fun delete(follow: UserFollow)
}

@Dao
interface VideoLikeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(like: VideoLike)

    @Query("SELECT COUNT(*) FROM video_likes WHERE videoId = :videoId AND userId = :userId")
    suspend fun isLikedByUser(videoId: String, userId: String): Int

    @Delete
    suspend fun delete(like: VideoLike)

    @Query("DELETE FROM video_likes WHERE videoId = :videoId AND userId = :userId")
    suspend fun removeLike(videoId: String, userId: String)
}

@Dao
interface VideoSaveDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(save: VideoSave)

    @Query("SELECT * FROM video_saves WHERE userId = :userId ORDER BY saveDate DESC")
    suspend fun getSavedVideos(userId: String): List<VideoSave>

    @Query("SELECT COUNT(*) FROM video_saves WHERE videoId = :videoId AND userId = :userId")
    suspend fun isSavedByUser(videoId: String, userId: String): Int

    @Query("DELETE FROM video_saves WHERE videoId = :videoId AND userId = :userId")
    suspend fun removeSave(videoId: String, userId: String)

    @Delete
    suspend fun delete(save: VideoSave)
}
