package mx.edu.utez.itheraqr.ui.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun Manage() {
    var nombreNegocio by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "MANAGE")

        Card {
            Column {
                Text(text = "Informacion del Negocio")
                OutlinedTextField(value = nombreNegocio, onValueChange = {nombreNegocio=it}, label = { Text(text = "Nombre del Negocio") })

                OutlinedTextField(value = nombreNegocio, onValueChange = {nombreNegocio=it}, label = { Text(text = "Categoria del Negocio") })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun asd(){
    Manage()
}
