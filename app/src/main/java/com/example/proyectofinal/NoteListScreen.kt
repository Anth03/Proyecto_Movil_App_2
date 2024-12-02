package com.example.proyectofinal

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.unit.Dp
import com.example.proyectofinal.data.Note
import com.example.proyectofinal.data.Reminder
import com.example.proyectofinal.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(navController: NavHostController, viewModel: NoteViewModel) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.titulo),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .background(Color(0xFF4CAF50), shape = RoundedCornerShape(12.dp))
                            .padding(8.dp)
                    )
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF4CAF50), shape = CircleShape)
                            .clickable {
                                navController.navigate("noteDetail/0")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+",
                            color = Color.White,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it }
            )
            Spacer(modifier = Modifier.height(9.dp))

            // Observar notas y recordatorios
            val notes by viewModel.allNotes.observeAsState(initial = emptyList())
            val reminders by viewModel.allReminders.observeAsState(initial = emptyList())

            // Filtrar notas por búsqueda
            val filteredNotes = notes.filter {
                it.title.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true)
            }

            // Agrupar recordatorios por noteId
            val remindersByNoteId = remember(reminders) {
                reminders.groupBy { it.noteId }
            }

            NoteGrid(
                notes = filteredNotes,
                remindersByNoteId = remindersByNoteId,
                navController = navController
            )
        }
    }
}

@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    var searchQueryInternal by remember { mutableStateOf(searchQuery) }

    OutlinedTextField(
        value = searchQueryInternal,
        onValueChange = {
            searchQueryInternal = it
            onSearchQueryChange(it)
        },
        placeholder = { Text(text = stringResource(id = R.string.buscar), color = Color.White) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = stringResource(id = R.string.buscar),
                tint = Color.White
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.Black, shape = RoundedCornerShape(10.dp))
    )
}

@Composable
fun NoteItem(
    note: Note,
    reminders: List<Reminder>, // Lista de recordatorios asociados
    navController: NavHostController,
    width: Dp = 250.dp,
    height: Dp = 200.dp
) {
    val backgroundColor = when {
        note.classification == "NOTE" -> Color(0xff9fc7f1)
        note.isCompleted -> Color(0xff6ecf72)
        else -> Color(0xffa8b38e)
    }

    Box(
        modifier = Modifier
            .size(width, height)
            .padding(5.dp)
            .background(backgroundColor, shape = RoundedCornerShape(20.dp))
            .clickable {
                navController.navigate("editNote/${note.id}")
            },
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(7.dp)
        ) {
            // Título de la Nota
            Text(
                text = note.title ?: stringResource(id = R.string.tarea),
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = Color.Black
            )
            // Clasificación
            Text(
                text = if (note.classification == "NOTE") {
                    stringResource(id = R.string.nota)
                } else {
                    stringResource(id = R.string.tarea)
                },
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xff606161)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Descripción de la Nota
            Text(
                text = note.description,
                fontSize = 20.sp,
                color = Color.Black
            )

            if (note.isCompleted) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(id = R.string.terminada),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00796B)
                )
            }

            // Mostrar Recordatorios Asociados
            if (reminders.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF37474F)
                )
                reminders.forEach { reminder ->
                    Text(
                        text = "${reminder.dueDate} ${reminder.dueTime}",
                        fontSize = 14.sp,
                        color = Color(0xFF37474F)
                    )
                }
            }
        }
    }
}

@Composable
fun NoteGrid(
    notes: List<Note>,
    remindersByNoteId: Map<Int, List<Reminder>>, // Mapa de recordatorios por noteId
    navController: NavHostController
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 180.dp),
        modifier = Modifier.padding(8.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(notes) { note ->
            val reminders = remindersByNoteId[note.id] ?: emptyList()
            NoteItem(note = note, reminders = reminders, navController = navController)
        }
    }
}
