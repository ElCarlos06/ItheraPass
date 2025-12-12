package mx.edu.utez.itheraqr.ui.screens.components

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import mx.edu.utez.itheraqr.ui.screens.components.navigation.EmptyStateCard
import mx.edu.utez.itheraqr.ui.screens.viewmodel.FilaViewModel
import mx.edu.utez.itheraqr.ui.screens.components.manage.TurnoCard


@Composable
fun Manage(viewModel: FilaViewModel) {

    val context = LocalContext.current
    val turnos by viewModel.listaTurnos.collectAsState()
    val miFilaId by viewModel.resultadoCrear.collectAsState()
    val misFilasCreadas by viewModel.misFilasCreadas.collectAsState()
    val allFillas by viewModel.listaFilas.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    // Encontrar el nombre de MI fila buscando por ID
    val miFilaNombre = remember(miFilaId, allFillas) {
        allFillas.find { it.id == miFilaId }?.nombre ?: "Mi Fila"
    }

    val miFilaActual = remember(miFilaId, allFillas) {
        allFillas.find { it.id == miFilaId }
    }

    // Cargar filas del usuario al entrar
    LaunchedEffect(Unit) {
        viewModel.cargarMisFilas()
        if (miFilaId != null) {
            viewModel.cargarTurnos()
        }
    }
    
    // Si no hay filas creadas, mostrar mensaje
    if (misFilasCreadas.isEmpty() && miFilaId == null) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                EmptyStateCard(
                    message = "No has creado ninguna fila.",
                    actionText = "¡Ve a 'Crear' para generar tu primera fila!",
                    onAction = null // No hay acción, solo mensaje
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            Text(
                text = "Gestionar Fila",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (miFilaId == null) {
                Text("No hay fila activa. Crea una nueva.", color = Color.Gray)
            } else {
                Text(
                    text = miFilaNombre,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(text = "ID: $miFilaId", fontSize = 14.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (miFilaId != null){
            item {
                val enEspera = turnos.count { it.estado == "EN_ESPERA" }
                val atendiendo = turnos.count { it.estado == "ATENDIENDO"}
                // 2. Usar el contador que viene desde la base de datos, no de la lista local
                val atendidos = miFilaActual?.cantidadAtendidos ?: 0

                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                    StatCard("En espera", enEspera.toString(), Color.Magenta,  Modifier.fillMaxWidth().weight(1f)) // Magenta
                    Spacer(modifier = Modifier.width(8.dp))
                    StatCard("Atendiendo", atendiendo.toString(), Color.Blue, Modifier.fillMaxWidth().weight(1f)) // Azul
                    Spacer(modifier = Modifier.width(8.dp))
                    StatCard("Atendidos", atendidos.toString(), Color.Green, Modifier.fillMaxWidth().weight(1f)) // Verde
                }
                Spacer(Modifier.height(8.dp))
            }

            // ACCIONES RÁPIDAS (DISEÑO MEJORADO)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    // Mejora de estilos: Elevación suave, esquinas redondeadas y fondo blanco limpio
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp) // Más espacio interno
                    ) {
                        // CABECERA: Título a la Izquierda, Iconos a la Derecha
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween, // CLAVE: Separa extremos
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Acciones Rápidas",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Fila de Iconos (Agrupados a la derecha)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { viewModel.cargarTurnos() }) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Recargar", tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { showDialog = true }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Borrar Fila", tint = Color.Red)
                                }
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))

                        // BOTONES DE CONTROL
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {

                            val hayGente = turnos.any { it.estado == "EN_ESPERA" }

                            ActionButton(
                                icon = Icons.Default.Groups, // Icono de gente
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
                                    if(atendiendoAlguien) {
                                        val turnoActual = turnos.firstOrNull { it.estado == "ATENDIENDO" }
                                        viewModel.atendido()
                                        // Mostrar toast de confirmación
                                        if (turnoActual != null) {
                                            Toast.makeText(context, "Turno ${turnoActual.codigoTurno} finalizado", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "No estás atendiendo", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        item {
            Text("Turnos Actuales", fontWeight = FontWeight.SemiBold, fontSize = 20.sp, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
        }

        val listaVisible = turnos.filter { it.estado != "ATENDIDO" }
        if (listaVisible.isEmpty()) {
            item { Text("Fila vacía.", color = Color.Gray) }
        } else {
            items(listaVisible) { turno ->
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
                    Text("¿Esta seguro de borrar la fila?")
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
                    Text(text = "Eliminar",
                        color = MaterialTheme.colorScheme.onBackground)
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
fun StatCard(titulo: String, valor: String, color: Color, modifier: Modifier) {
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