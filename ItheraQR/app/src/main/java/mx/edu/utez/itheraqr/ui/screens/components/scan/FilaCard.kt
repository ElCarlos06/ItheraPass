package mx.edu.utez.itheraqr.ui.screens.components.scan

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.itheraqr.data.local.model.Fila
import mx.edu.utez.itheraqr.ui.theme.primary

@Composable
fun FilaCard(fila: Fila, onJoin: (Fila)-> Unit, onDelete: (Fila)-> Unit) {
    Card(shape = RoundedCornerShape(8.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${fila.nombre}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text ="${fila.categoria}", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(6.dp))
                Row {
                    Text(text = "En fila: ${fila.formados}", modifier = Modifier.padding(end = 12.dp), fontSize = 10.sp)
                    Text(text = "Atendidos: ${fila.atendidos}", fontSize = 10.sp)
                }
            }
            Button(onClick = { onJoin(fila) }, modifier = Modifier.height(36.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = primary)
            ) {
                Text(text = "Unirse", color = Color.White)
            }
            IconButton(onClick = {
                onDelete(fila)
            }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Eliminar"
                )
            }
        }
    }
}