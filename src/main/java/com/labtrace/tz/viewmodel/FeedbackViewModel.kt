package com.labtrace.tz.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.labtrace.tz.data.AppDatabase
import com.labtrace.tz.data.Feedback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class FeedbackViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val dao = db.feedbackDao()

    private val _feedbacks = MutableStateFlow<List<Feedback>>(emptyList())
    val feedbacks: StateFlow<List<Feedback>> = _feedbacks

    init {
        loadFeedbacks()
    }

    private fun loadFeedbacks() {
        viewModelScope.launch {
            _feedbacks.value = dao.getAll()
        }
    }

    fun submitFeedback(userType: String, service: String, rating: Int, comment: String) {
        val feedback = Feedback(
            id = UUID.randomUUID().toString(),
            userType = userType,
            service = service,
            rating = rating,
            comment = comment,
            timestamp = System.currentTimeMillis()
        )
        viewModelScope.launch {
            dao.insert(feedback)
            loadFeedbacks()
        }
    }
}
