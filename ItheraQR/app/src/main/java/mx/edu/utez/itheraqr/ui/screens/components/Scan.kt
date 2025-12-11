import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.itheraqr.data.local.UserSession
import mx.edu.utez.itheraqr.data.network.model.Fila
import mx.edu.utez.itheraqr.ui.screens.viewmodel.FilaViewModel
import mx.edu.utez.itheraqr.ui.theme.tertiary

data class QueueItem(val name: String, val category: String, val inQueue: Int, val attended: Int)///pediente

@Composable
fun Scan(
    viewModel: FilaViewModel,
    items: List<Fila>,
    onScan: () -> Unit = {},
    scannedCode: String,
    onJoin: () -> Unit) {

    //val filas by viewModel.filaState.collectAsState()
    val context = LocalContext.current

    //dialog
    var showDialog by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf(UserSession.getUserName(context)) }
    var userEmail by remember { mutableStateOf(UserSession.getUserEmail(context)) }

    // Escuchamos si el usuario se formó con éxito para resetear todo
    val exitoFormarse by viewModel.resultadoFormarse.collectAsState()
    val error by viewModel.errores.collectAsState()

    // Si esquanea lanzamos el dialog
    LaunchedEffect(scannedCode) {
        if (scannedCode.isNotEmpty()) {
            showDialog = true
        }
    }

    LaunchedEffect(exitoFormarse) {
        if (exitoFormarse) {
            Toast.makeText(context, "¡Te has formado con éxito!", Toast.LENGTH_LONG).show()
            viewModel.resultadoFormarse.value = false // Reset del estado
            onJoin() // Navegamos a la pantalla de filas (ROUTE_ROWS)
        }
    }

    // 3. REACCIÓN A ERRORES
    LaunchedEffect(error) {
        if (error.isNotEmpty()) {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            viewModel.errores.value = "" // Reset
        }
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp)) {
        // Título o Espacio superior
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Escanear QR de Fila",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        //item obligatorio para la manipulacion en el lazy
        item {
            Card {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier
                        .size(256.dp)
                        .border(
                            width = 2.dp,
                            color = Color(0xFFD1D5DC),
                            shape = RoundedCornerShape(14.dp)
                        ),
                        contentAlignment = Alignment.Center){
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "ccamera",
                            modifier = Modifier.size(50.dp)
                        )
                    }

                    Button(
                        onClick = { onScan() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = tertiary)
                    ) {
                        Text(text = "Escanear código de la fila", color = Color.White)
                    }
                }

            }
        }
/*      Ya no se ocupara en teoria xd
        //textfield para la busqueda
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

        //sistema para filtrar las busquedas
        val filtered = if (query.isBlank()) items else items.filter {
            it.name.contains(query, true) || it.category.contains(query, true)
        }
        //items como foreach en el lazy

*/
        //quitar esto es de prueba
        // último item: mostrar el código escaneado (solo para comprobación lo voy a quitar w)
        item {
            Spacer(modifier = Modifier.height(8.dp))
            val textToShow = scannedCode ?: "No se ha escaneado ningún QR aún"
            Text(text = "Último QR leído: $textToShow", modifier = Modifier.padding(vertical = 8.dp))
        }

        item {
        }
    }


    // DIALOG PARA PEDIR NOMBRE
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            title = { Text(text = "Entrar a la fila") },
            text = {
                Column {
                    // Limpiamos la URL para mostrar solo el ID si es necesario
                    val idLimpio = if (scannedCode.contains("/fila/")) {
                        scannedCode.split("/").last()
                    } else {
                        scannedCode
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        label = {Text("Ingresa tu nombre para el turno:")},
                        value = userName,
                        onValueChange = { userName = it },
                        placeholder = { Text("Ej. Alberto Peralta") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // CAMPO CORREO (¡NUEVO!)
                    OutlinedTextField(
                        value = userEmail,
                        onValueChange = { userEmail = it },
                        label = { Text("Correo Electrónico") },
                        placeholder = { Text("ejemplo@correo.com") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (userName.isNotBlank()) {
                            showDialog = false

                            // Extraemos el ID limpio nuevamente para enviarlo
                            val idParaEnviar = if (scannedCode.contains("/fila/")) {
                                scannedCode.split("/").last()
                            } else {
                                scannedCode
                            }

                            // LLAMAMOS AL VIEWMODEL
                            viewModel.formarse(idParaEnviar, userName, userEmail)
                        } else {
                            Toast.makeText(context, "Escribe un nombre", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Formarse")
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
