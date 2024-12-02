package com.example.proyectofinal.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.proyectofinal.cruds.MultimediaRepository
import com.example.proyectofinal.data.Note
import com.example.proyectofinal.data.NoteDatabase
import com.example.proyectofinal.cruds.NoteRepository
import com.example.proyectofinal.cruds.ReminderRepository
import com.example.proyectofinal.data.Multimedia
import com.example.proyectofinal.data.Reminder
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository
    private val reminderRepository: ReminderRepository
    private val multimediaRepository: MultimediaRepository

    val allNotes: LiveData<List<Note>>
    var allReminders: LiveData<List<Reminder>>
    val allPhotos: LiveData<List<Multimedia.Photo>>
    val allVideos: LiveData<List<Multimedia.Video>>
    val allAudios: LiveData<List<Multimedia.Audio>>

    init {
        val noteDao = NoteDatabase.getDatabase(application).noteDao()
        val reminderDao = NoteDatabase.getDatabase(application).reminderDao()
        val multimediaDao = NoteDatabase.getDatabase(application).multimediaDao()

        repository = NoteRepository(noteDao)
        reminderRepository = ReminderRepository(reminderDao)
        multimediaRepository = MultimediaRepository(multimediaDao)

        allNotes = repository.allNotes
        allReminders = reminderRepository.allReminder
        allPhotos = multimediaRepository.getAllPhotos()
        allVideos = multimediaRepository.getAllVideos()
        allAudios = multimediaRepository.getAllAudios()
    }

    //AÑADIR NOTAS
    fun insert(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        val insertedNoteId = repository.insert(note)
    }

    //AÑADIR RECORDATORIO
    fun insertReminder(reminder: Reminder): Long {
        return runBlocking {
            reminderRepository.insert(reminder)
        }
    }

    fun update(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(note)
    }

    fun delete(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(note)
        val reminders = reminderRepository.getRemindersForNote(note.id).value
        reminders?.forEach { reminder ->
            reminderRepository.delete(reminder)
        }
    }

    fun getNoteById(id: Int): LiveData<Note> {
        return repository.getNoteById(id)
    }

    fun getRemindersForNote(noteId: Int): LiveData<List<Reminder>> {
        return reminderRepository.getRemindersForNote(noteId)
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            reminderRepository.delete(reminder)
        }
    }

    // Actualizar recordatorio
    fun updateReminder(reminder: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            reminderRepository.update(reminder)
        }
    }

     // MULTIMEDIA
    //FOTOS
     fun insertPhoto(photo: Multimedia.Photo) {
         viewModelScope.launch(Dispatchers.IO) {
             multimediaRepository.insertPhoto(photo)
         }
     }

    fun getPhotosForNote(noteId: Int): LiveData<List<Multimedia.Photo>> {
        return multimediaRepository.getPhotosForNote(noteId)
    }

    fun deletePhotoById(photoId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            multimediaRepository.deletePhotoById(photoId)
        }
    }

    // VIDEOS
    fun insertVideo(video: Multimedia.Video) {
        viewModelScope.launch(Dispatchers.IO) {
            multimediaRepository.insertVideo(video)
        }
    }

    fun getVideosForNote(noteId: Int): LiveData<List<Multimedia.Video>> {
        return multimediaRepository.getVideosForNote(noteId)
    }

    fun deleteVideoById(videoId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            multimediaRepository.deleteVideoById(videoId)
        }
    }

    // Convierte fecha y hora a milisegundos
    private fun calculateDelay(date: String, time: String): Long {
        val dateFormat = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault()).apply {
            timeZone = TimeZone.getDefault() // Usar la zona horaria local
        }
        Log.d("calculateDelay", "Fecha recibida: $date, Hora recibida: $time")
        return try {
            val dueDateTime = dateFormat.parse("$date $time")?.time ?: 0L
            val currentTime = System.currentTimeMillis()
            val delay = dueDateTime - currentTime
            Log.d(
                "calculateDelay",
                "Fecha del recordatorio en ms: $dueDateTime, Tiempo actual en ms: $currentTime, Retraso en ms: $delay"
            )
            delay
        } catch (e: Exception) {
            Log.e("calculateDelay", "Error al calcular el retraso: ${e.message}")
            -1L // Usar -1 para indicar error
        }
    }

    fun scheduleNotifications(note: Note) {
        try {
            // Verificar si la tarea está completada
            if (note.isCompleted == true) {
                Log.d("scheduleNotifications", "La tarea ya está completada. No se programa ninguna notificación.")
                return
            }
            getRemindersForNote(note.id).observeForever { reminderList ->
                Log.d("scheduleNotifications", "Recordatorios obtenidos: $reminderList")
                reminderList?.forEach { reminder ->
                    val delayMillis = calculateDelay(reminder.dueDate, reminder.dueTime)
                    Log.d("scheduleNotifications", "Retraso calculado para el recordatorio: $delayMillis ms")

                    if (delayMillis > 0) {
                        val inputData = workDataOf(
                            "noteId" to note.id,
                            "title" to note.title,
                            "description" to note.description
                        )
                        val notificationWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                            .setInputData(inputData)
                            .build()

                        WorkManager.getInstance(getApplication()).enqueue(notificationWorkRequest)
                        Log.d("scheduleNotifications", "Notificación programada con ID: ${notificationWorkRequest.id}")
                    } else {
                        Log.d("scheduleNotifications", "El recordatorio está en el pasado, no se programa.")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("scheduleNotifications", "Error al programar notificaciones: ${e.message}")
        }
    }
}
