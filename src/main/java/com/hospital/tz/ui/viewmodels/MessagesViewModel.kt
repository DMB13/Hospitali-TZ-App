package com.hospital.tz.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hospital.tz.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class MessagesViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val messageDao = database.messageDao()
    private val userDao = database.userDao()
    private val userFollowDao = database.userFollowDao()

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations

    private val _selectedConversation = MutableStateFlow<Conversation?>(null)
    val selectedConversation: StateFlow<Conversation?> = _selectedConversation

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId

    private val _followedUsers = MutableStateFlow<List<User>>(emptyList())
    val followedUsers: StateFlow<List<User>> = _followedUsers

    fun setCurrentUser(userId: String) {
        _currentUserId.value = userId
        loadConversations()
        loadFollowedUsers()
    }

    private fun loadConversations() {
        val userId = _currentUserId.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val convs = messageDao.getConversationsForUser(userId)
                _conversations.value = convs
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadFollowedUsers() {
        val userId = _currentUserId.value ?: return
        viewModelScope.launch {
            try {
                val follows = userFollowDao.getFollowsByFollower(userId)
                val followedIds = follows.map { it.followedId }
                val users = followedIds.mapNotNull { userDao.getUserById(it) }
                _followedUsers.value = users
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun selectConversation(conversation: Conversation) {
        _selectedConversation.value = conversation
        loadMessages(conversation.id)
        markMessagesAsRead(conversation.id)
    }

    private fun loadMessages(conversationId: String) {
        viewModelScope.launch {
            try {
                val msgs = messageDao.getMessagesForConversation(conversationId)
                _messages.value = msgs
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun markMessagesAsRead(conversationId: String) {
        val userId = _currentUserId.value ?: return
        viewModelScope.launch {
            try {
                messageDao.markMessagesAsRead(conversationId, userId)
                // Update conversation unread count
                val unreadCount = messageDao.getUnreadCountForConversation(conversationId, userId)
                messageDao.updateUnreadCount(conversationId, unreadCount)
                // Refresh conversations
                loadConversations()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun sendMessage(conversationId: String, content: String, messageType: MessageType = MessageType.TEXT) {
        val userId = _currentUserId.value ?: return
        val conversation = _selectedConversation.value ?: return

        val receiverId = if (conversation.participant1Id == userId) conversation.participant2Id else conversation.participant1Id
        val receiverName = if (conversation.participant1Id == userId) conversation.participant2Name else conversation.participant1Name
        val senderName = if (conversation.participant1Id == userId) conversation.participant1Name else conversation.participant2Name

        val message = Message(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            senderId = userId,
            senderName = senderName,
            receiverId = receiverId,
            receiverName = receiverName,
            content = content,
            messageType = messageType,
            timestamp = System.currentTimeMillis(),
            isRead = false,
            isDelivered = false
        )

        viewModelScope.launch {
            try {
                messageDao.insertMessage(message)
                messageDao.updateLastMessage(conversationId, content, System.currentTimeMillis())
                loadMessages(conversationId)
                loadConversations()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun startNewConversation(otherUserId: String) {
        val userId = _currentUserId.value ?: return

        // Check if conversation already exists
        val existingConv = _conversations.value.find {
            (it.participant1Id == userId && it.participant2Id == otherUserId) ||
            (it.participant1Id == otherUserId && it.participant2Id == userId)
        }

        if (existingConv != null) {
            selectConversation(existingConv)
            return
        }

        // Check if user is followed
        val isFollowed = _followedUsers.value.any { it.id == otherUserId }
        if (!isFollowed) {
            // Cannot start conversation with non-followed user
            return
        }

        viewModelScope.launch {
            try {
                val currentUser = userDao.getUserById(userId)
                val otherUser = userDao.getUserById(otherUserId)

                if (currentUser != null && otherUser != null) {
                    val conversation = Conversation(
                        id = UUID.randomUUID().toString(),
                        participant1Id = userId,
                        participant1Name = currentUser.name,
                        participant2Id = otherUserId,
                        participant2Name = otherUser.name,
                        lastMessage = "",
                        lastMessageTime = System.currentTimeMillis(),
                        unreadCount = 0
                    )

                    messageDao.insertConversation(conversation)
                    loadConversations()
                    selectConversation(conversation)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun clearSelection() {
        _selectedConversation.value = null
        _messages.value = emptyList()
    }

    fun getTotalUnreadCount(): Int {
        val userId = _currentUserId.value ?: return 0
        return _conversations.value.sumOf { it.unreadCount }
    }
}
