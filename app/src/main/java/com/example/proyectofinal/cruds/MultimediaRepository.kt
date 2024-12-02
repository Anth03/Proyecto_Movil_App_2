package com.example.proyectofinal.cruds

import androidx.lifecycle.LiveData
import com.example.proyectofinal.data.Multimedia

class MultimediaRepository(private val multimediaDao: MultimediaDao) {

    // PHOTO
    suspend fun insertPhoto(photo: Multimedia.Photo): Long {
        return multimediaDao.insertPhoto(photo)
    }

    suspend fun deletePhotoById(photoId: Int) {
        multimediaDao.deletePhotoById(photoId)
    }

    fun getPhotosForNote(noteId: Int): LiveData<List<Multimedia.Photo>> {
        return multimediaDao.getPhotosForNote(noteId)
    }

    fun getAllPhotos(): LiveData<List<Multimedia.Photo>> {
        return multimediaDao.getAllPhotos()
    }

    // VIDEO
    suspend fun insertVideo(video: Multimedia.Video): Long {
        return multimediaDao.insertVideo(video)
    }

    suspend fun deleteVideoById(videoId: Int) {
        multimediaDao.deleteVideoById(videoId)
    }

    fun getVideosForNote(noteId: Int): LiveData<List<Multimedia.Video>> {
        return multimediaDao.getVideosForNote(noteId)
    }

    fun getAllVideos(): LiveData<List<Multimedia.Video>> {
        return multimediaDao.getAllVideos()
    }

    // AUDIO
    suspend fun insertAudio(audio: Multimedia.Audio): Long {
        return multimediaDao.insertAudio(audio)
    }

    suspend fun updateAudio(audio: Multimedia.Audio) {
        multimediaDao.updateAudio(audio)
    }

    suspend fun deleteAudio(audio: Multimedia.Audio) {
        multimediaDao.deleteAudio(audio)
    }

    fun getAudiosForNote(noteId: Int): LiveData<List<Multimedia.Audio>> {
        return multimediaDao.getAudiosForNote(noteId)
    }

    fun getAllAudios(): LiveData<List<Multimedia.Audio>> {
        return multimediaDao.getAllAudios()
    }

    // DELETE MULTIMEDIA ASSOCIATED WITH A NOTE
    suspend fun deleteAllMultimediaForNote(noteId: Int) {
        multimediaDao.deletePhotosForNote(noteId)
        multimediaDao.deleteVideosForNote(noteId)
        multimediaDao.deleteAudiosForNote(noteId)
    }
}
