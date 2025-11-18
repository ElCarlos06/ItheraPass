import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import mx.edu.utez.itheraqr.ui.components.ScanCamera

data class QueueItem(val name: String, val category: String, val inQueue: Int, val attended: Int)

@Composable
fun Scan(items: List<QueueItem>, onScan: () -> Unit = {},scannedCode: String?, onJoin: (QueueItem) -> Unit = {}) {
    var query by remember { mutableStateOf("") }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier
        .fillMaxSize()
        .padding(24.dp)) {
        item {
            Card {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier
                        .size(256.dp)
                        .border(
                            width = 2.dp,
                            color = Color(0xFFD1D5DC),
                            shape = RoundedCornerShape(14.dp)
                        ),
                        contentAlignment = Alignment.Center){
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "ccamera", modifier = Modifier.size(50.dp))
                    }

                    Button(
                        onClick = { onScan() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF155DFC))
                    ) {
                        Text(text = "Escanear código de la fila", color = Color.White)
                    }
                }

            }
        }

        item {
            TextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Buscar negocios...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Text(text = "Filas Registradas", fontSize = 18.sp)
        }

        val filtered = if (query.isBlank()) items else items.filter {
            it.name.contains(query, true) || it.category.contains(query, true)
        }
        items(filtered) { item ->
            Card(shape = RoundedCornerShape(8.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = item.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(text = item.category, fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row {
                            Text(text = "En fila: ${item.inQueue}", modifier = Modifier.padding(end = 12.dp), fontSize = 10.sp)
                            Text(text = "Atendidos: ${item.attended}", fontSize = 10.sp)
                        }
                    }
                    Button(onClick = { onJoin(item) }, modifier = Modifier.height(36.dp), colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF155DFC))) {
                        Text(text = "Unirse", color = Color.White)
                    }
                }
            }
        }

        // último item: mostrar el código escaneado (solo para comprobación)
        item {
            Spacer(modifier = Modifier.height(8.dp))
            val textToShow = scannedCode ?: "No se ha escaneado ningún QR aún"
            Text(text = "Último QR leído: $textToShow", modifier = Modifier.padding(vertical = 8.dp))
        }
    }



}
