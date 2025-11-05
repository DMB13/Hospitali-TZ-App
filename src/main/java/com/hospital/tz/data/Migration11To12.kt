package com.hospital.tz.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_11_12 = object : Migration(11, 12) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add new columns to users table
        database.execSQL("ALTER TABLE users ADD COLUMN permanentResident INTEGER")
        database.execSQL("ALTER TABLE users ADD COLUMN locationAccessGranted INTEGER")
    }
}
