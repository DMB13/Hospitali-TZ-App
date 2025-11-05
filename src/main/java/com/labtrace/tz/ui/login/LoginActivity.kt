package com.labtrace.tz.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.labtrace.tz.ui.main.MainActivity
import com.labtrace.tz.utils.SecurityUtils
import java.util.concurrent.Executor

class LoginActivity : FragmentActivity() {
    private lateinit var securityUtils: SecurityUtils
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        securityUtils = SecurityUtils(this)

        // Check for root
        if (securityUtils.isDeviceRooted()) {
            // Show warning or exit
            finish()
            return
        }

        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    navigateToMain("Biometric", "staff") // Default to staff for biometric
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // Handle failure
                }
            })

        setContent {
            LoginScreen(
                onBiometricLogin = { showBiometricPrompt() },
                onPasswordLogin = { username, password, userType ->
                    if (validateCredentials(username, password)) {
                        navigateToMain(username, userType)
                    }
                }
            )
        }
    }

    private fun showBiometricPrompt() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Login")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use Password")
            .build()
        biometricPrompt.authenticate(promptInfo)
    }

    private fun validateCredentials(username: String, password: String): Boolean {
        // Basic validation - in real app, check against secure storage
        return username.isNotBlank() && password.length >= 6 && securityUtils.validateInput(username) && securityUtils.validateInput(password)
    }

    private fun navigateToMain(username: String, userType: String) {
        securityUtils.encryptedSharedPreferences.edit().putString("username", username).putString("user_type", userType).apply()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("user_type", userType)
        startActivity(intent)
        finish()
    }
}

@Composable
fun LoginScreen(onBiometricLogin: () -> Unit, onPasswordLogin: (String, String, String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("patient") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("LabTrace TZ Secure Login") }) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Select User Type")
            Row {
                RadioButton(
                    selected = userType == "patient",
                    onClick = { userType = "patient" }
                )
                Text("Patient", modifier = Modifier.padding(start = 4.dp, end = 16.dp))
                RadioButton(
                    selected = userType == "staff",
                    onClick = { userType = "staff" }
                )
                Text("Hospital Staff", modifier = Modifier.padding(start = 4.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { if (username.isNotBlank() && password.isNotBlank()) onPasswordLogin(username, password, userType) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login with Password")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onBiometricLogin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login with Biometric")
            }
        }
    }
}
