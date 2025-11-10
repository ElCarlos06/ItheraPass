package mx.edu.utez.itheraqr.ui.screens.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import mx.edu.utez.itheraqr.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BotttomBar() {
    var seleccionado by remember { mutableStateOf(0) }
    //ya respeta lso elementos del tel (statu8s bar)
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        ///minusculas atributo mayus componente

        bottomBar = {
            BottomAppBar {
                NavigationBarItem(
                    selected = seleccionado == 0,
                    onClick = {seleccionado = 0},
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home"

                            )
                            Text(text = "Inicio")
                        }

                    }
                )
                NavigationBarItem(
                    selected = seleccionado ==1,
                    onClick = {seleccionado = 1},
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(painter = painterResource(id = R.drawable.qr), contentDescription = "Scan", modifier = Modifier.size(24.dp))
                            Text(text = "Escanear")
                        }
                    },
                )
                NavigationBarItem(
                    selected = seleccionado==2,
                    onClick = {seleccionado = 2},
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(painter = painterResource(id = R.drawable.rows), contentDescription = "Rows", modifier = Modifier.size(24.dp))
                            Text(text = "Mis filas")
                        }
                    }
                )
                NavigationBarItem(
                    selected = seleccionado==3,
                    onClick = {seleccionado = 3},
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(painter = painterResource(id = R.drawable.business), contentDescription = "Business", modifier = Modifier.size(24.dp))
                            Text(text = "Negocio")
                        }
                    }
                )
            }
        }

    ) { innerpadding ->
        Box(
            modifier = Modifier.padding(innerpadding)
        ){
            when(seleccionado){
                0 -> Home()
                1 -> Scan()
                2 -> Rows()
                3 -> Manage()
            }
        }
    }
}