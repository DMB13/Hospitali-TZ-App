package com.hospital.tz.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val receiverId: String,
    val receiverName: String,
    val content: String,
    val messageType: MessageType = MessageType.TEXT,
    val timestamp: Long,
    val isRead: Boolean = false,
    val isDelivered: Boolean = false,
    val attachmentUrl: String? = null,
    val attachmentType: AttachmentType? = null,
    val attachmentName: String? = null,
    val isActive: Boolean = true
)

@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey val id: String,
    val participant1Id: String,
    val participant1Name: String,
    val participant2Id: String,
    val participant2Name: String,
    val lastMessage: String,
    val lastMessageTime: Long,
    val unreadCount: Int = 0,
    val isActive: Boolean = true
)

enum class MessageType {
    TEXT,
    IMAGE,
    VIDEO,
    FILE,
    CONTACT,
    EMOJI
}

enum class AttachmentType {
    IMAGE,
    VIDEO,
    DOCUMENT,
    AUDIO,
    CONTACT
}
