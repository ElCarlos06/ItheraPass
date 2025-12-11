package mx.edu.utez.itheraqr.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.itheraqr.ui.screens.components.rows.FilaCard
import mx.edu.utez.itheraqr.ui.screens.viewmodel.FilaViewModel

@Composable
fun Rows(viewModel: FilaViewModel) {

    // 1. Escuchamos la lista de filas del ViewModel
    val filas by viewModel.listaFilas.collectAsState()
    val error by viewModel.errores.collectAsState()

    // Cargar datos al entrar a la pantalla
    LaunchedEffect(Unit) {
        viewModel.cargarFilas()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Encabezado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filas Activas",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            // Botón para recargar manualmente
            IconButton(onClick = { viewModel.cargarFilas() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Recargar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mensaje de error si falla internet
        if (error.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                Text(
                    text = error,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Lista de Filas
        if (filas.isEmpty()) {
            // Estado vacío o cargando
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay filas disponibles o cargando...", color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filas) { fila ->
                    FilaCard(
                        fila
                    )
                }
            }
        }
    }
}