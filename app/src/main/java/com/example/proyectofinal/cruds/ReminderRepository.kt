package com.example.proyectofinal.cruds

import androidx.lifecycle.LiveData
import com.example.proyectofinal.data.Reminder

class ReminderRepository(private val reminderDao: ReminderDao) {
    val allReminder: LiveData<List<Reminder>> = reminderDao.getAllReminders()

    suspend fun insert(reminder: Reminder): Long {
        return reminderDao.insert(reminder)
    }

    suspend fun update(reminder: Reminder): Int {
        return reminderDao.update(reminder)
    }

    suspend fun delete(reminder: Reminder): Int {
        return reminderDao.delete(reminder)
    }

    fun getRemindersForNote(noteId: Int): LiveData<List<Reminder>> {
        return reminderDao.getRemindersForNote(noteId)
    }

    suspend fun getReminderById(id: Int): Reminder? {
        return reminderDao.getReminderById(id)
    }
}


