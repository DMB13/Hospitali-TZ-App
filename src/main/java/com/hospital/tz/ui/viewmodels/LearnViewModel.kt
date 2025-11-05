package com.hospital.tz.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hospital.tz.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class LearnViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val videoDao = database.videoContentDao()
    private val commentDao = database.videoCommentDao()
    private val followDao = database.userFollowDao()
    private val likeDao = database.videoLikeDao()
    private val saveDao = database.videoSaveDao()

    private val _videos = MutableStateFlow<List<VideoContent>>(emptyList())
    val videos: StateFlow<List<VideoContent>> = _videos

    private val _selectedVideo = MutableStateFlow<VideoContent?>(null)
    val selectedVideo: StateFlow<VideoContent?> = _selectedVideo

    private val _comments = MutableStateFlow<List<VideoComment>>(emptyList())
    val comments: StateFlow<List<VideoComment>> = _comments

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId

    init {
        loadVideos()
        loadSampleVideos()
    }

    private fun loadVideos() {
        viewModelScope.launch {
            _isLoading.value = true
            _videos.value = videoDao.getAllVideos()
            _isLoading.value = false
        }
    }

    private fun loadSampleVideos() {
        viewModelScope.launch {
            // Add sample videos for demonstration
            val sampleVideos = listOf(
                VideoContent(
                    id = "vid1",
                    title = "Understanding Malaria Prevention",
                    description = "Dr. Sarah Johnson explains the importance of malaria prevention in Tanzania and provides practical tips for families.",
                    videoUrl = "https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_1mb.mp4",
                    thumbnailUrl = "https://via.placeholder.com/320x180/4CAF50/FFFFFF?text=Malaria+Prevention",
                    duration = 180, // 3 minutes
                    authorId = "staff1",
                    authorName = "Dr. Sarah Johnson",
                    authorType = UserType.STAFF,
                    uploadDate = System.currentTimeMillis() - 86400000, // 1 day ago
                    views = 1250,
                    likes = 89,
                    comments = 23,
                    shares = 12,
                    tags = "malaria,prevention,health,tanzania",
                    isApproved = true
                ),
                VideoContent(
                    id = "vid2",
                    title = "Nutrition for Pregnant Women",
                    description = "Learn about essential nutrients and dietary guidelines for pregnant women in rural Tanzania.",
                    videoUrl = "https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_2mb.mp4",
                    thumbnailUrl = "https://via.placeholder.com/320x180/E91E63/FFFFFF?text=Pregnancy+Nutrition",
                    duration = 240, // 4 minutes
                    authorId = "staff2",
                    authorName = "Nutritionist Maria Kimaro",
                    authorType = UserType.STAFF,
                    uploadDate = System.currentTimeMillis() - 172800000, // 2 days ago
                    views = 890,
                    likes = 67,
                    comments = 18,
                    shares = 8,
                    tags = "pregnancy,nutrition,health,women",
                    isApproved = true
                ),
                VideoContent(
                    id = "vid3",
                    title = "Basic First Aid Techniques",
                    description = "Essential first aid skills everyone should know. Presented by experienced medical professionals.",
                    videoUrl = "https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_5mb.mp4",
                    thumbnailUrl = "https://via.placeholder.com/320x180/2196F3/FFFFFF?text=First+Aid",
                    duration = 300, // 5 minutes
                    authorId = "staff3",
                    authorName = "Emergency Response Team",
                    authorType = UserType.STAFF,
                    uploadDate = System.currentTimeMillis() - 259200000, // 3 days ago
                    views = 2100,
                    likes = 145,
                    comments = 34,
                    shares = 25,
                    tags = "first aid,emergency,health,safety",
                    isApproved = true
                )
            )

            sampleVideos.forEach { video ->
                val existing = videoDao.getVideoById(video.id)
                if (existing == null) {
                    videoDao.insert(video)
                }
            }

            // Add sample comments
            val sampleComments = listOf(
                VideoComment(
                    id = "comment1",
                    videoId = "vid1",
                    userId = "user1",
                    userName = "John Doe",
                    comment = "Very informative! Thank you for sharing this knowledge.",
                    timestamp = System.currentTimeMillis() - 3600000,
                    likes = 5
                ),
                VideoComment(
                    id = "comment2",
                    videoId = "vid1",
                    userId = "user2",
                    userName = "Mary Smith",
                    comment = "This will help many families in our community.",
                    timestamp = System.currentTimeMillis() - 7200000,
                    likes = 3
                )
            )

            sampleComments.forEach { comment ->
                // Check if comment exists (simple check by videoId and userId)
                // In real implementation, you'd have a proper unique constraint
                commentDao.insert(comment)
            }

            loadVideos() // Refresh the list
        }
    }

    fun selectVideo(video: VideoContent) {
        _selectedVideo.value = video
        loadCommentsForVideo(video.id)
        // Increment view count
        viewModelScope.launch {
            videoDao.incrementViews(video.id)
            loadVideos() // Refresh to show updated view count
        }
    }

    private fun loadCommentsForVideo(videoId: String) {
        viewModelScope.launch {
            _comments.value = commentDao.getCommentsForVideo(videoId)
        }
    }

    fun addComment(videoId: String, userId: String, userName: String, comment: String) {
        viewModelScope.launch {
            val newComment = VideoComment(
                id = UUID.randomUUID().toString(),
                videoId = videoId,
                userId = userId,
                userName = userName,
                comment = comment,
                timestamp = System.currentTimeMillis()
            )
            commentDao.insert(newComment)
            videoDao.incrementComments(videoId)
            loadCommentsForVideo(videoId)
            loadVideos() // Refresh video comment count
        }
    }

    fun toggleLike(videoId: String, userId: String) {
        viewModelScope.launch {
            val isLiked = likeDao.isLikedByUser(videoId, userId) > 0
            if (isLiked) {
                likeDao.removeLike(videoId, userId)
                // Note: In a real implementation, you'd decrement the like count in video table
            } else {
                val like = VideoLike(
                    id = UUID.randomUUID().toString(),
                    videoId = videoId,
                    userId = userId,
                    timestamp = System.currentTimeMillis()
                )
                likeDao.insert(like)
                videoDao.incrementLikes(videoId)
            }
            loadVideos() // Refresh to show updated like count
        }
    }

    fun toggleSave(videoId: String, userId: String) {
        viewModelScope.launch {
            val isSaved = saveDao.isSavedByUser(videoId, userId) > 0
            if (isSaved) {
                saveDao.removeSave(videoId, userId)
            } else {
                val save = VideoSave(
                    id = UUID.randomUUID().toString(),
                    videoId = videoId,
                    userId = userId,
                    saveDate = System.currentTimeMillis()
                )
                saveDao.insert(save)
            }
        }
    }

    fun followUser(followerId: String, followedId: String) {
        viewModelScope.launch {
            val isFollowing = followDao.isFollowing(followerId, followedId) > 0
            if (!isFollowing) {
                val follow = UserFollow(
                    id = UUID.randomUUID().toString(),
                    followerId = followerId,
                    followedId = followedId,
                    followDate = System.currentTimeMillis()
                )
                followDao.insert(follow)
            }
        }
    }

    fun searchVideos(query: String) {
        viewModelScope.launch {
            _videos.value = if (query.isBlank()) {
                videoDao.getAllVideos()
            } else {
                videoDao.searchVideosByTag(query)
            }
        }
    }

    fun setCurrentUser(userId: String) {
        _currentUserId.value = userId
    }

    fun clearSelection() {
        _selectedVideo.value = null
        _comments.value = emptyList()
    }
}
