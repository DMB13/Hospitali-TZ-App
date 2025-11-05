package com.hospital.tz.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hospital.tz.data.AppDatabase
import com.hospital.tz.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val userDao = database.userDao()

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess.asStateFlow()

    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val user = userDao.getUserById(userId)
                _userProfile.value = user
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(
        userId: String,
        name: String,
        description: String? = null,
        profilePicture: String? = null,
        professionLicense: String? = null,
        nationalId: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _updateSuccess.value = false
            try {
                val currentUser = _userProfile.value ?: return@launch
                val updatedUser = currentUser.copy(
                    name = name,
                    description = description,
                    profilePicture = profilePicture ?: currentUser.profilePicture,
                    professionLicense = professionLicense ?: currentUser.professionLicense,
                    nationalId = nationalId ?: currentUser.nationalId,
                    lastLoginAt = System.currentTimeMillis()
                )
                userDao.updateUser(updatedUser)
                _userProfile.value = updatedUser
                _updateSuccess.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfilePicture(userId: String, profilePictureUrl: String) {
        viewModelScope.launch {
            try {
                val currentUser = _userProfile.value ?: return@launch
                val updatedUser = currentUser.copy(
                    profilePicture = profilePictureUrl,
                    lastLoginAt = System.currentTimeMillis()
                )
                userDao.updateUser(updatedUser)
                _userProfile.value = updatedUser
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update profile picture: ${e.message}"
            }
        }
    }

    fun updateMedicalCredentials(userId: String, professionLicense: String, nationalId: String) {
        viewModelScope.launch {
            try {
                val currentUser = _userProfile.value ?: return@launch
                val updatedUser = currentUser.copy(
                    professionLicense = professionLicense,
                    nationalId = nationalId,
                    lastLoginAt = System.currentTimeMillis()
                )
                userDao.updateUser(updatedUser)
                _userProfile.value = updatedUser
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update medical credentials: ${e.message}"
            }
        }
    }

    fun resetUpdateSuccess() {
        _updateSuccess.value = false
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun calculateProfileCompletion(user: User?): Float {
        if (user == null) return 0f

        var completedFields = 0
        var totalFields = 6 // name, email, userType, description, profilePicture, verification

        if (user.name.isNotBlank()) completedFields++
        if (user.email.isNotBlank()) completedFields++
        if (user.userType.isNotBlank()) completedFields++
        if (!user.description.isNullOrBlank()) completedFields++
        if (!user.profilePicture.isNullOrBlank()) completedFields++

        // Medical credentials for doctors/staff
        if (user.userType in listOf("Doctor", "Staff")) {
            totalFields += 2 // professionLicense, nationalId
            if (!user.professionLicense.isNullOrBlank()) completedFields++
            if (!user.nationalId.isNullOrBlank()) completedFields++
        }

        return completedFields.toFloat() / totalFields.toFloat()
    }
}
