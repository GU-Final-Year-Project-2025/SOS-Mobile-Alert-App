package com.example.todoapp

import android.Manifest
import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.telephony.SmsManager
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

class EmergencyAccessibilityService : AccessibilityService() {
    private val TAG = "EmergencyService"
    private val pressTimeWindow = 1000L // 1 second window for consecutive presses
    private val requiredPresses = 3
    private var lastPressTime = 0L
    private var pressCount = 0

    // REPLACE THESE WITH VALUES FROM YOUR DATABASE
    private val emergencyContacts = listOf("1234567890", "0987654321") // List of emergency contacts
    private val emergencyMessage = "EMERGENCY! I need help. This is an automated message."
    private val countdownSeconds = 5 // Countdown time in seconds

    // Location tracking
    private var locationManager: LocationManager? = null
    private var currentLocation: Location? = null
    private var countDownTimer: CountDownTimer? = null
    private var isCountdownActive = false

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // Not needed for key events
    }

    override fun onInterrupt() {
        // Not needed for this implementation
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        initializeLocationTracking()

        // Register for emergency cancel broadcast
//        registerReceiver(
//            emergencyCancelReceiver,
//            IntentFilter("com.example.todoapp.CANCEL_EMERGENCY")
//        )
        // In your EmergencyAccessibilityService class, modify the registerReceiver call
        registerReceiver(
            emergencyCancelReceiver,
            IntentFilter("com.example.todoapp.CANCEL_EMERGENCY"),
            Context.RECEIVER_NOT_EXPORTED // Add this flag
        )
    }

    private fun initializeLocationTracking() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            try {
                locationManager?.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000L, // 5 seconds
                    10f, // 10 meters
                    locationListener
                )

                // Get last known location as a fallback
                currentLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize location tracking: ${e.message}")
            }
        }
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            currentLocation = location
            Log.d(TAG, "Location updated: ${location.latitude}, ${location.longitude}")
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        // Skip processing if countdown is already active
        if (isCountdownActive) return true

        Log.d(TAG, "Key event: ${event.keyCode}, action: ${event.action}")

        // Check if it's the power button (KEYCODE_POWER) and the key is being pressed down
        if (event.keyCode == KeyEvent.KEYCODE_POWER && event.action == KeyEvent.ACTION_DOWN) {
            val currentTime = System.currentTimeMillis()
            val timeSinceLastPress = currentTime - lastPressTime

            Log.d(TAG, "Power button pressed. Time since last press: $timeSinceLastPress ms")

            if (currentTime - lastPressTime <= pressTimeWindow) {
                pressCount++
                Log.d(TAG, "Press within time window. Press count: $pressCount")

                if (pressCount >= requiredPresses) {
                    Log.d(TAG, "Required press count reached! Starting emergency countdown.")
                    // Reset counter
                    pressCount = 0
                    // Start emergency procedure with countdown
                    startEmergencyCountdown()
                    return true // Consume the event
                }
            } else {
                // Reset counter if time window exceeded
                Log.d(TAG, "Time window exceeded. Resetting press count to 1")
                pressCount = 1
            }
            lastPressTime = currentTime
        }

        return super.onKeyEvent(event) // Let system handle the event normally
    }

    private fun startEmergencyCountdown() {
        isCountdownActive = true

        // Launch Compose-based countdown activity
        val intent = Intent(this, EmergencyCountdownActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("countdown_seconds", countdownSeconds)
        }

        startActivity(intent)

        // Start countdown
        countDownTimer = object : CountDownTimer(countdownSeconds * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Update is handled by the Compose activity
                Log.d(TAG, "Countdown: ${millisUntilFinished / 1000} seconds remaining")
            }

            override fun onFinish() {
                sendEmergencySMS()
                isCountdownActive = false
            }
        }.start()
    }

    private val emergencyCancelReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "com.example.todoapp.CANCEL_EMERGENCY") {
                Log.d(TAG, "Emergency canceled by user")
                countDownTimer?.cancel()
                isCountdownActive = false
                Toast.makeText(context, "Emergency alert canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendEmergencySMS() {
        try {
            Log.d(TAG, "Sending emergency SMS to ${emergencyContacts.size} contacts")
            val smsManager = SmsManager.getDefault()

            // Build message with location if available
            var fullMessage = emergencyMessage
            currentLocation?.let { location ->
                fullMessage += "\n\nMy current location: " +
                        "https://maps.google.com/?q=${location.latitude},${location.longitude}"
            }

            // Send to all emergency contacts
            for (contact in emergencyContacts) {
                smsManager.sendTextMessage(contact, null, fullMessage, null, null)
                Log.d(TAG, "Emergency SMS sent to $contact")
            }

            // Notify user
            Toast.makeText(
                this,
                "Emergency alert sent to ${emergencyContacts.size} contacts",
                Toast.LENGTH_LONG
            ).show()

        } catch (e: Exception) {
            Log.e(TAG, "Failed to send emergency SMS: ${e.message}")
            Toast.makeText(
                this,
                "Failed to send emergency message: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager?.removeUpdates(locationListener)
        countDownTimer?.cancel()
        try {
            unregisterReceiver(emergencyCancelReceiver)
        } catch (e: Exception) {
            // Ignore if not registered
        }
    }
}