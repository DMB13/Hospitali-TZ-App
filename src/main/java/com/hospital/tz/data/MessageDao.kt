package com.hospital.tz.data

import androidx.room.*

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: Conversation)

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId AND isActive = 1 ORDER BY timestamp ASC")
    suspend fun getMessagesForConversation(conversationId: String): List<Message>

    @Query("SELECT * FROM conversations WHERE (participant1Id = :userId OR participant2Id = :userId) AND isActive = 1 ORDER BY lastMessageTime DESC")
    suspend fun getConversationsForUser(userId: String): List<Conversation>

    @Query("SELECT * FROM conversations WHERE id = :conversationId")
    suspend fun getConversationById(conversationId: String): Conversation?

    @Query("SELECT * FROM messages WHERE id = :messageId")
    suspend fun getMessageById(messageId: String): Message?

    @Query("UPDATE messages SET isRead = 1 WHERE conversationId = :conversationId AND receiverId = :userId")
    suspend fun markMessagesAsRead(conversationId: String, userId: String)

    @Query("UPDATE messages SET isDelivered = 1 WHERE id = :messageId")
    suspend fun markMessageAsDelivered(messageId: String)

    @Query("SELECT COUNT(*) FROM messages WHERE conversationId = :conversationId AND receiverId = :userId AND isRead = 0")
    suspend fun getUnreadCountForConversation(conversationId: String, userId: String): Int

    @Query("UPDATE conversations SET unreadCount = :count WHERE id = :conversationId")
    suspend fun updateUnreadCount(conversationId: String, count: Int)

    @Query("UPDATE conversations SET lastMessage = :message, lastMessageTime = :timestamp WHERE id = :conversationId")
    suspend fun updateLastMessage(conversationId: String, message: String, timestamp: Long)

    @Query("SELECT COUNT(*) FROM conversations WHERE (participant1Id = :userId OR participant2Id = :userId) AND unreadCount > 0")
    suspend fun getTotalUnreadCount(userId: String): Int

    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteMessage(messageId: String)

    @Query("DELETE FROM conversations WHERE id = :conversationId")
    suspend fun deleteConversation(conversationId: String)

    @Update
    suspend fun updateMessage(message: Message)

    @Update
    suspend fun updateConversation(conversation: Conversation)
}
