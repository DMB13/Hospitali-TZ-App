package com.hospital.tz.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.hospital.tz.R
import com.hospital.tz.data.User
import com.hospital.tz.ui.viewmodels.ProfileViewModel

@Composable
fun ProfileScreen(
    userId: String,
    viewModel: ProfileViewModel = viewModel()
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val updateSuccess by viewModel.updateSuccess.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var showCredentialsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        viewModel.loadUserProfile(userId)
    }

    // Handle success message
    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            // Show success snackbar or toast
            viewModel.resetUpdateSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                errorMessage != null -> {
                    ErrorView(errorMessage!!) { viewModel.loadUserProfile(userId) }
                }
                userProfile != null -> {
                    ProfileContent(
                        user = userProfile!!,
                        viewModel = viewModel,
                        onEditCredentials = { showCredentialsDialog = true }
                    )
                }
                else -> {
                    Text(
                        "Profile not found",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.h6
                    )
                }
            }
        }
    }

    // Edit Profile Dialog
    if (showEditDialog && userProfile != null) {
        EditProfileDialog(
            user = userProfile!!,
            onDismiss = { showEditDialog = false },
            onSave = { name, description ->
                viewModel.updateProfile(
                    userId = userId,
                    name = name,
                    description = description
                )
                showEditDialog = false
            }
        )
    }

    // Medical Credentials Dialog
    if (showCredentialsDialog && userProfile != null) {
        MedicalCredentialsDialog(
            user = userProfile!!,
            onDismiss = { showCredentialsDialog = false },
            onSave = { license, nationalId ->
                viewModel.updateMedicalCredentials(userId, license, nationalId)
                showCredentialsDialog = false
            }
        )
    }
}

@Composable
fun ProfileContent(
    user: User,
    viewModel: ProfileViewModel,
    onEditCredentials: () -> Unit
) {
    val profileCompletion = viewModel.calculateProfileCompletion(user)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Header
        item {
            ProfileHeader(user, profileCompletion)
        }

        // Profile Completion Progress
        item {
            ProfileCompletionCard(profileCompletion)
        }

        // Basic Information
        item {
            ProfileSectionCard("Basic Information") {
                ProfileField("Name", user.name)
                ProfileField("Email", user.email)
                ProfileField("User Type", user.userType)
                ProfileField("Member Since", formatDate(user.createdAt))
                ProfileField("Last Login", formatDate(user.lastLoginAt))
            }
        }

        // Medical Credentials (for Doctors/Staff)
        if (user.userType in listOf("Doctor", "Staff")) {
            item {
                ProfileSectionCard("Medical Credentials") {
                    ProfileField("License Number", user.professionLicense ?: "Not provided")
                    ProfileField("National ID", user.nationalId ?: "Not provided")
                    Button(
                        onClick = onEditCredentials,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Update Credentials")
                    }
                }
            }
        }

        // Description
        item {
            ProfileSectionCard("About") {
                Text(
                    text = user.description ?: "No description provided",
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Start
                )
            }
        }

        // Verification Status
        item {
            VerificationStatusCard(user.isVerified)
        }

        // Settings Section
        item {
            ProfileSectionCard("Settings") {
                SettingItem("Notifications", Icons.Default.Notifications) { /* TODO */ }
                SettingItem("Privacy", Icons.Default.Lock) { /* TODO */ }
                SettingItem("Security", Icons.Default.Security) { /* TODO */ }
                SettingItem("Help & Support", Icons.Default.Help) { /* TODO */ }
            }
        }

        // Help Section
        item {
            ProfileSectionCard("Help & Support") {
                HelpItem("FAQ", "Frequently asked questions") { /* TODO */ }
                HelpItem("Contact Support", "Get help from our support team") { /* TODO */ }
                HelpItem("User Guide", "Learn how to use the app") { /* TODO */ }
                HelpItem("Report Issue", "Report a problem or bug") { /* TODO */ }
            }
        }

        // Powered by text
        item {
            Text(
                text = "Powered by DevisByaru",
                style = MaterialTheme.typography.caption,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            )
        }
    }
}

@Composable
fun ProfileHeader(user: User, completion: Float) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Picture
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            if (user.profilePicture != null) {
                Image(
                    painter = rememberAsyncImagePainter(user.profilePicture),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Default Profile",
                    modifier = Modifier.size(60.dp),
                    tint = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = user.name,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = user.userType,
            style = MaterialTheme.typography.body1,
            color = Color.Gray
        )

        if (user.isVerified) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    Icons.Default.Verified,
                    contentDescription = "Verified",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Verified",
                    style = MaterialTheme.typography.body2,
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}

@Composable
fun ProfileCompletionCard(completion: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Profile Completion",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = completion,
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF4CAF50)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "${(completion * 100).toInt()}% Complete",
                style = MaterialTheme.typography.body2,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ProfileSectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun ProfileField(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            label,
            style = MaterialTheme.typography.caption,
            color = Color.Gray
        )
        Text(
            value,
            style = MaterialTheme.typography.body1
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun VerificationStatusCard(isVerified: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp,
        backgroundColor = if (isVerified) Color(0xFFE8F5E8) else Color(0xFFFFF3E0)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (isVerified) Icons.Default.Verified else Icons.Default.Warning,
                contentDescription = null,
                tint = if (isVerified) Color(0xFF4CAF50) else Color(0xFFFF9800)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    if (isVerified) "Account Verified" else "Account Not Verified",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    if (isVerified)
                        "Your account has been verified and you have full access to all features."
                    else
                        "Please complete your profile and submit verification documents.",
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }
}

@Composable
fun SettingItem(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.Gray)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, style = MaterialTheme.typography.body1)
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
    }
}

@Composable
fun HelpItem(title: String, description: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.body2,
            color = Color.Gray
        )
    }
}

@Composable
fun EditProfileDialog(
    user: User,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(user.name) }
    var description by remember { mutableStateOf(user.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(name, description) },
                enabled = name.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun MedicalCredentialsDialog(
    user: User,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var license by remember { mutableStateOf(user.professionLicense ?: "") }
    var nationalId by remember { mutableStateOf(user.nationalId ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Medical Credentials") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = license,
                    onValueChange = { license = it },
                    label = { Text("Professional License") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = nationalId,
                    onValueChange = { nationalId = it },
                    label = { Text("National ID") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(license, nationalId) },
                enabled = license.isNotBlank() && nationalId.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Red
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Error loading profile",
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            message,
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

private fun formatDate(timestamp: Long): String {
    // Simple date formatting - in real app, use proper date formatting
    return java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
        .format(java.util.Date(timestamp))
}
