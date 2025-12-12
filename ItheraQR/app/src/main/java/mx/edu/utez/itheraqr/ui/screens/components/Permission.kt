package mx.edu.utez.itheraqr.ui.screens.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun Permission() {

    Column {


        Card {
            Column {
                Text("Como quieres usar la aplicacion?")

                Row {
                    Button(onClick = {}) {
                        Text("Cliente")
                    }
                    Button(onClick = {}) {
                        Text("Negocio")
                    }
                }
            }


        }
    }
}

@Preview(showBackground = true)
@Composable
fun asfd(){
    Permission()
}