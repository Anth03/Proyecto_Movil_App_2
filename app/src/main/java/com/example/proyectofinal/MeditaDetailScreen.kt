package com.example.proyectofinal.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.proyectofinal.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeditaDetailScreen(
    navController: NavHostController,
    photoPath: String, // Ruta de la foto
    viewModel: NoteViewModel,
    photoId: Int // ID único de la foto para eliminarla
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de la Foto", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
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
            // Mostrar foto
            Image(
                painter = rememberAsyncImagePainter(model = photoPath),
                contentDescription = "Foto",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
            )

            // Botón para eliminar la foto
            Button(
                onClick = {
                    // Eliminar la foto desde la base de datos y posiblemente del almacenamiento interno
                    viewModel.deletePhotoById(photoId)
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
