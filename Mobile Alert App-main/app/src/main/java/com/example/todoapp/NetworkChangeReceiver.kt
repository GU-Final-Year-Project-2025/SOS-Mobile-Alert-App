package com.example.todoapp
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat

class NetworkChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == "android.net.conn.CONNECTIVITY_CHANGE") {
            Log.d("NetworkChangeReceiver", "Network connectivity changed!")
            Toast.makeText(context, "Network connectivity changed!", Toast.LENGTH_SHORT).show()

            // Send a notification
            sendNotification(context, "Network Connectivity", "Network connectivity has changed!")
        }
    }

    private fun sendNotification(context: Context, title: String, message: String) {
        val channelId = "network_change_channel"
        val notificationId = 1

        // Create a Notification Manager
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a Notification Channel for Android 8.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Network Change Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for network connectivity changes"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Show the notification
        notificationManager.notify(notificationId, notification)
    }
}
