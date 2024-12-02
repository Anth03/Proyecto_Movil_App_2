package com.example.proyectofinal.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object MultimediaFileManager {
    // Guardar una imagen en almacenamiento interno
    fun saveImageToInternalStorage(context: Context, bitmap: Bitmap, filename: String, format: Bitmap.CompressFormat): String? {
        val directory = context.filesDir // Almacenamiento interno
        val file = File(directory, filename)

        try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(format, 100, outputStream) // Guardar con calidad máxima
                outputStream.flush()
            }
            Log.d("MultimediaFileManager", "Imagen guardada: ${file.absolutePath}")
            return file.absolutePath
        } catch (e: IOException) {
            Log.e("MultimediaFileManager", "Error al guardar la imagen: ${e.message}")
            return null
        }
    }

    // Guardar un archivo genérico (videos, audios, etc.)
    fun saveFileToInternalStorage(context: Context, data: ByteArray, filename: String): String? {
        val directory = context.filesDir // Almacenamiento interno
        val file = File(directory, filename)

        try {
            FileOutputStream(file).use { outputStream ->
                outputStream.write(data)
                outputStream.flush()
            }
            Log.d("MultimediaFileManager", "Archivo guardado: ${file.absolutePath}")
            return file.absolutePath
        } catch (e: IOException) {
            Log.e("MultimediaFileManager", "Error al guardar el archivo: ${e.message}")
            return null
        }
    }

    // Eliminar un archivo del almacenamiento interno
    fun deleteFileFromInternalStorage(context: Context, filePath: String): Boolean {
        val file = File(filePath)
        return if (file.exists()) {
            file.delete().also { success ->
                if (success) {
                    Log.d("MultimediaFileManager", "Archivo eliminado: $filePath")
                } else {
                    Log.e("MultimediaFileManager", "No se pudo eliminar el archivo: $filePath")
                }
            }
        } else {
            Log.e("MultimediaFileManager", "Archivo no encontrado: $filePath")
            false
        }
    }
}