package mx.edu.utez.itheraqr.ui.screens.components.manage

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.utez.itheraqr.ui.screens.viewmodel.FilaViewModel
import mx.edu.utez.itheraqr.ui.theme.tertiary


@Composable
fun Manage(viewModel: FilaViewModel) {

    val context = LocalContext.current
    val turnos by viewModel.listaTurnos.collectAsState()
    val miFilaId by viewModel.resultadoCrear.collectAsState()

    var showDialog by remember { mutableStateOf(false) }



    // Cargar datos al entrar a la pantalla
    LaunchedEffect(Unit) {
        viewModel.cargarTurnos()
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {

        item {
            Text(
                text = "Gestionar Fila",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        item {
            Text(
                text = "Caja Rápida #1", // Aquí iría el nombre real de tu fila
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(16.dp))
        }


        if (miFilaId != null){
            item {
                val enEspera = turnos.count { it.estado == "EN_ESPERA" }
                val atendiendo = turnos.count { it.estado == "ATENDIENDO"}
                val atendidos = turnos.count { it.estado == "ATENDIDO" }
                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                    StatCard("En espera", enEspera.toString(), Color.Magenta) // Magenta
                    Spacer(modifier = Modifier.width(8.dp))
                    StatCard("Atendiendo", atendiendo.toString(), Color.Blue) // Azul
                    Spacer(modifier = Modifier.width(8.dp))
                    StatCard("Atendidos", atendidos.toString(), Color.Blue) // Verde
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Acciones Rápidas")
                        IconButton(onClick = { viewModel.cargarTurnos() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Recargar", tint = MaterialTheme.colorScheme.primary)
                        }
                        // ¡NUEVO! Botón Borrar Fila
                        IconButton(onClick = { showDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Borrar Fila", tint = Color.Red)
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {

                        val hayGente = turnos.any { it.estado == "EN_ESPERA" }

                        ActionButton(
                            icon = Icons.Default.NotificationsActive,
                            label = "Atender",
                            color = if(hayGente) Color(0xFF66BB6A) else Color.Gray,
                            onClick = {
                                if(hayGente) viewModel.llamarSiguiente()
                                else Toast.makeText(context, "Nadie en espera", Toast.LENGTH_SHORT).show()
                            }
                        )

                        val atendiendoAlguien = turnos.any { it.estado == "ATENDIENDO" }

                        ActionButton(
                            icon = Icons.Default.CheckCircle,
                            label = "Finalizar Turno",
                            color = if(atendiendoAlguien) Color(0xFF42A5F5) else Color.Gray,
                            onClick = {
                                if(atendiendoAlguien) viewModel.atendido()
                                else Toast.makeText(context, "No estás atendiendo", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
/*
        item {
            FilaList() { }
        }
*/
        item {
            Text("Personas formadas")
        }

        if (turnos.isEmpty()) {
            item { Text("Nadie formado aún.", color = Color.Gray) }
        } else {
            items(turnos) { turno ->
                TurnoCard(turno)
            }
        }

    }

    if (showDialog){
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            title = { Text(text = "Borrar la fila") },
            text = {
                Column {
                    Text("Estas seguro de borrar la fila?")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        viewModel.eliminarFila() // Llamamos al ViewModel
                        Toast.makeText(context, "Fila eliminada", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

//Carta para las estaditicas
//la puse aqui pq creo que no vcale la pena otro archivo por algo que se usa 3 veces so,lamente
@Composable
fun StatCard(titulo: String, valor: String, color: Color) {
    Card(
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = titulo, fontSize = 12.sp)
            Text(text = valor, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

//todo aco para terminar mas rapido
@Composable
fun ActionButton(icon: ImageVector, label: String, color: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FilledIconButton(onClick = onClick, colors = IconButtonDefaults.filledIconButtonColors(containerColor = color.copy(alpha = 0.1f))) { Icon(icon, contentDescription = label, tint = color) }
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun AlertDelete(){
    AlertDialog(onDismissRequest = {}, confirmButton = {})
}