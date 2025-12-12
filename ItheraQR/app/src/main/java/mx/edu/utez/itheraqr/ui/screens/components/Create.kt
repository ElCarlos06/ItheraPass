package mx.edu.utez.itheraqr.ui.screens.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import kotlinx.coroutines.launch
import mx.edu.utez.itheraqr.data.local.FileManager
import mx.edu.utez.itheraqr.ui.screens.viewmodel.FilaViewModel
import mx.edu.utez.itheraqr.ui.theme.primary
import mx.edu.utez.itheraqr.R
import mx.edu.utez.itheraqr.ui.screens.components.create.generarQR

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Create(
    viewModel: FilaViewModel,
    onInsert: (String, String, String, String, String) -> Unit
) {
    val context = LocalContext.current
    // Estados para los campos del formulario
    var nombreFila by remember { mutableStateOf("") }
    var tiempoPromedio by remember { mutableStateOf("5") }
    //dorpdown
    var categoriaSeleccionada by remember { mutableStateOf("") }
    var expandido by remember { mutableStateOf(false) }
    val categoriasDisponibles =
        listOf("Restaurante", "Banco", "Trámites", "Salud", "Escuela", "Otro")

    //preuba de creacion de qr
    var qrBitmap by rememberSaveable { mutableStateOf<Bitmap?>(null) }

    // viemodel
    val exitoCrear by viewModel.resultadoCrear.collectAsState()
    val error by viewModel.errores.collectAsState()

    val scope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }
    val fileManager = remember { FileManager(context) }
    val logoBitmap = remember {
        BitmapFactory.decodeResource(context.resources, R.drawable.logo)
    }

    var creada by remember { mutableStateOf(false) }


    var contenidoQR by remember { mutableStateOf("") }

    ///preubas

    // Reaccionar al éxito
    LaunchedEffect(exitoCrear) {
        if (exitoCrear != null) {
            if (!creada){
                Toast.makeText(context, "¡Fila creada con éxito!", Toast.LENGTH_SHORT).show()
                creada = true
            }

            // Generamos el QR usando el nombre como identificador único simple
            // En una app real usarías el ID que devuelve la BD (ej. fila.id)
            // Generamos QR
            val contenidoQR = "https://www.itherapass.com/fila/$exitoCrear"
            qrBitmap = generarQR(
                texto = contenidoQR,
                logo = logoBitmap
            )

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
        if (qrBitmap == null) {
            item {

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
                            .fillMaxWidth()
                    ) {
                        Text(text = "Informacion del Negocio")
                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = nombreFila,
                            onValueChange = { nombreFila = it },
                            label = { Text(text = "Nombre del Negocio")},
                            colors = OutlinedTextFieldDefaults.colors()
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
                                colors = OutlinedTextFieldDefaults.colors()
                            )
                            ExposedDropdownMenu(
                                expanded = expandido,
                                onDismissRequest = {
                                    expandido = false
                                },
                                modifier = Modifier.background(MaterialTheme.colorScheme.background)
                            ) {
                                categoriasDisponibles.forEach { categoria ->
                                    DropdownMenuItem(
                                        onClick = {
                                            categoriaSeleccionada = categoria
                                            expandido = false
                                        },

                                    ) {
                                        Text(text = categoria,
                                            color = MaterialTheme.colorScheme.onSurface)
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
                            placeholder = { Text("El tiempo por defecto es de 5 min") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors()
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
                                    Toast.makeText(
                                        context,
                                        "Llena todos los campos",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Crear fila y generar QR ",
                                color = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }


        }

        if (qrBitmap != null) {

            item {
                Card() {

                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {

                        Image(
                            bitmap = qrBitmap!!.asImageBitmap(),
                            contentDescription = "QR generado",
                            modifier = Modifier
                                .size(256.dp)
                        )

                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = {
                                if (!isSaving && qrBitmap != null) {
                                    // Lanzamos la corrutina para guardar
                                    scope.launch {
                                        isSaving = true
                                        // Usamos el ID o el nombre para el archivo
                                        val filename = "ItheraQR_${nombreFila.trim()}_${exitoCrear}"
                                        fileManager.saveBitmapToGallery(qrBitmap!!, filename)
                                        isSaving = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isSaving // Se deshabilita si ya está isSaving
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Guardando...")
                            } else {
                                Icon(Icons.Default.Download, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Guardar en Galería")
                            }
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
            "Ingresa el nombre y la categoria de tu negocio.",
            "Indica cuánto tardas en atender aproximadamente.",
            "Genera el codigo QR e imprimelo",
            "Colócalo en un lugar visible en tu negocio",
            "Gestiona la fila desde la pestaña \"Gestionar Fila\""
        )

        itemsIndexed(pasosUso) { index, paso ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "${index + 1}", color = Color.White)
                }
                Spacer(Modifier.width(12.dp))
                Text(text = paso)
            }
        }
    }
}
