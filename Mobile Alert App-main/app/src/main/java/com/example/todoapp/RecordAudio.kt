package com.example.todoapp

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordAudio : Service() {
    companion object {
        private const val TAG = "RecordAudioService"
        const val AUDIO_FILE_PATH_EXTRA = "audio_file_path"
    }

    private var mediaRecorder: MediaRecorder? = null
    private var audioFilePath: String? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Started audio recording service")

        // Check if we have permission to record audio
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "No permission to record audio")
            return START_NOT_STICKY
        }

        startRecording()

        return START_STICKY
    }

//    private fun startRecording() {
//        try {
//            // Create directory for storing audio files if it doesn't exist
//            val directory = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "EmergencyRecordings")
//            if (!directory.exists()) {
//                directory.mkdirs()
//            }
//
//            // Create a unique file name based on timestamp
//            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//            val audioFile = File(directory, "emergency_audio_$timestamp.3gp")
//            audioFilePath = audioFile.absolutePath
//
//            Log.d(TAG, "Audio will be saved to: $audioFilePath")
//
//            // Initialize MediaRecorder
//            mediaRecorder = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
//                MediaRecorder(this)
//            } else {
//                @Suppress("DEPRECATION")
//                MediaRecorder()
//            }
//
//            mediaRecorder?.apply {
//                setAudioSource(MediaRecorder.AudioSource.MIC)
//                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
//                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
//                setOutputFile(audioFilePath)
//
//                try {
//                    prepare()
//                    start()
//                    Log.d(TAG, "Recording started successfully")
//                } catch (e: IOException) {
//                    Log.e(TAG, "Recording failed to start: ${e.message}")
//                    e.printStackTrace()
//                }
//            }
//        } catch (e: Exception) {
//            Log.e(TAG, "Error starting recording: ${e.message}")
//            e.printStackTrace()
//        }
//    }



    private fun startRecording() {
        try {
            // Create directory for storing audio files if it doesn't exist
            val directory = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "EmergencyRecordings")
            if (!directory.exists()) {
                directory.mkdirs()
            }

            // Create a unique file name based on timestamp - CHANGED: .3gp to .mp4
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val audioFile = File(directory, "emergency_audio_$timestamp.mp4")
            audioFilePath = audioFile.absolutePath

            Log.d(TAG, "Audio will be saved to: $audioFilePath")

            // Initialize MediaRecorder - SAME as your existing code
            mediaRecorder = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                MediaRecorder(this)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)

                // CHANGED: Use MPEG_4 instead of THREE_GPP
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)

                // CHANGED: Use AAC instead of AMR_NB
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

                // ADDED: For web compatibility
                setAudioSamplingRate(44100)
                setAudioEncodingBitRate(128000)

                setOutputFile(audioFilePath)

                try {
                    prepare()
                    start()
                    Log.d(TAG, "Recording started successfully")
                } catch (e: IOException) {
                    Log.e(TAG, "Recording failed to start: ${e.message}")
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting recording: ${e.message}")
            e.printStackTrace()
        }
    }



    private fun stopRecording(): String? {
        try {
            mediaRecorder?.apply {
                try {
                    stop()
                    Log.d(TAG, "Recording stopped successfully")
                } catch (e: RuntimeException) {
                    // This can happen if recording is stopped too soon
                    Log.e(TAG, "Failed to stop recording: ${e.message}")
                }
                release()
            }
            mediaRecorder = null

            // Return the path of the recorded audio file
            Log.d(TAG, "Audio file saved at: $audioFilePath")
            return audioFilePath
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping recording: ${e.message}")
            e.printStackTrace()
            return null
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        val audioPath = stopRecording()

        // Broadcast that recording has stopped and provide the file path
        val intent = Intent("com.example.todoapp.RECORDING_COMPLETED")
        intent.putExtra(AUDIO_FILE_PATH_EXTRA, audioPath)
        sendBroadcast(intent)

        Log.d(TAG, "Stopped audio recording service")
    }
}