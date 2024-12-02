package com.example.proyectofinal.cruds

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.proyectofinal.data.Reminder

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: Reminder): Long

    @Update
    suspend fun update(reminder: Reminder): Int

    @Delete
    suspend fun delete(reminder: Reminder): Int

    @Query("SELECT * FROM reminders WHERE noteId = :noteId ORDER BY dueDate ASC, dueTime ASC")
    fun getRemindersForNote(noteId: Int): LiveData<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderById(id: Int): Reminder?

    @Query("SELECT * FROM reminders")
    fun getAllReminders(): LiveData<List<Reminder>>

}

