package com.example.todoapp.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.database.AppDatabase
import com.example.todoapp.database.Contact
import com.example.todoapp.database.Profile
import com.example.todoapp.database.SOSMessage
import com.example.todoapp.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ContactViewModel(application: Application): AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = AppRepository(database.contactDao(), database.profileDao(), database.SOSMessageDao())

    val allContacts: Flow<List<Contact>> = repository.getAllContacts
    val profileInfo: Flow<List<Profile>> = repository.getProfileInfo
    val SOSMessage: Flow<List<SOSMessage>> = repository.getSOSMessage

    fun addContacts(contact: Contact) = viewModelScope.launch {
        repository.addContact(contact)
    }

    fun updateContacts(contact: Contact) = viewModelScope.launch {
        repository.updateContact(contact)
    }

    fun deleteContacts(contact: Contact) = viewModelScope.launch {
        repository.deleteContact(contact)
    }

    // CRUDS FOR PROFILE
    fun createProfile(profile: Profile) = viewModelScope.launch {
        repository.addProfile(profile)
    }

    fun updateProfile(profile: Profile) = viewModelScope.launch {
        repository.updateProfile(profile)
    }

    fun deleteProfile(profile: Profile) = viewModelScope.launch {
        repository.deleteProfile(profile)
    }

    fun createSOSMessage(sosMessage: SOSMessage) = viewModelScope.launch {
        repository.addSOSMessage(sosMessage)
    }

    fun editSOSMessage(sosMessage: SOSMessage) = viewModelScope.launch {
        repository.updateSOSMessage(sosMessage)
    }

    fun deleteSOSMessage(sosMessage: SOSMessage) = viewModelScope.launch {
        repository.deleteSOSmessage(sosMessage)
    }

}