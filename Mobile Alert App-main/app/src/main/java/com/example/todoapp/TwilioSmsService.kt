package com.example.todoapp


import android.util.Log
import com.twilio.Twilio
import com.twilio.rest.api.v2010.account.Message
import com.twilio.type.PhoneNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SmsService {
    companion object {
        private const val TAG = "SmsService"
        private const val ACCOUNT_SID = "YOUR_ACCOUNT_SID" // Replace with your Twilio Account SID
        private const val AUTH_TOKEN = "YOUR_AUTH_TOKEN"   // Replace with your Twilio Auth Token
        private const val FROM_NUMBER = "+15017250604"     // Replace with your Twilio phone number
    }

    /**
     * Sends an SMS message via Twilio
     * Must be called from a background thread or coroutine
     */
    suspend fun sendSms(toNumber: String, messageBody: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // Initialize Twilio client
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN)

            // Send message
            val message = Message.creator(
                PhoneNumber(toNumber),
                PhoneNumber(FROM_NUMBER),
                messageBody
            ).create()

            Log.d(TAG, "SMS sent successfully with SID: ${message.getSid()}")
            return@withContext true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send SMS: ${e.message}", e)
            return@withContext false
        }
    }

    /**
     * Sends emergency SOS messages to multiple contacts
     */
    suspend fun sendEmergencySms(phoneNumbers: List<String>, messageBody: String): List<Boolean> {
        return phoneNumbers.map { phoneNumber ->
            sendSms(phoneNumber, messageBody)
        }
    }
}