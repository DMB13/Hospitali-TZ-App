package com.labtrace.tz.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.labtrace.tz.data.AppDatabase
import com.labtrace.tz.data.SampleRecord
import com.labtrace.tz.utils.SmsEmailSender
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class SampleViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val dao = db.sampleRecordDao()
    private val smsSender = SmsEmailSender(application)

    private val _samples = MutableStateFlow<List<SampleRecord>>(emptyList())
    val samples: StateFlow<List<SampleRecord>> = _samples

    init {
        loadSamples()
    }

    private fun loadSamples() {
        viewModelScope.launch {
            _samples.value = dao.getAll()
        }
    }

    fun createSample(patientInitials: String, sampleType: String, destinationLab: String) {
        val sampleId = UUID.randomUUID().toString().substring(0, 8).uppercase()
        val record = SampleRecord(
            sampleId = sampleId,
            patientInitials = patientInitials,
            sampleType = sampleType,
            destinationLab = destinationLab,
            status = "Collected",
            lastUpdated = System.currentTimeMillis()
        )
        viewModelScope.launch {
            dao.insert(record)
            loadSamples()
        }
    }

    fun updateStatus(sampleId: String, newStatus: String) {
        viewModelScope.launch {
            val record = dao.getById(sampleId)
            if (record != null) {
                val updated = record.copy(status = newStatus, lastUpdated = System.currentTimeMillis())
                dao.update(updated)
                loadSamples()
                // Send notification
                smsSender.sendSmsViaEmail("patient@example.com", "Sample $sampleId status updated to $newStatus")
            }
        }
    }

    fun getSampleById(sampleId: String): SampleRecord? {
        return _samples.value.find { it.sampleId == sampleId }
    }
}
