package com.hospital.tz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hospital.tz.data.VideoContent
import com.hospital.tz.ui.viewmodels.LearnViewModel

@Composable
fun LearnScreen(userId: String) {
    val viewModel: LearnViewModel = viewModel()
    val videos by viewModel.videos.collectAsState()
    val selectedVideo by viewModel.selectedVideo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    // Set current user
    LaunchedEffect(userId) {
        viewModel.setCurrentUser(userId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        LearnHeader(
            searchQuery = searchQuery,
            onSearchChange = {
                searchQuery = it
                viewModel.searchVideos(it)
            }
        )

        // Content
        if (selectedVideo != null) {
            VideoDetailView(
                video = selectedVideo!!,
                onBackClick = { viewModel.clearSelection() },
                onLikeClick = { viewModel.toggleLike(selectedVideo!!.id, userId) }
            )
        } else {
            VideoListView(videos = videos, isLoading = isLoading, onVideoClick = { viewModel.selectVideo(it) })
        }
    }
}

@Composable
fun LearnHeader(searchQuery: String, onSearchChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Learn",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2)
            )

            IconButton(onClick = { /* TODO: Upload video */ }) {
                Icon(Icons.Default.Add, contentDescription = "Upload Video")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            label = { Text("Search videos by topic") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

@Composable
fun VideoListView(videos: List<VideoContent>, isLoading: Boolean, onVideoClick: (VideoContent) -> Unit) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Educational Videos",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
            }

            items(videos) { video ->
                VideoCard(video = video, onClick = { onVideoClick(video) })
            }

            if (videos.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No videos available yet.\nStaff users can upload educational content.",
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VideoCard(video: VideoContent, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Thumbnail placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Play Video",
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF1976D2)
                    )
                    Text(
                        text = "Video Thumbnail",
                        style = MaterialTheme.typography.caption,
                        color = Color.Gray
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = video.description,
                    style = MaterialTheme.typography.body2,
                    maxLines = 3,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${video.authorName} • ${formatDuration(video.duration)}",
                        style = MaterialTheme.typography.caption,
                        color = Color.Gray
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Text(
                            text = "${video.views}",
                            style = MaterialTheme.typography.caption,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.ThumbUp,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFF4CAF50)
                            )
                            Text(
                                text = "${video.likes}",
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Comment,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFF2196F3)
                            )
                            Text(
                                text = "${video.comments}",
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VideoDetailView(video: VideoContent, onBackClick: () -> Unit, onLikeClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Video player placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Play Video",
                    modifier = Modifier.size(64.dp),
                    tint = Color.White
                )
                Text(
                    text = "Video Player (ExoPlayer)",
                    color = Color.White,
                    style = MaterialTheme.typography.body2
                )
            }
        }

        // Video info and actions
        Column(modifier = Modifier.fillMaxSize()) {
            // Back button and title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${video.authorName} • ${formatTimestamp(video.uploadDate)}",
                        style = MaterialTheme.typography.caption,
                        color = Color.Gray
                    )
                }
            }

            // Action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                VideoActionButton(
                    icon = Icons.Default.ThumbUp,
                    text = "${video.likes}",
                    onClick = onLikeClick
                )

                VideoActionButton(
                    icon = Icons.Default.Comment,
                    text = "${video.comments}",
                    onClick = { /* TODO: Show comments */ }
                )

                VideoActionButton(
                    icon = Icons.Default.Share,
                    text = "Share",
                    onClick = { /* TODO: Implement share */ }
                )

                VideoActionButton(
                    icon = Icons.Default.Bookmark,
                    text = "Save",
                    onClick = { /* TODO: Implement save */ }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Text(
                text = video.description,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun VideoActionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            icon,
            contentDescription = text,
            modifier = Modifier.size(24.dp),
            tint = Color(0xFF1976D2)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.caption,
            color = Color(0xFF1976D2)
        )
    }
}

private fun formatDuration(seconds: Long): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%d:%02d", minutes, remainingSeconds)
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
}
