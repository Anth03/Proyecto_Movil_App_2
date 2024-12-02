package com.example.proyectofinal.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

class Multimedia {

    @Entity(
        tableName = "photos",
        foreignKeys = [ForeignKey(
            entity = Note::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        )]
    )
    data class Photo(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val noteId: Int,
        val photoPath: String
    )

    @Entity(
        tableName = "videos",
        foreignKeys = [ForeignKey(
            entity = Note::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        )]
    )
    data class Video(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val noteId: Int,
        val videoPath: String
    )

    @Entity(
        tableName = "audios",
        foreignKeys = [ForeignKey(
            entity = Note::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        )]
    )
    data class Audio(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val noteId: Int,
        val audioPath: String
    )
}
