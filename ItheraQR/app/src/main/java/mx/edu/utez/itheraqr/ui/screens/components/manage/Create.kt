package mx.edu.utez.itheraqr.ui.screens.components.manage

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.itheraqr.ui.screens.viewmodel.FilaViewModel
import mx.edu.utez.itheraqr.ui.theme.primary

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Create(
    viewModel: FilaViewModel,
    onInsert:(String, String, String, String, String)-> Unit
) {
    val context = LocalContext.current
    // Estados para los campos del formulario
    var nombreFila by remember { mutableStateOf("") }
    var tiempoPromedio by remember { mutableStateOf("5") }
    //dorpdown
    var categoriaSeleccionada by remember { mutableStateOf("") }
    var expandido by remember{ mutableStateOf(false)}
    val categoriasDisponibles = listOf("Restaurante", "Banco", "Trámites", "Salud", "Escuela", "Otro")

    //preuba de creacion de qr
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // viemodel
    val exitoCrear by viewModel.resultadoCrear.collectAsState()
    val error by viewModel.errores.collectAsState()

    ///preubas

    // Reaccionar al éxito
    LaunchedEffect(exitoCrear) {
        if (exitoCrear != null) {
            Toast.makeText(context, "¡Fila creada con éxito!", Toast.LENGTH_SHORT).show()

            // Generamos el QR usando el nombre como identificador único simple
            // En una app real usarías el ID que devuelve la BD (ej. fila.id)
            // Generamos QR
            val contenidoQR = "https://www.itherapass.com/fila/$exitoCrear"
            qrBitmap = generarQR(contenidoQR)

            // Reseteamos el ViewModel
            //viewModel.resultadoCrear.value = false
        }
    }

    // Reaccionar a errores
    LaunchedEffect(error) {
        if (error.isNotEmpty()) {
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            viewModel.errores.value = ""
        }
    }

    LazyColumn(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
            .padding(horizontal = 24.dp),
    ) {


        item {
            Text(
                text = "Crear Nueva Fila",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp, top = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            if (qrBitmap == null) {
                Card(
                    modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()) {
                        Text(text = "Informacion del Negocio")
                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = nombreFila,
                            onValueChange = {nombreFila=it},
                            label = { Text(text = "Nombre del Negocio") }
                        )

                        Spacer(Modifier.height(8.dp))

                        ExposedDropdownMenuBox(
                            expanded = expandido,
                            onExpandedChange = { expandido = !expandido },
                        ) {
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                readOnly = true,
                                value = categoriaSeleccionada,
                                onValueChange = { },
                                label = { Text("Categoria") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expandido
                                    )
                                },
                            )
                            ExposedDropdownMenu(
                                expanded = expandido,
                                onDismissRequest = {
                                    expandido = false
                                },
                                //modifier = Modifier.background(Color.Black) preguntar como establecer estos colores
                            ) {
                                categoriasDisponibles.forEach { categoria ->
                                    DropdownMenuItem(
                                        onClick = {
                                            categoriaSeleccionada = categoria
                                            expandido = false
                                        }
                                    ) {
                                        Text(text = categoria)
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        // CAMPO: TIEMPO PROMEDIO
                        OutlinedTextField(
                            value = tiempoPromedio,
                            onValueChange = {
                                // Solo permitimos números
                                if (it.all { char -> char.isDigit() }) {
                                    tiempoPromedio = it
                                }
                            },
                            label = { Text("Tiempo aprox. por persona (min)") },
                            placeholder = {Text("El tiempo por defecto es de 5 min")},
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                        )

                        Spacer(Modifier.height(8.dp))

                        Button(
                            onClick = {
                                if (nombreFila.isNotBlank() && categoriaSeleccionada.isNotBlank()) {
                                    val tiempoLong = tiempoPromedio.toLongOrNull() ?: 5L

                                    viewModel.crearFila(
                                        nombre = nombreFila,
                                        categoria = categoriaSeleccionada,
                                        tiempo = tiempoLong
                                    )
                                } else {
                                    Toast.makeText(context, "Llena todos los campos", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Crear fila y generar QR ")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }



        }

        item {
            Column(modifier = Modifier.padding(start = 48.dp)) {
                when(qrBitmap){
                    null -> null
                    else ->
                        Card() {
                            qrBitmap?.let { bmp ->
                                Image(
                                    bitmap = bmp.asImageBitmap(),
                                    contentDescription = "QR generado",
                                    modifier = Modifier
                                        .size(256.dp)
                                )
                            }
                        }

                }
            }
        }


        item {
            Spacer(Modifier.height(8.dp))
            Text(text = "Intrucciones")
        }
         //pasos de uso
        val pasosUso = listOf(
            "Escribe el nombre de tu negocio.",
            "Selecciona una categoría de la lista.",
            "Indica cuánto tardas en atender aprox.",
            "Genera tu código QR único",
            "Descarga e imprime el código",
            "Colócalo en un lugar visible en tu negocio",
            "Los clientes lo escanearán para unirse a la fila",
            "Gestiona la fila desde la pestaña \"Gestionar Fila\""
        )

        itemsIndexed(pasosUso){index,paso->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 12.dp)) {
                Box(modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "${index+1}", color = Color.White)
                }
                Spacer(Modifier.width(12.dp))
                Text(text = paso)
            }
        }
    }
}