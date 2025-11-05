package com.hospital.tz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hospital.tz.data.Conversation
import com.hospital.tz.data.Message
import com.hospital.tz.data.MessageType
import com.hospital.tz.data.User
import com.hospital.tz.ui.viewmodels.MessagesViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MessagesScreen(userId: String) {
    val viewModel: MessagesViewModel = viewModel()
    val conversations by viewModel.conversations.collectAsState()
    val selectedConversation by viewModel.selectedConversation.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val followedUsers by viewModel.followedUsers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showNewMessageDialog by remember { mutableStateOf(false) }
    var messageText by remember { mutableStateOf("") }

    // Set current user
    LaunchedEffect(userId) {
        viewModel.setCurrentUser(userId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        MessagesHeader(
            onNewMessageClick = { showNewMessageDialog = true },
            unreadCount = viewModel.getTotalUnreadCount()
        )

        // Content
        if (selectedConversation != null) {
            // Chat view
            ChatView(
                conversation = selectedConversation!!,
                messages = messages,
                messageText = messageText,
                onMessageTextChange = { messageText = it },
                onSendMessage = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendMessage(selectedConversation!!.id, messageText.trim())
                        messageText = ""
                    }
                },
                onBackClick = { viewModel.clearSelection() }
            )
        } else {
            // Conversations list
            ConversationsListView(
                conversations = conversations,
                isLoading = isLoading,
                onConversationClick = { viewModel.selectConversation(it) }
            )
        }
    }

    // New message dialog
    if (showNewMessageDialog) {
        NewMessageDialog(
            followedUsers = followedUsers,
            onUserSelected = { user ->
                viewModel.startNewConversation(user.id)
                showNewMessageDialog = false
            },
            onDismiss = { showNewMessageDialog = false }
        )
    }
}

@Composable
fun MessagesHeader(onNewMessageClick: () -> Unit, unreadCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Messages",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1976D2)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (unreadCount > 0) {
                Badge(
                    backgroundColor = Color.Red,
                    contentColor = Color.White,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(text = unreadCount.toString())
                }
            }

            IconButton(onClick = onNewMessageClick) {
                Icon(Icons.Default.Add, contentDescription = "New Message")
            }
        }
    }
}

@Composable
fun ConversationsListView(
    conversations: List<Conversation>,
    isLoading: Boolean,
    onConversationClick: (Conversation) -> Unit
) {
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "Recent Conversations",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(conversations) { conversation ->
                ConversationItem(
                    conversation = conversation,
                    onClick = { onConversationClick(conversation) }
                )
            }

            if (conversations.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Chat,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No conversations yet.\nStart a conversation with someone you follow.",
                                textAlign = TextAlign.Center,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationItem(conversation: Conversation, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = 2.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = conversation.participant2Name.firstOrNull()?.toString() ?: "?",
                    style = MaterialTheme.typography.h6,
                    color = Color(0xFF1976D2)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = conversation.participant2Name,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = conversation.lastMessage.take(50) + if (conversation.lastMessage.length > 50) "..." else "",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray,
                    maxLines = 1
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatTimestamp(conversation.lastMessageTime),
                    style = MaterialTheme.typography.caption,
                    color = Color.Gray
                )

                if (conversation.unreadCount > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Badge(
                        backgroundColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    ) {
                        Text(text = conversation.unreadCount.toString())
                    }
                }
            }
        }
    }
}

@Composable
fun ChatView(
    conversation: Conversation,
    messages: List<Message>,
    messageText: String,
    onMessageTextChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Chat header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = conversation.participant2Name.firstOrNull()?.toString() ?: "?",
                    style = MaterialTheme.typography.body1,
                    color = Color(0xFF1976D2)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = conversation.participant2Name,
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold
            )
        }

        // Messages list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                MessageItem(message = message)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Message input
        MessageInput(
            text = messageText,
            onTextChange = onMessageTextChange,
            onSendClick = onSendMessage
        )
    }
}

@Composable
fun MessageItem(message: Message) {
    val isCurrentUser = message.senderId == "current_user_id" // This should be passed from ViewModel

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(
                topStart = if (isCurrentUser) 16.dp else 4.dp,
                topEnd = if (isCurrentUser) 4.dp else 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            backgroundColor = if (isCurrentUser) Color(0xFF1976D2) else Color(0xFFF5F5F5),
            elevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.content,
                    color = if (isCurrentUser) Color.White else Color.Black,
                    style = MaterialTheme.typography.body1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = formatTimestamp(message.timestamp),
                        style = MaterialTheme.typography.caption,
                        color = if (isCurrentUser) Color.White.copy(alpha = 0.7f) else Color.Gray
                    )

                    if (isCurrentUser) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            if (message.isRead) Icons.Default.DoneAll else Icons.Default.Done,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageInput(
    text: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Attachment button
        IconButton(onClick = { /* TODO: Implement attachment */ }) {
            Icon(Icons.Default.AttachFile, contentDescription = "Attach")
        }

        // Text field
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Type a message...") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { onSendClick() }),
            maxLines = 4,
            shape = RoundedCornerShape(24.dp)
        )

        // Send button
        IconButton(
            onClick = onSendClick,
            enabled = text.isNotBlank()
        ) {
            Icon(
                Icons.Default.Send,
                contentDescription = "Send",
                tint = if (text.isNotBlank()) Color(0xFF1976D2) else Color.Gray
            )
        }
    }
}

@Composable
fun NewMessageDialog(
    followedUsers: List<User>,
    onUserSelected: (User) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Start New Conversation") },
        text = {
            LazyColumn {
                items(followedUsers) { user ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onUserSelected(user) }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE3F2FD)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.name.firstOrNull()?.toString() ?: "?",
                                style = MaterialTheme.typography.h6,
                                color = Color(0xFF1976D2)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = user.name,
                                style = MaterialTheme.typography.subtitle1,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = user.userType,
                                style = MaterialTheme.typography.caption,
                                color = Color.Gray
                            )
                        }
                    }
                }

                if (followedUsers.isEmpty()) {
                    item {
                        Text(
                            text = "You need to follow users to start conversations.",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
