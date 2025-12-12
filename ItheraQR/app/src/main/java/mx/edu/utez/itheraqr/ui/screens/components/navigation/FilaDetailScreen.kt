package mx.edu.utez.itheraqr.ui.screens.components.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.itheraqr.data.network.model.Fila
import mx.edu.utez.itheraqr.ui.screens.components.manage.TurnoCard
import mx.edu.utez.itheraqr.ui.screens.viewmodel.FilaViewModel
import mx.edu.utez.itheraqr.utils.NotificationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilaDetailScreen(
    fila: Fila,
    viewModel: FilaViewModel,
    onBack: () -> Unit
) {
    val turnos by viewModel.listaTurnos.collectAsState()
    
    // Detener la alarma cuando el usuario entra a esta pantalla (interacción)
    LaunchedEffect(Unit) {
        NotificationHelper.stopCallAlarm()
        viewModel.cargarMisFilas()
        viewModel.cargarTurnos()
    }

    LaunchedEffect(fila.id) {
        viewModel.cargarTurnosPorFila(fila.id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles de Fila") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (fila.codigoTurno != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (fila.estadoTurno == "ATENDIENDO") 
                                MaterialTheme.colorScheme.primaryContainer 
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Tu Turno", 
                                fontSize = 18.sp, 
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // El código ya es solo el número
                                Text(
                                    fila.codigoTurno, 
                                    fontSize = 32.sp, 
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Surface(
                                    color = when (fila.estadoTurno) {
                                        "EN_ESPERA" -> MaterialTheme.colorScheme.tertiaryContainer
                                        "ATENDIENDO" -> MaterialTheme.colorScheme.primaryContainer
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        fila.estadoTurno ?: "EN_ESPERA",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        color = when (fila.estadoTurno) {
                                            "EN_ESPERA" -> MaterialTheme.colorScheme.onTertiaryContainer
                                            "ATENDIENDO" -> MaterialTheme.colorScheme.onPrimaryContainer
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        },
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            
            item {
                Text(
                    "Personas formadas", 
                    fontSize = 20.sp, 
                    fontWeight = FontWeight.Bold, 
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            if (turnos.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            "No hay personas formadas aún", 
                            modifier = Modifier.padding(16.dp), 
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(turnos) { turno ->
                    TurnoCard(turno)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

