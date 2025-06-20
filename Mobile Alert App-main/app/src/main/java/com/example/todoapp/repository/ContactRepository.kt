package com.example.todoapp.repository

import com.example.todoapp.database.Contact
import com.example.todoapp.database.AppDatabase
import com.example.todoapp.database.ContactDao
import com.example.todoapp.database.Profile
import com.example.todoapp.database.ProfileDao
import com.example.todoapp.database.SOSMessage
import com.example.todoapp.database.SOSMessageDao
import kotlinx.coroutines.flow.Flow

class AppRepository(private val contactDao: ContactDao, private val profileDao: ProfileDao, private val SOSMessagingDao: SOSMessageDao) {
    // Get all contacts
    val getAllContacts: Flow<List<Contact>> = contactDao.getAllContacts()
    // Get all profile
    val getProfileInfo: Flow<List<Profile>> = profileDao.readProfile()

    // Get all message
    val getSOSMessage: Flow<List<SOSMessage>> = SOSMessagingDao.getMessage()

    // Contact cruds
    suspend fun addContact(contact: Contact) = contactDao.addContact(contact)
    suspend fun updateContact(contact: Contact) = contactDao.updateContact(contact)
    suspend fun deleteContact(contact: Contact) = contactDao.deleteContact(contact)

    // Profile cruds
    suspend fun addProfile(profile: Profile) = profileDao.createProfile(profile)
    suspend fun updateProfile(profile: Profile) = profileDao.updateProfile(profile)
    suspend fun deleteProfile(profile: Profile) = profileDao.deleteProfile(profile)

    // SOSMessages cruds
    suspend fun addSOSMessage(sosMessage: SOSMessage) = SOSMessagingDao.createMessage(sosMessage)
    suspend fun updateSOSMessage(sosMessage: SOSMessage) = SOSMessagingDao.editMessage(sosMessage)
    suspend fun deleteSOSmessage(sosMessage: SOSMessage) = SOSMessagingDao.deleteMessage(sosMessage)
}