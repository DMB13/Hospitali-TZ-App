package com.hospital.tz.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(entities = [
    User::class,
    SampleRecord::class,
    Feedback::class,
    Announcement::class,
    Hospital::class,
    PatientProgress::class,
    VideoContent::class,
    VideoComment::class,
    UserFollow::class,
    VideoLike::class,
    VideoSave::class,
    Message::class,
    Conversation::class,
    PatientRecord::class,
    MedicalHistory::class,
    Prescription::class,
    Appointment::class,
    DoctorAvailability::class,
    AppointmentReminder::class,
    Settings::class,
    EmergencyContact::class,
    EmergencyAlert::class,
    EmergencyProtocol::class,
    SavedReport::class
], version = 12)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun sampleRecordDao(): SampleRecordDao
    abstract fun feedbackDao(): FeedbackDao
    abstract fun announcementDao(): AnnouncementDao
    abstract fun hospitalDao(): HospitalDao
    abstract fun patientProgressDao(): PatientProgressDao
    abstract fun videoContentDao(): VideoContentDao
    abstract fun videoCommentDao(): VideoCommentDao
    abstract fun userFollowDao(): UserFollowDao
    abstract fun videoLikeDao(): VideoLikeDao
    abstract fun videoSaveDao(): VideoSaveDao
    abstract fun messageDao(): MessageDao
    abstract fun patientRecordDao(): PatientRecordDao
    abstract fun appointmentDao(): AppointmentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val passphrase: ByteArray = SQLiteDatabase.getBytes("hospital_tz_secure_key".toCharArray())
                val factory = SupportFactory(passphrase)
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hospital_tz_db"
                )
                .openHelperFactory(factory)
                .addMigrations(
                    MIGRATION_1_2,
                    MIGRATION_2_3,
                    MIGRATION_3_4,
                    MIGRATION_4_5,
                    MIGRATION_5_6,
                    MIGRATION_6_7,
                    MIGRATION_7_8,
                    MIGRATION_8_9,
                    MIGRATION_9_10,
                    MIGRATION_10_11,
                    MIGRATION_11_12
                )
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `feedback` (`id` TEXT NOT NULL, `userType` TEXT NOT NULL, `service` TEXT NOT NULL, `rating` INTEGER NOT NULL, `comment` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`id`))")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `users` (
                `id` TEXT NOT NULL,
                `email` TEXT NOT NULL,
                `name` TEXT NOT NULL,
                `userType` TEXT NOT NULL,
                `isVerified` INTEGER NOT NULL DEFAULT 0,
                `nationalId` TEXT,
                `professionLicense` TEXT,
                `profilePicture` TEXT,
                `description` TEXT,
                `createdAt` INTEGER NOT NULL,
                `lastLoginAt` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
        """)
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `announcements` (
                `id` TEXT NOT NULL,
                `title` TEXT NOT NULL,
                `content` TEXT NOT NULL,
                `hospitalName` TEXT NOT NULL,
                `timestamp` INTEGER NOT NULL,
                `isRead` INTEGER NOT NULL DEFAULT 0,
                PRIMARY KEY(`id`)
            )
        """)
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `hospitals` (
                `id` TEXT NOT NULL,
                `name` TEXT NOT NULL,
                `type` TEXT NOT NULL,
                `region` TEXT NOT NULL,
                `district` TEXT NOT NULL,
                `latitude` REAL NOT NULL,
                `longitude` REAL NOT NULL,
                `address` TEXT NOT NULL,
                `phone` TEXT,
                `email` TEXT,
                `services` TEXT NOT NULL,
                `isActive` INTEGER NOT NULL DEFAULT 1,
                PRIMARY KEY(`id`)
            )
        """)
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `patient_progress` (
                `patientId` TEXT NOT NULL,
                `patientName` TEXT NOT NULL,
                `diagnosis` TEXT NOT NULL,
                `status` TEXT NOT NULL,
                `hospitalName` TEXT NOT NULL,
                `doctorName` TEXT,
                `roomNumber` TEXT,
                `admissionDate` INTEGER NOT NULL,
                `lastUpdate` INTEGER NOT NULL,
                `nextAppointment` INTEGER,
                `notes` TEXT,
                PRIMARY KEY(`patientId`)
            )
        """)
    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Video content table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `video_content` (
                `id` TEXT NOT NULL,
                `title` TEXT NOT NULL,
                `description` TEXT NOT NULL,
                `videoUrl` TEXT NOT NULL,
                `thumbnailUrl` TEXT,
                `duration` INTEGER NOT NULL,
                `authorId` TEXT NOT NULL,
                `authorName` TEXT NOT NULL,
                `authorType` TEXT NOT NULL,
                `uploadDate` INTEGER NOT NULL,
                `views` INTEGER NOT NULL DEFAULT 0,
                `likes` INTEGER NOT NULL DEFAULT 0,
                `comments` INTEGER NOT NULL DEFAULT 0,
                `shares` INTEGER NOT NULL DEFAULT 0,
                `tags` TEXT NOT NULL,
                `isApproved` INTEGER NOT NULL DEFAULT 0,
                `moderationReason` TEXT,
                `isActive` INTEGER NOT NULL DEFAULT 1,
                PRIMARY KEY(`id`)
            )
        """)

        // Video comments table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `video_comments` (
                `id` TEXT NOT NULL,
                `videoId` TEXT NOT NULL,
                `userId` TEXT NOT NULL,
                `userName` TEXT NOT NULL,
                `comment` TEXT NOT NULL,
                `timestamp` INTEGER NOT NULL,
                `likes` INTEGER NOT NULL DEFAULT 0,
                `isActive` INTEGER NOT NULL DEFAULT 1,
                PRIMARY KEY(`id`)
            )
        """)

        // User follows table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `user_follows` (
                `id` TEXT NOT NULL,
                `followerId` TEXT NOT NULL,
                `followedId` TEXT NOT NULL,
                `followDate` INTEGER NOT NULL,
                `isActive` INTEGER NOT NULL DEFAULT 1,
                PRIMARY KEY(`id`)
            )
        """)

        // Video likes table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `video_likes` (
                `id` TEXT NOT NULL,
                `videoId` TEXT NOT NULL,
                `userId` TEXT NOT NULL,
                `timestamp` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
        """)

        // Video saves table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `video_saves` (
                `id` TEXT NOT NULL,
                `videoId` TEXT NOT NULL,
                `userId` TEXT NOT NULL,
                `saveDate` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
        """)
    }
}

val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Messages table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `messages` (
                `id` TEXT NOT NULL,
                `conversationId` TEXT NOT NULL,
                `senderId` TEXT NOT NULL,
                `senderName` TEXT NOT NULL,
                `receiverId` TEXT NOT NULL,
                `receiverName` TEXT NOT NULL,
                `content` TEXT NOT NULL,
                `messageType` TEXT NOT NULL DEFAULT 'TEXT',
                `timestamp` INTEGER NOT NULL,
                `isRead` INTEGER NOT NULL DEFAULT 0,
                `isDelivered` INTEGER NOT NULL DEFAULT 0,
                `attachmentUrl` TEXT,
                `attachmentType` TEXT,
                `attachmentName` TEXT,
                `isActive` INTEGER NOT NULL DEFAULT 1,
                PRIMARY KEY(`id`)
            )
        """)

        // Conversations table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `conversations` (
                `id` TEXT NOT NULL,
                `participant1Id` TEXT NOT NULL,
                `participant1Name` TEXT NOT NULL,
                `participant2Id` TEXT NOT NULL,
                `participant2Name` TEXT NOT NULL,
                `lastMessage` TEXT NOT NULL,
                `lastMessageTime` INTEGER NOT NULL,
                `unreadCount` INTEGER NOT NULL DEFAULT 0,
                `isActive` INTEGER NOT NULL DEFAULT 1,
                PRIMARY KEY(`id`)
            )
        """)
    }
}

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Patient Records table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `patient_records` (
                `id` TEXT NOT NULL,
                `patientId` TEXT NOT NULL,
                `patientName` TEXT NOT NULL,
                `dateOfBirth` INTEGER NOT NULL,
                `gender` TEXT NOT NULL,
                `bloodType` TEXT,
                `allergies` TEXT NOT NULL,
                `emergencyContact` TEXT NOT NULL,
                `emergencyPhone` TEXT NOT NULL,
                `medicalConditions` TEXT NOT NULL,
                `currentMedications` TEXT NOT NULL,
                `insuranceProvider` TEXT,
                `insuranceNumber` TEXT,
                `primaryDoctorId` TEXT,
                `primaryDoctorName` TEXT,
                `lastVisitDate` INTEGER,
                `nextAppointmentDate` INTEGER,
                `isActive` INTEGER NOT NULL DEFAULT 1,
                `createdAt` INTEGER NOT NULL,
                `updatedAt` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
        """)

        // Medical History table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `medical_history` (
                `id` TEXT NOT NULL,
                `patientId` TEXT NOT NULL,
                `recordDate` INTEGER NOT NULL,
                `condition` TEXT NOT NULL,
                `diagnosis` TEXT NOT NULL,
                `treatment` TEXT NOT NULL,
                `doctorId` TEXT NOT NULL,
                `doctorName` TEXT NOT NULL,
                `notes` TEXT NOT NULL,
                `attachments` TEXT NOT NULL,
                `isActive` INTEGER NOT NULL DEFAULT 1,
                `createdAt` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
        """)

        // Prescriptions table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `prescriptions` (
                `id` TEXT NOT NULL,
                `patientId` TEXT NOT NULL,
                `doctorId` TEXT NOT NULL,
                `doctorName` TEXT NOT NULL,
                `medicationName` TEXT NOT NULL,
                `dosage` TEXT NOT NULL,
                `frequency` TEXT NOT NULL,
                `duration` TEXT NOT NULL,
                `instructions` TEXT NOT NULL,
                `prescribedDate` INTEGER NOT NULL,
                `startDate` INTEGER NOT NULL,
                `endDate` INTEGER,
                `refillsRemaining` INTEGER NOT NULL,
                `pharmacyId` TEXT,
                `pharmacyName` TEXT,
                `status` TEXT NOT NULL DEFAULT 'ACTIVE',
                `isActive` INTEGER NOT NULL DEFAULT 1,
                `createdAt` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
        """)
    }
}

val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Appointments table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `appointments` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `patientId` TEXT NOT NULL,
                `patientName` TEXT NOT NULL,
                `doctorId` TEXT NOT NULL,
                `doctorName` TEXT NOT NULL,
                `hospitalId` INTEGER NOT NULL,
                `hospitalName` TEXT NOT NULL,
                `appointmentDate` INTEGER NOT NULL,
                `appointmentTime` TEXT NOT NULL,
                `durationMinutes` INTEGER NOT NULL DEFAULT 30,
                `appointmentType` TEXT NOT NULL DEFAULT 'CONSULTATION',
                `status` TEXT NOT NULL DEFAULT 'SCHEDULED',
                `symptoms` TEXT,
                `notes` TEXT,
                `telemedicineLink` TEXT,
                `roomNumber` TEXT,
                `createdAt` INTEGER NOT NULL,
                `updatedAt` INTEGER NOT NULL,
                `reminderSent` INTEGER NOT NULL DEFAULT 0,
                `followUpAppointmentId` INTEGER,
                FOREIGN KEY(`patientId`) REFERENCES `users`(`id`) ON DELETE CASCADE,
                FOREIGN KEY(`doctorId`) REFERENCES `users`(`id`) ON DELETE CASCADE,
                FOREIGN KEY(`hospitalId`) REFERENCES `hospitals`(`id`) ON DELETE CASCADE
            )
        """)

        // Doctor Availability table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `doctor_availability` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `doctorId` TEXT NOT NULL,
                `hospitalId` INTEGER NOT NULL,
                `dayOfWeek` INTEGER NOT NULL,
                `startTime` TEXT NOT NULL,
                `endTime` TEXT NOT NULL,
                `slotDurationMinutes` INTEGER NOT NULL DEFAULT 30,
                `isAvailable` INTEGER NOT NULL DEFAULT 1,
                `maxAppointmentsPerDay` INTEGER NOT NULL DEFAULT 8,
                `createdAt` INTEGER NOT NULL,
                `updatedAt` INTEGER NOT NULL,
                FOREIGN KEY(`doctorId`) REFERENCES `users`(`id`) ON DELETE CASCADE,
                FOREIGN KEY(`hospitalId`) REFERENCES `hospitals`(`id`) ON DELETE CASCADE
            )
        """)

        // Appointment Reminders table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `appointment_reminders` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `appointmentId` INTEGER NOT NULL,
                `reminderType` TEXT NOT NULL,
                `scheduledTime` INTEGER NOT NULL,
                `sent` INTEGER NOT NULL DEFAULT 0,
                `sentAt` INTEGER,
                `createdAt` INTEGER NOT NULL,
                FOREIGN KEY(`appointmentId`) REFERENCES `appointments`(`id`) ON DELETE CASCADE
            )
        """)
    }
}

val MIGRATION_10_11 = object : Migration(10, 11) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add any additional migrations if needed
    }
}
