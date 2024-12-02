package com.example.proyectofinal.ui

import android.net.Uri
import android.widget.VideoView
import android.widget.MediaController
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.example.proyectofinal.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDetailScreen(
    navController: NavHostController,
    videoPath: String, // Ruta del video
    viewModel: NoteViewModel,
    videoId: Int // ID único del video para eliminarlo
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Video", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    Text(
                        text = "Atrás",
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { navController.navigateUp() },
                        fontSize = 16.sp
                    )
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Mostrar el video usando VideoView
            AndroidView(
                factory = { context ->
                    VideoView(context).apply {
                        val uri = Uri.parse(videoPath) // Usar la ruta del video
                        setVideoURI(uri)

                        // Agregar control de medios (play, pause, etc.)
                        val mediaController = MediaController(context)
                        mediaController.setAnchorView(this)
                        setMediaController(mediaController)

                        // Iniciar la reproducción del video automáticamente
                        start()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
            )

            // Botón para eliminar el video
            Button(
                onClick = {
                    // Eliminar el video desde la base de datos y posiblemente del almacenamiento interno
                    viewModel.deleteVideoById(videoId)
                    navController.navigateUp()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(text = "Eliminar", color = Color.White)
            }
        }
    }
}