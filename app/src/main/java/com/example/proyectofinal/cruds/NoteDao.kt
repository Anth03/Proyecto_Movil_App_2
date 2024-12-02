package com.example.proyectofinal.cruds

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.proyectofinal.data.Note

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: Note): Long

    @Update
    fun update(note: Note): Int

    @Delete
    fun delete(note: Note): Int

    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNoteById(id: Int): LiveData<Note>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteByIdSync(id: Int): Note?

    @Query("SELECT * FROM notes ORDER BY title ASC")
    fun getAllNotes(): LiveData<List<Note>>
}
