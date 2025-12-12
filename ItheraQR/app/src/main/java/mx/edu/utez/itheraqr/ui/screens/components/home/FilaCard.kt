package mx.edu.utez.itheraqr.ui.screens.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.itheraqr.data.network.model.Fila

@Composable
fun FilaCard(
    fila: Fila,
    onClick: (() -> Unit)? = null
) {
    // Color según estado (Verde = Abierta, Rojo = Cerrada)
    val colorEstado = when (fila.estado) {
        "ABIERTA" -> Color(0xFF4CAF50) // Verde
        "PAUSADA" -> Color(0xFFFF9800) // Naranja
        "CERRADA" -> Color(0xFFF44336) // Rojo
        else -> Color.Gray
    }

    val colorTexto = if (fila.estado == "ABIERTA") Color(0xFF2E7D32) else Color(0xFFC62828)
    val colorFondo = if (fila.estado == "ABIERTA") Color(0xFFE8F5E9) else Color(0xFFFFEBEE)

    // --- CORRECCIÓN AQUÍ ---
    // coerceAtLeast(0) asegura que si el número es negativo (ej. -1), se muestre 0.
    val enEspera = fila.cantidadEnEspera.coerceAtLeast(0)

    // Calculamos el tiempo usando el número ya corregido
    val tiempoTotal = (fila.tiempoPromedioAtencion * enEspera).coerceAtLeast(0)

    Card(
        onClick = onClick ?: {},
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp, vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = fila.nombre,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp)) // Recorta las esquinas
                        .background(colorFondo)          // Pinta el fondo
                        .padding(horizontal = 8.dp, vertical = 4.dp) // Espacio interno
                ) {
                    Text(
                        text = fila.estado,
                        color = colorTexto,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Categoría: ${fila.categoria}", color = Color.Gray, fontSize = 14.sp)

            Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray.copy(alpha = 0.5f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoCard("En espera", "${enEspera} personas")
                InfoCard(
                    "Tiempo aprox",
                    "${tiempoTotal} min"
                )
            }
            
            // Mostrar info de las filas si esta creada
            if (fila.codigoTurno != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider(modifier = Modifier.padding(vertical = 4.dp), color = Color.LightGray.copy(alpha = 0.5f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // El código ya es solo el número
                    Text(
                        text = "Tu turno: ${fila.codigoTurno}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when (fila.estadoTurno) {
                                    "EN_ESPERA" -> Color(0xFFFFF3E0)
                                    "ATENDIENDO" -> Color(0xFFE3F2FD)
                                    else -> Color(0xFFF5F5F5)
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = fila.estadoTurno ?: "EN_ESPERA",
                            color = when (fila.estadoTurno) {
                                "EN_ESPERA" -> Color(0xFFE65100)
                                "ATENDIENDO" -> Color(0xFF1976D2)
                                else -> Color.Gray
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
