package mx.edu.utez.itheraqr.ui.screens.components.manage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.itheraqr.data.network.model.Turno

@Composable
fun TurnoCard(turno: Turno) {
    // CAMBIO: Comparamos String
    val isActive = turno.estado == "ATENDIENDO"

    Card(elevation = CardDefaults.cardElevation(if (isActive) 8.dp else 2.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(if (isActive) MaterialTheme.colorScheme.primary else Color.LightGray), contentAlignment = Alignment.Center) {
                    val inicial = if (turno.nombreUsuario.isNotEmpty()) turno.nombreUsuario.first().toString() else "?"
                    Text(text = inicial, color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = turno.nombreUsuario, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    // El código ya es solo el número
                    Text(text = "Turno: ${turno.codigoTurno}", color = Color.Gray, fontSize = 14.sp)
                }
            }
            if (isActive) {
                Surface(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(50)) {
                    Text("ATENDIENDO", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            } else {
                Text(text = turno.estado, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}