package com.hospital.tz.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientRecordDao {

    // Patient Records
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatientRecord(record: PatientRecord)

    @Update
    suspend fun updatePatientRecord(record: PatientRecord)

    @Delete
    suspend fun deletePatientRecord(record: PatientRecord)

    @Query("SELECT * FROM patient_records WHERE id = :recordId")
    suspend fun getPatientRecordById(recordId: String): PatientRecord?

    @Query("SELECT * FROM patient_records WHERE patientId = :patientId")
    suspend fun getPatientRecordByPatientId(patientId: String): PatientRecord?

    @Query("SELECT * FROM patient_records WHERE primaryDoctorId = :doctorId ORDER BY updatedAt DESC")
    fun getPatientRecordsForDoctor(doctorId: String): Flow<List<PatientRecord>>

    @Query("SELECT * FROM patient_records WHERE patientName LIKE '%' || :searchQuery || '%' ORDER BY patientName ASC")
    fun searchPatientRecords(searchQuery: String): Flow<List<PatientRecord>>

    @Query("SELECT * FROM patient_records ORDER BY patientName ASC")
    fun getAllPatientRecords(): Flow<List<PatientRecord>>

    // Medical History
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicalHistory(history: MedicalHistory)

    @Update
    suspend fun updateMedicalHistory(history: MedicalHistory)

    @Delete
    suspend fun deleteMedicalHistory(history: MedicalHistory)

    @Query("SELECT * FROM medical_history WHERE patientId = :patientId ORDER BY recordDate DESC")
    fun getMedicalHistoryForPatient(patientId: String): Flow<List<MedicalHistory>>

    @Query("SELECT * FROM medical_history WHERE id = :historyId")
    suspend fun getMedicalHistoryById(historyId: String): MedicalHistory?

    @Query("SELECT * FROM medical_history WHERE doctorId = :doctorId ORDER BY recordDate DESC")
    fun getMedicalHistoryByDoctor(doctorId: String): Flow<List<MedicalHistory>>

    // Prescriptions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrescription(prescription: Prescription)

    @Update
    suspend fun updatePrescription(prescription: Prescription)

    @Delete
    suspend fun deletePrescription(prescription: Prescription)

    @Query("SELECT * FROM prescriptions WHERE patientId = :patientId ORDER BY prescribedDate DESC")
    fun getPrescriptionsForPatient(patientId: String): Flow<List<Prescription>>

    @Query("SELECT * FROM prescriptions WHERE doctorId = :doctorId ORDER BY prescribedDate DESC")
    fun getPrescriptionsByDoctor(doctorId: String): Flow<List<Prescription>>

    @Query("SELECT * FROM prescriptions WHERE id = :prescriptionId")
    suspend fun getPrescriptionById(prescriptionId: String): Prescription?

    @Query("SELECT * FROM prescriptions WHERE status = :status ORDER BY prescribedDate DESC")
    fun getPrescriptionsByStatus(status: PrescriptionStatus): Flow<List<Prescription>>

    @Query("UPDATE prescriptions SET status = :status WHERE id = :prescriptionId")
    suspend fun updatePrescriptionStatus(prescriptionId: String, status: PrescriptionStatus)

    @Query("UPDATE prescriptions SET refillsRemaining = refillsRemaining - 1 WHERE id = :prescriptionId AND refillsRemaining > 0")
    suspend fun decrementRefills(prescriptionId: String)

    // Combined queries for patient dashboard
    @Query("""
        SELECT pr.*, COUNT(DISTINCT mh.id) as historyCount, COUNT(DISTINCT p.id) as prescriptionCount
        FROM patient_records pr
        LEFT JOIN medical_history mh ON pr.patientId = mh.patientId
        LEFT JOIN prescriptions p ON pr.patientId = p.patientId
        WHERE pr.primaryDoctorId = :doctorId
        GROUP BY pr.id
        ORDER BY pr.updatedAt DESC
    """)
    fun getPatientRecordsWithCounts(doctorId: String): Flow<List<PatientRecordWithCounts>>

    @Query("""
        SELECT pr.*, COUNT(DISTINCT mh.id) as historyCount, COUNT(DISTINCT p.id) as prescriptionCount
        FROM patient_records pr
        LEFT JOIN medical_history mh ON pr.patientId = mh.patientId
        LEFT JOIN prescriptions p ON pr.patientId = p.patientId
        WHERE pr.patientId = :patientId
        GROUP BY pr.id
    """)
    suspend fun getPatientRecordWithCounts(patientId: String): PatientRecordWithCounts?
}

data class PatientRecordWithCounts(
    val id: String,
    val patientId: String,
    val patientName: String,
    val dateOfBirth: Long,
    val gender: String,
    val bloodType: String?,
    val allergies: String,
    val emergencyContact: String,
    val emergencyPhone: String,
    val medicalConditions: String,
    val currentMedications: String,
    val insuranceProvider: String?,
    val insuranceNumber: String?,
    val primaryDoctorId: String?,
    val primaryDoctorName: String?,
    val lastVisitDate: Long?,
    val nextAppointmentDate: Long?,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val historyCount: Int,
    val prescriptionCount: Int
)
