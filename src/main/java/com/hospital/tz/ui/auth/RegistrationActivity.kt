package com.hospital.tz.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hospital.tz.data.User
import com.hospital.tz.data.UserType
import com.hospital.tz.ui.main.MainActivity
import kotlinx.coroutines.launch

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegistrationScreen { user ->
                // Save user to database and navigate to main
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("user_id", user.id)
                startActivity(intent)
                finish()
            }
        }
    }
}

@Composable
fun RegistrationScreen(onRegister: (User) -> Unit) {
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf(UserType.NORMAL) }
    var nationalId by remember { mutableStateOf("") }
    var professionLicense by remember { mutableStateOf("") }
    var agreedToPrivacy by remember { mutableStateOf(false) }
    var permanentResident by remember { mutableStateOf(false) }
    var locationAccessGranted by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Hospital TZ Registration") }) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Select User Type:")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RadioButton(
                    selected = userType == UserType.NORMAL,
                    onClick = { userType = UserType.NORMAL }
                )
                Text("Normal User (Free)")
                RadioButton(
                    selected = userType == UserType.STAFF,
                    onClick = { userType = UserType.STAFF }
                )
                Text("Staff User (Premium)")
            }

            if (userType == UserType.STAFF) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = nationalId,
                    onValueChange = { nationalId = it },
                    label = { Text("National ID") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = professionLicense,
                    onValueChange = { professionLicense = it },
                    label = { Text("Profession License") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = agreedToPrivacy,
                    onCheckedChange = { agreedToPrivacy = it }
                )
                Text("I agree to the Privacy Policy")
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = permanentResident,
                    onCheckedChange = { permanentResident = it }
                )
                Text("I am a permanent resident")
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = locationAccessGranted,
                    onCheckedChange = { locationAccessGranted = it }
                )
                Text("Grant location access")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (email.isNotBlank() && name.isNotBlank() && agreedToPrivacy) {
                        val user = User(
                            id = email, // Using email as ID for simplicity
                            email = email,
                            name = name,
                            userType = userType,
                            nationalId = if (userType == UserType.STAFF) nationalId else null,
                            professionLicense = if (userType == UserType.STAFF) professionLicense else null,
                            permanentResident = permanentResident,
                            locationAccessGranted = locationAccessGranted
                        )
                        onRegister(user)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register")
            }
        }
    }
}
