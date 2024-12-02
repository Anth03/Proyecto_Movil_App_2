package com.example.proyectofinal.cruds

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.proyectofinal.data.Multimedia
import com.example.proyectofinal.data.Note

@Dao
interface MultimediaDao {

    // FOTOS

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: Multimedia.Photo): Long

    @Update
    suspend fun updatePhoto(photo: Multimedia.Photo)

    @Query("DELETE FROM photos WHERE id = :photoId")
    suspend fun deletePhotoById(photoId: Int)

    @Query("SELECT * FROM photos WHERE noteId = :noteId")
    fun getPhotosForNote(noteId: Int): LiveData<List<Multimedia.Photo>>

    @Query("DELETE FROM photos WHERE noteId = :noteId")
    suspend fun deletePhotosForNote(noteId: Int)

    @Query("SELECT * FROM photos")
    fun getAllPhotos(): LiveData<List<Multimedia.Photo>>


    // VIDEOS

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: Multimedia.Video): Long

    @Update
    suspend fun updateVideo(video: Multimedia.Video)

    @Query("DELETE FROM videos WHERE id = :videoId")
    suspend fun deleteVideoById(videoId: Int)

    @Query("SELECT * FROM videos WHERE noteId = :noteId")
    fun getVideosForNote(noteId: Int): LiveData<List<Multimedia.Video>>

    @Query("DELETE FROM videos WHERE noteId = :noteId")
    suspend fun deleteVideosForNote(noteId: Int)

    @Query("SELECT * FROM videos")
    fun getAllVideos(): LiveData<List<Multimedia.Video>>

    // AUDIOS

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudio(audio: Multimedia.Audio): Long

    @Update
    suspend fun updateAudio(audio: Multimedia.Audio)

    @Delete
    suspend fun deleteAudio(audio: Multimedia.Audio)

    @Query("SELECT * FROM audios WHERE noteId = :noteId")
    fun getAudiosForNote(noteId: Int): LiveData<List<Multimedia.Audio>>

    @Query("DELETE FROM audios WHERE noteId = :noteId")
    suspend fun deleteAudiosForNote(noteId: Int)

    @Query("SELECT * FROM audios")
    fun getAllAudios(): LiveData<List<Multimedia.Audio>>
}

