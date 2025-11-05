package com.labtrace.tz.utils

import android.content.Context
import android.util.Log
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class SmsEmailSender(private val context: Context) {

    // Example: send SMS via email gateway (e.g., number@txt.att.net)
    fun sendSmsViaEmail(toEmail: String, messageBody: String) {
        val username = "your_email@example.com" // TODO: replace with real email
        val password = "your_password" // TODO: replace with real password

        val props = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
        }

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(username, password)
            }
        })

        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(username))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail))
                subject = "LabTrace TZ Notification"
                setText(messageBody)
            }
            Transport.send(message)
            Log.d("SmsEmailSender", "Email sent to $toEmail")
        } catch (e: Exception) {
            Log.e("SmsEmailSender", "Failed to send email", e)
        }
    }
}
