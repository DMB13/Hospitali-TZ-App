package com.labtrace.tz.utils

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.io.File
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class SecurityUtils(private val context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    val encryptedSharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun isDeviceRooted(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        for (path in paths) {
            if (File(path).exists()) return true
        }

        // Check for SU binary in PATH
        val envPath = System.getenv("PATH") ?: ""
        for (path in envPath.split(":")) {
            if (File(path, "su").exists()) return true
        }

        return false
    }

    fun validateInput(input: String): Boolean {
        // Basic input validation - no SQL injection patterns
        val sqlInjectionPatterns = arrayOf(
            "';", "--", "/*", "*/", "xp_", "sp_", "exec", "union", "select", "insert", "update", "delete", "drop"
        )
        val lowerInput = input.lowercase()
        for (pattern in sqlInjectionPatterns) {
            if (lowerInput.contains(pattern)) return false
        }
        return true
    }

    fun sanitizeInput(input: String): String {
        // Basic sanitization - remove potentially dangerous characters
        return input.replace(Regex("[<>\"';&]"), "")
    }

    fun generateSecureId(): String {
        return java.util.UUID.randomUUID().toString()
    }
}
