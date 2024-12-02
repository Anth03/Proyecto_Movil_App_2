package com.example.proyectofinal.viewmodel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.proyectofinal.R

class NotificationWorker(
    context: Context, workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val noteId = inputData.getInt("noteId", -1)
        if (noteId == -1) {
            return Result.failure()
        }
        val title = inputData.getString("title") ?: "Tarea pendiente"
        val description = inputData.getString("description") ?: "Tienes una tarea pendiente."
        showNotification(title, description, noteId)
        return Result.success()
        Log.d("NotificationWorker", "Ejecutando Worker para la nota ID: $noteId")
    }

    private fun showNotification(title: String, description: String, noteId: Int) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "task_reminders"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Recordatorios de Tareas", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(noteId, notification)
    }
}
