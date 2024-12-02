package com.example.proyectofinal

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.util.Log
import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.proyectofinal.data.Multimedia
import com.example.proyectofinal.viewmodel.NoteViewModel
import com.example.proyectofinal.data.Note
import com.example.proyectofinal.data.Reminder
import com.example.proyectofinal.viewmodel.MultimediaFileManager
import kotlinx.coroutines.*
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(navController: NavHostController, viewModel: NoteViewModel, note: Note) {
    var title by remember { mutableStateOf(note.title) }
    var description by remember { mutableStateOf(note.description) }
    var classification by remember { mutableStateOf(note.classification) }
    var isCompleted by remember { mutableStateOf(note.isCompleted) }
    var dueDate by remember { mutableStateOf("") }
    var dueTime by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) } // Estado para grabación de audio

    // Recordatorios y selección
    var selectedReminder by remember { mutableStateOf<Reminder?>(null) }
    val reminders = viewModel.getRemindersForNote(note.id).observeAsState(emptyList())

    // Contexto y formatos de fecha/hora
    val context = LocalContext.current
    val activity = LocalContext.current as ComponentActivity
    val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    // Estados para listas de multimedia (se conectarían con el backend en el futuro)
    val photos = remember { mutableStateListOf<String>() }
    val videos = remember { mutableStateListOf<String>() }
    val audios = remember { mutableStateListOf<String>() }

    //PERMISOS
    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Si se concede el permiso, abre la cámara
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            context.startActivity(takePictureIntent)
        } else {
            // Si se deniega el permiso, muestra un mensaje
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_LONG).show()
        }
    }

    val requestStoragePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(context, "Permiso de almacenamiento denegado", Toast.LENGTH_LONG).show()
        }
    }

    val selectPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            Log.d("SelectPhoto", "Foto seleccionada: $uri")
            Toast.makeText(context, "Foto seleccionada: $uri", Toast.LENGTH_SHORT).show()
        } else {
            Log.d("SelectPhoto", "No se seleccionó ninguna foto")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                actions = {
                    Text(
                        text = "Guardar",
                        color = Color(0xff649562),
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable {
                                // Guardar la nota
                                val updatedNote = note.copy(
                                    title = title,
                                    description = description,
                                    classification = classification,
                                    isCompleted = isCompleted
                                )
                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.update(updatedNote)
                                    withContext(Dispatchers.Main) {
                                        navController.navigateUp()
                                    }
                                }
                            },
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    Text(
                        text = "Cancelar",
                        color = Color.Gray,
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { navController.navigateUp() },
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Campos de texto para título y descripción
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nombre") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                if (classification == "TASK") {
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = {
                        val calendar = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                calendar.set(year, month, dayOfMonth)
                                dueDate = dateFormat.format(calendar.time)
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }) {
                        Text(text = if (dueDate.isEmpty()) "Fecha" else "Fecha: $dueDate")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = {
                        val calendar = Calendar.getInstance()
                        TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                calendar.set(Calendar.MINUTE, minute)
                                dueTime = timeFormat.format(calendar.time)
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        ).show()
                    }) {
                        Text(text = if (dueTime.isEmpty()) "Hora" else "Hora: $dueTime")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = {
                        if (selectedReminder != null) {
                            // Actualizar recordatorio
                            val updatedReminder = selectedReminder!!.copy(
                                dueDate = dueDate,
                                dueTime = dueTime
                            )
                            viewModel.updateReminder(updatedReminder)
                        } else {
                            // Agregar nuevo recordatorio
                            val newReminder = Reminder(
                                noteId = note.id,
                                dueDate = dueDate,
                                dueTime = dueTime
                            )
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.insertReminder(newReminder)
                                withContext(Dispatchers.Main) {
                                    viewModel.scheduleNotifications(note)
                                }
                            }
                        }
                        dueDate = ""
                        dueTime = ""
                        selectedReminder = null
                    }) {
                        Text(text = "Guardar Recordatorio")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isCompleted,
                            onCheckedChange = { isCompleted = it }
                        )
                        Text(text = "Marcar como completada", modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            // Lista de recordatorios (elementos dinámicos)
            items(reminders.value) { reminder ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text("Fecha: ${reminder.dueDate} Hora: ${reminder.dueTime}")
                    Row {
                        Button(onClick = {
                            selectedReminder = reminder
                            dueDate = reminder.dueDate
                            dueTime = reminder.dueTime
                        }) {
                            Text("Editar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = { viewModel.deleteReminder(reminder) }) {
                            Text("Eliminar")
                        }
                    }
                }
            }

            // MULTIMEDIA FOTOS
            item {
                Text(text = "Foto", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                // Obteniendo fotos desde la base de datos
                val photos by viewModel.getPhotosForNote(note.id).observeAsState(emptyList())
                // Mostrar lista de fotos
                LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
                    items(photos) { photo ->
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .padding(4.dp)
                                .clickable {
                                    val encodedPhotoPath = Uri.encode(photo.photoPath)
                                    navController.navigate("mediaDetail/$encodedPhotoPath/${photo.id}")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = photo.photoPath,
                                contentDescription = "Foto",
                                modifier = Modifier.size(100.dp)
                            )
                        }
                    }
                }

                // Lanzador para tomar foto
                val cameraLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.TakePicturePreview()
                ) { bitmap ->
                    if (bitmap != null) {
                        val fileName = "photo_${System.currentTimeMillis()}.jpg"
                        val filePath = MultimediaFileManager.saveImageToInternalStorage(
                            context = context,
                            bitmap = bitmap,
                            filename = fileName,
                            format = Bitmap.CompressFormat.JPEG
                        )

                        if (filePath != null) {
                            val newPhoto = Multimedia.Photo(
                                noteId = note.id,
                                photoPath = filePath
                            )
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.insertPhoto(newPhoto)
                            }
                            Toast.makeText(context, "Foto guardada", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Error al guardar la foto", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // Lanzador para cargar foto desde galería
                val galleryLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri ->
                    if (uri != null) {
                        val inputStream = context.contentResolver.openInputStream(uri)
                        val byteArray = inputStream?.readBytes()
                        inputStream?.close()

                        if (byteArray != null) {
                            val fileName = "photo_${System.currentTimeMillis()}.jpg"
                            val filePath = MultimediaFileManager.saveFileToInternalStorage(
                                context = context,
                                data = byteArray,
                                filename = fileName
                            )

                            if (filePath != null) {
                                val newPhoto = Multimedia.Photo(
                                    noteId = note.id,
                                    photoPath = filePath
                                )
                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.insertPhoto(newPhoto)
                                }
                                Toast.makeText(context, "Foto cargada y guardada", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Error al guardar la foto", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            cameraLauncher.launch(null)
                        } else {
                            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xff436588)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(text = "Tomar Foto", color = Color(0xffffffff))
                }

                Button(
                    onClick = {
                        galleryLauncher.launch("image/*")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                ) {
                    Text(text = "Cargar Foto", color = Color.White)
                }
            }

            // Multimedia: Video
            item {
                Text(text = "Video", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                // Obteniendo videos desde la base de datos
                val videos by viewModel.getVideosForNote(note.id).observeAsState(emptyList())

                LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
                    items(videos) { video ->
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .padding(4.dp)
                                .clickable {
                                    val encodedVideoPath = Uri.encode(video.videoPath)
                                    navController.navigate("videoDetail/$encodedVideoPath/${video.id}")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Video", color = Color.White)
                        }
                    }
                }

                // Botón para grabar video
                Button(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            // Permiso ya concedido, abrir la cámara en modo video
                            val recordVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                            context.startActivity(recordVideoIntent)
                        } else {
                            // Solicitar permiso dinámicamente
                            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xff436588)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(text = "Grabar Video", color = Color(0xffffffff))
                }

                // Lanzador para seleccionar un video de la galería
                val selectVideoLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri ->
                    if (uri != null) {
                        Log.d("SelectVideo", "Video seleccionado: $uri")
                        Toast.makeText(context, "Video seleccionado: $uri", Toast.LENGTH_SHORT).show()

                        val filePath = MultimediaFileManager.saveFileToInternalStorage(
                            context = context,
                            data = uri.toString().toByteArray(),
                            filename = "video_${System.currentTimeMillis()}.mp4"
                        )

                        if (filePath != null) {
                            val newVideo = Multimedia.Video(noteId = note.id, videoPath = filePath)
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.insertVideo(newVideo)
                            }
                        }
                    } else {
                        Log.d("SelectVideo", "No se seleccionó ningún video")
                    }
                }

                // Botón para cargar video desde la galería
                Button(
                    onClick = {
                        selectVideoLauncher.launch("video/*")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                ) {
                    Text(text = "Cargar Video", color = Color.White)
                }
            }

            // Multimedia: Audio
            item {
                Text(text = "Audio", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                // Mostrar lista de audios
                LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
                    items(audios) { audio ->
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .padding(4.dp)
                                .background(Color.Gray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Audio", color = Color.White)
                        }
                    }
                }

                // Botón para grabar audio
                Button(
                    onClick = {
                        isRecording = !isRecording
                        if (isRecording) {
                            // Iniciar grabación
                            Log.d("AudioRecording", "Grabación iniciada")
                            Toast.makeText(context, "Grabando audio...", Toast.LENGTH_SHORT).show()
                        } else {
                            // Detener grabación
                            Log.d("AudioRecording", "Grabación detenida")
                            Toast.makeText(context, "Grabación finalizada", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xff436588)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = if (isRecording) "Detener Grabación" else "Grabar Audio",
                        color = Color(0xffffffff)
                    )
                }

                // Lanzador para cargar un archivo de audio desde la galería
                val selectAudioLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri ->
                    if (uri != null) {
                        Log.d("SelectAudio", "Audio seleccionado: $uri")
                        Toast.makeText(context, "Audio seleccionado: $uri", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d("SelectAudio", "No se seleccionó ningún audio")
                    }
                }

                // Botón para cargar audio
                Button(
                    onClick = {
                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU) {
                            // Android 12 o inferior requiere permiso de almacenamiento
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                selectAudioLauncher.launch("audio/*")
                            } else {
                                requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                        } else {
                            // Android 13 o superior, no se requiere permiso
                            selectAudioLauncher.launch("audio/*")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                ) {
                    Text(text = "Cargar Audio", color = Color.White)
                }
            }

            // Checkbox y botón de eliminar nota
            item {
                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.delete(note)
                            withContext(Dispatchers.Main) {
                                navController.navigateUp()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(text = "Eliminar", color = Color.White)
                }
            }
        }
    }
}
