package com.hospital.tz.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_10_11 = object : Migration(10, 11) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Settings table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `settings` (
                `userId` TEXT NOT NULL,
                `notificationsEnabled` INTEGER NOT NULL DEFAULT 1,
                `appointmentReminders` INTEGER NOT NULL DEFAULT 1,
                `messageNotifications` INTEGER NOT NULL DEFAULT 1,
                `announcementNotifications` INTEGER NOT NULL DEFAULT 1,
                `theme` TEXT NOT NULL DEFAULT 'SYSTEM',
                `language` TEXT NOT NULL DEFAULT 'en',
                `privacyLevel` TEXT NOT NULL DEFAULT 'STANDARD',
                `dataSharing` INTEGER NOT NULL DEFAULT 0,
                `biometricEnabled` INTEGER NOT NULL DEFAULT 0,
                `autoBackup` INTEGER NOT NULL DEFAULT 0,
                `backupFrequency` TEXT NOT NULL DEFAULT 'WEEKLY',
                `lastBackupDate` INTEGER,
                `createdAt` INTEGER NOT NULL,
                `updatedAt` INTEGER NOT NULL,
                PRIMARY KEY(`userId`)
            )
        """)

        // Emergency Contacts table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `emergency_contacts` (
                `id` TEXT NOT NULL,
                `userId` TEXT NOT NULL,
                `name` TEXT NOT NULL,
                `relationship` TEXT NOT NULL,
                `phoneNumber` TEXT NOT NULL,
                `email` TEXT,
                `isPrimary` INTEGER NOT NULL DEFAULT 0,
                `createdAt` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
        """)

        // Emergency Alerts table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `emergency_alerts` (
                `id` TEXT NOT NULL,
                `userId` TEXT NOT NULL,
                `alertType` TEXT NOT NULL,
                `location` TEXT,
                `latitude` REAL,
                `longitude` REAL,
                `message` TEXT,
                `timestamp` INTEGER NOT NULL,
                `status` TEXT NOT NULL DEFAULT 'ACTIVE',
                `respondersNotified` INTEGER NOT NULL DEFAULT 0,
                `responseTime` INTEGER,
                PRIMARY KEY(`id`)
            )
        """)

        // Emergency Protocols table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `emergency_protocols` (
                `id` TEXT NOT NULL,
                `title` TEXT NOT NULL,
                `description` TEXT NOT NULL,
                `steps` TEXT NOT NULL,
                `category` TEXT NOT NULL,
                `isActive` INTEGER NOT NULL DEFAULT 1,
                `createdAt` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
        """)

        // Saved Reports table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `saved_reports` (
                `id` TEXT NOT NULL,
                `userId` TEXT NOT NULL,
                `reportType` TEXT NOT NULL,
                `reportData` TEXT NOT NULL,
                `generatedAt` INTEGER NOT NULL,
                `period` TEXT NOT NULL,
                `title` TEXT NOT NULL,
                `description` TEXT,
                PRIMARY KEY(`id`)
            )
        """)
    }
}
