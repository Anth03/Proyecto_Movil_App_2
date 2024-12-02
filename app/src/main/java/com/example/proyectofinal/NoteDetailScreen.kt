package com.example.proyectofinal.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.navigation.NavHostController
import com.example.proyectofinal.data.Note
import com.example.proyectofinal.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(navController: NavHostController, viewModel: NoteViewModel) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var classification by remember { mutableStateOf("NOTE") }

    val saveNote = {
        // Crear la nueva nota con los datos actuales
        val newNote = Note(
            id = 0,  // Este valor será 0 porque es una nueva nota
            title = title,
            description = description,
            classification = classification,
            isCompleted = false
        )

        // Insertar la nueva nota
        viewModel.insert(newNote)

        // Navegar hacia atrás después de guardar
        navController.navigateUp()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Añadir Nota/Tarea", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                actions = {
                    Text(
                        text = "Guardar",
                        color = Color(0xff649562),
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { saveNote() },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Campo de título
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 8.dp)
            )

            // Campo de descripción
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()  // Asegura que el Row ocupe todo el ancho disponible
                    .padding(16.dp), // Agregar algo de padding alrededor
                horizontalArrangement = Arrangement.spacedBy(8.dp),  // Espaciado entre los botones
                verticalAlignment = Alignment.CenterVertically  // Alinea los botones verticalmente en el centro
            ) {
                Button(
                    onClick = { classification = "NOTE" },
                    modifier = Modifier.weight(1f),  // Esto hace que el botón ocupe espacio igual
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (classification == "NOTE") Color(0xff649562) else Color.Gray
                    )
                ) {
                    Text("Nota")
                }

                Button(
                    onClick = { classification = "TASK" },
                    modifier = Modifier.weight(1f),  // Esto hace que el botón ocupe espacio igual
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (classification == "TASK") Color(0xff649562) else Color.Gray
                    )
                ) {
                    Text("Tarea")
                }
            }
        }
    }
}
