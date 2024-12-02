package com.example.proyectofinal

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectofinal.viewmodel.NoteViewModel
import androidx.compose.runtime.livedata.observeAsState
import com.example.proyectofinal.ui.NoteDetailScreen
import com.example.proyectofinal.ui.MeditaDetailScreen
import com.example.proyectofinal.ui.VideoDetailScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    viewModel: NoteViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = "noteList", modifier = modifier) {
        composable("noteList") {
            NoteListScreen(navController = navController, viewModel = viewModel)
        }
        composable("noteDetail/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()

            if (noteId != null) {
                NoteDetailScreen(navController = navController, viewModel = viewModel)
            }
        }
        composable("editNote/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()
            val note = noteId?.let { viewModel.getNoteById(it).observeAsState().value }
            if (note != null) {
                NoteEditScreen(navController = navController, viewModel = viewModel, note = note)
            }
        }
        composable("mediaDetail/{photoPath}/{photoId}") { backStackEntry ->
            val encodedPhotoPath = backStackEntry.arguments?.getString("photoPath")
            val photoId = backStackEntry.arguments?.getString("photoId")?.toIntOrNull()

            val photoPath = encodedPhotoPath?.let { Uri.decode(it) }

            if (photoPath != null && photoId != null) {
                MeditaDetailScreen(
                    navController = navController,
                    photoPath = photoPath,
                    viewModel = viewModel,
                    photoId = photoId
                )
            }
        }
        composable("videoDetail/{videoPath}/{videoId}") { backStackEntry ->
            // Obtener los argumentos desde la URL
            val encodedVideoPath = backStackEntry.arguments?.getString("videoPath")
            val videoId = backStackEntry.arguments?.getString("videoId")?.toIntOrNull()

            // Decodificar la ruta del video
            val videoPath = encodedVideoPath?.let { Uri.decode(it) }

            if (videoPath != null && videoId != null) {
                VideoDetailScreen(
                    navController = navController,
                    videoPath = videoPath,
                    viewModel = viewModel,
                    videoId = videoId
                )
            }
        }
    }
}
