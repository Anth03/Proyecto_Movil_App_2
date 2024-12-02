package com.example.proyectofinal.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.proyectofinal.cruds.NoteDao
import com.example.proyectofinal.cruds.ReminderDao
import com.example.proyectofinal.cruds.MultimediaDao

@Database(
    entities = [
        Note::class,
        Reminder::class,
        Multimedia.Photo::class,
        Multimedia.Video::class,
        Multimedia.Audio::class
    ],
    version = 5, // Cambia el número de versión al modificar la estructura
    exportSchema = false
)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun reminderDao(): ReminderDao
    abstract fun multimediaDao(): MultimediaDao

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
