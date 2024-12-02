package com.example.proyectofinal

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.proyectofinal.ui.theme.ContactAppTheme
import com.example.proyectofinal.viewmodel.NoteViewModel
import java.util.*

class MainActivity : ComponentActivity() {

    private val viewModel: NoteViewModel by viewModels()

    // Registrar el lanzador para el permiso de notificaciones
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Si el permiso es concedido, configura las notificaciones
            setupNotifications()
        } else {
            // Si el permiso es denegado, muestra un mensaje
            Toast.makeText(
                this,
                "El permiso de notificaciones es necesario para recibir alertas.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDefaultLocale(this)

        // Comprobar y solicitar permiso de notificaciones si es necesario
        checkAndRequestNotificationPermission()

        setContent {
            ContactAppTheme {
                MainContent(viewModel)
            }
        }
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Para Android 13 o superior, verificar y solicitar permiso
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permiso concedido
                setupNotifications()
            } else {
                // Solicitar permiso
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // Para Android 12 o inferior, configurar notificaciones directamente
            setupNotifications()
        }
    }

    private fun setupNotifications() {
        // Crear el canal de notificaciones
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "your_channel_id"
            val channelName = "Recordatorio"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Canal para mostrar recordatorios"
            }

            // Registrar el canal con el sistema
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setDefaultLocale(context: Context) {
        val defaultLanguage = "es"
        val currentLanguage = Locale.getDefault().language

        if (currentLanguage != "es" && currentLanguage != "en") {
            val locale = Locale(defaultLanguage)
            Locale.setDefault(locale)
            val config = Configuration()
            config.setLocale(locale)
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
    }
}

@Composable
fun MainContent(viewModel: NoteViewModel) {
    val navController = rememberNavController()

    Scaffold { innerPadding ->
        AppNavigation(
            navController = navController,
            viewModel = viewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
