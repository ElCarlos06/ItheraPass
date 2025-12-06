package mx.edu.utez.itheraqr.ui.screens.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Switch
import androidx.compose.material.TextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.edu.utez.itheraqr.ui.screens.components.manage.generarQr
import mx.edu.utez.itheraqr.ui.screens.viewmodel.FilaViewModel
import mx.edu.utez.itheraqr.ui.theme.primary

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Manage(viewModel: FilaViewModel, onInsert:(String, Int, Int, Int, Int)-> Unit) {
    //preuba de creacion de qr
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(0) }
    var capacidad by remember { mutableStateOf(0) }
    var formados by remember { mutableStateOf(0) }
    var atendidos by remember { mutableStateOf(0) }

    var switchOpciones by remember { mutableStateOf(false) }
    var nombreNegocio by remember { mutableStateOf("") }
    val categorias = listOf("Cafeteria", "Cine", "Restaurante","Tienda")
    var expandido by remember{ mutableStateOf(false)}
    var selectedOptionText by remember { mutableStateOf(categorias[0]) }
    val pasosUso = listOf("Genera tu código QR único", "Descarga e imprime el código", "Colócalo en un lugar visible en tu negocio","Los clientes lo escanearán para unirse a la fila","Gestiona la fila desde la pestaña \"Gestionar Fila\"")

    LazyColumn(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        item {
            Switch(checked = switchOpciones,onCheckedChange = { switchOpciones = it })
        }
        item {
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                Column(modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()) {
                    Text(text = "Informacion del Negocio")
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(modifier = Modifier.fillMaxWidth(), value = nombreNegocio, onValueChange = {nombreNegocio=it}, label = { Text(text = "Nombre del Negocio") })

                    Spacer(Modifier.height(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = expandido,
                        onExpandedChange = { expandido = it },
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(), readOnly = true,
                            value = selectedOptionText,
                            onValueChange = { },
                            label = { Text("Categoria del Negocio") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = expandido
                                )
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = expandido,
                            onDismissRequest = {
                                expandido = false
                            }
                        ) {
                            categorias.forEach { categoria ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedOptionText = categoria
                                        expandido = false
                                    }
                                ) {
                                    Text(text = categoria)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Button(onClick = {
                        if (nombreNegocio.isNotBlank()) {
                            qrBitmap = generarQr(nombreNegocio, 512, 512)
                            onInsert(nombre, categoria, capacidad, formados, atendidos)
                            //viewModel.insertarFila(nombre,categoria)
                        } else {
                            qrBitmap = null
                        }
                    }, modifier = Modifier.fillMaxWidth()) {
                        Row {

                            Text(text = "Generar codigo QR: ")
                        }
                    }
                }
            }
        }

        item {
            when(qrBitmap){
                null -> null
                else ->
                    Card {
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

        item {
            Text(text = "Intrucciones")

        }

        var num = 1
        items(pasosUso){paso->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 12.dp)) {
                Box(modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "${num}", color = Color.White)
                }
                Spacer(Modifier.width(12.dp))
                Text(text = paso)
            }
            num++
        }
    }
}