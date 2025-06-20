package com.example.todoapp.viewModel


import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.local.database.MyAppDatabase
import com.example.todoapp.local.entities.AppSettingsEntity
import com.example.todoapp.local.entities.ContactEntity
import com.example.todoapp.local.entities.HistoryEntity
import com.example.todoapp.local.entities.MedicalUnitsEntity
import com.example.todoapp.local.entities.MessageMediaEntity
import com.example.todoapp.local.entities.MessageRecipientEntity
import com.example.todoapp.local.entities.SOSMessageEntity
import com.example.todoapp.local.entities.TimerEntity
import com.example.todoapp.local.entities.UserEntity
import com.example.todoapp.local.entities.VideoCapture
import com.example.todoapp.repository.MyAppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyviewModel(application: Application) : AndroidViewModel(application) {
    private val database = MyAppDatabase.getDatabase(application)
    private val repository = MyAppRepository(database)

    // Flows for different entities
    val allContacts: Flow<List<ContactEntity>> = repository.getAllContacts.flowOn(Dispatchers.IO)
    val allVideoCaptures: Flow<List<VideoCapture>> = repository.getAllVideoCaptures
    val allSOSMessages: Flow<List<SOSMessageEntity>> = repository.getAllSOSMessages.flowOn(Dispatchers.IO)
    val allUsers: Flow<List<UserEntity>> = repository.getAllUsers
    val allHistory: Flow<List<HistoryEntity>> = repository.getAllHistory
    val allMedicalUnits: Flow<List<MedicalUnitsEntity>> = repository.getAllMedicalUnits

    // Medical Units Operations
    suspend fun createMedicalUnit(medicalUnitsEntity: MedicalUnitsEntity) = repository.addMedicalUnits(medicalUnitsEntity)
    suspend fun updateMedicalUnit(medicalUnitsEntity: MedicalUnitsEntity) = repository.updateMedicalUnits(medicalUnitsEntity)
    suspend fun deleteMedicalUnit(medicalUnitsEntity: MedicalUnitsEntity) = repository.deleteMedicalUnits(medicalUnitsEntity)

    suspend fun createHistory(historyEntity: HistoryEntity) = repository.createHistory(historyEntity)

    // User operations
    fun addUser(user: UserEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.addUser(user)
    }

    fun updateProfile(user: UserEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateProfile(user)
    }

    fun updateUser(user: UserEntity) = viewModelScope.launch {
        repository.updateUser(user)
    }

    fun getUserById(userId: Int) = viewModelScope.launch {
        repository.getUserById(userId)
    }

    suspend fun getUserByIdModified(userId: Int) = withContext(Dispatchers.IO) {
        repository.getUserById(userId)
    }

    suspend fun getUserByEmailAndPassword(email: String, password: String): UserEntity {
        return withContext(Dispatchers.IO){ repository.getUserByEmailAndPassword(email, password)!! }
    }

    // Contact operations
    fun addContact(contact: ContactEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.addContact(contact)
    }

//    fun updateContact(contact: ContactEntity) = viewModelScope.launch {
//        repository.updateContact(contact)
//    }

    fun updateContact(contact: ContactEntity) = viewModelScope.launch(Dispatchers.IO) {
        // Convert from old Contact model to ContactEntity
        // This is a compatibility method to handle the transition
        val contactEntity = ContactEntity(
            id = contact.id,
            userId = 1, // Default user ID
            name = contact.name,
            phoneNumber = contact.phoneNumber,
            isActive = contact.isActive,
            dateCreated = contact.dateCreated ?: java.util.Date(),
            createdBy = contact.createdBy ?: "System",
            dateModified = java.util.Date(),
            modifiedBy = "User"
        )
        database.contactDao().update(contactEntity)
    }

    fun getContactsByUserId(userId: Int) = viewModelScope.launch {
        repository.getContactsByUserId(userId)
    }

    fun deleteContacts(contact: ContactEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteContacts(contact)
    }

    // SOS Message operations
    fun addSOSMessage(sosMessage: SOSMessageEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.addSOSMessage(sosMessage)
    }

    fun updateSOSMessage(sosMessage: SOSMessageEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateSOSMessage(sosMessage)
    }

    fun getSOSMessagesByProfileId(profileId: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.getSOSMessagesByProfileId(profileId)
    }

    // Message Media operations
    fun addMessageMedia(messageMedia: MessageMediaEntity) = viewModelScope.launch {
        repository.addMessageMedia(messageMedia)
    }

    fun updateMessageMedia(messageMedia: MessageMediaEntity) = viewModelScope.launch {
        repository.updateMessageMedia(messageMedia)
    }

    fun getMessageMediaByMessageId(messageId: Int) = viewModelScope.launch {
        repository.getMessageMediaByMessageId(messageId)
    }

    // Message Recipient operations
    fun addMessageRecipient(messageRecipient: MessageRecipientEntity) = viewModelScope.launch {
        repository.addMessageRecipient(messageRecipient)
    }

    fun updateMessageRecipient(messageRecipient: MessageRecipientEntity) = viewModelScope.launch {
        repository.updateMessageRecipient(messageRecipient)
    }

    fun getMessageRecipientsByMessageId(messageId: Int) = viewModelScope.launch {
        repository.getMessageRecipientsByMessageId(messageId)
    }

    // Timer operations
    fun addTimer(timer: TimerEntity) = viewModelScope.launch {
        repository.addTimer(timer)
    }

    suspend fun updateTimer(minutes: Int, seconds: Int, userId: Int) = withContext(Dispatchers.IO) {

        val timer = TimerEntity(
            userId = userId,
            minutes = minutes,
            seconds = seconds,
            dateCreated = java.util.Date(),
            createdBy = "User",
            dateModified = java.util.Date(),
            modifiedBy = "User"
        )

        Log.d("Created timer", "Minues: ${timer.minutes}\n timer: ${timer}")

        repository.updateTimer(timer)
    }

    fun insertOrUpdateTimer(timer: TimerEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertOrUpdateTimer(timer)
    }

    fun getTimersByUserId(userId: Int) = viewModelScope.launch {
        repository.getTimersByUserId(userId)
    }

    suspend fun getTimersByIdForUser(userId: Int): List<TimerEntity> = withContext(Dispatchers.IO) {
        repository.getTimersByUserId(userId)
    }

    // App Settings operations
    fun addAppSettings(appSettings: AppSettingsEntity) = viewModelScope.launch {
        repository.addAppSettings(appSettings)
    }

    fun updateAppSettings(appSettings: AppSettingsEntity) = viewModelScope.launch {
        repository.updateAppSettings(appSettings)
    }

    fun getAppSettingsByProfileId(profileId: Int) = viewModelScope.launch {
        repository.getAppSettingsByProfileId(profileId)
    }

    suspend fun saveVideoCapture(videoCapture: VideoCapture) {
        repository.addVideoCapture(videoCapture)
    }
}