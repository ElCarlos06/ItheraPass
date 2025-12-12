package mx.edu.utez.itheraqr.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.itheraqr.R
import mx.edu.utez.itheraqr.ui.screens.components.rows.FilaCard
import mx.edu.utez.itheraqr.ui.screens.home.ActionCard
import mx.edu.utez.itheraqr.ui.screens.viewmodel.FilaViewModel
import mx.edu.utez.itheraqr.ui.theme.primary
import mx.edu.utez.itheraqr.ui.theme.secondary

@Composable
fun Home(onOpenScan: () -> Unit = {}, onOpenManage: () -> Unit = {}, viewModel: FilaViewModel) {
    //Lista de pasos optimizada
    val pasosUso = listOf(
        "Escanea el código QR del negocio",
        "Espera cómodamente donde quieras"
    )

    val filas by viewModel.misFilasActivas.collectAsState()

    // Cargar datos al entrar
    LaunchedEffect(Unit) {
        viewModel.cargarFilas()
    }

    LazyColumn(modifier = Modifier.padding(start = 24.dp, end = 24.dp)) {
        item {

            Column(
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Text(text = "Más facil, más organizado", fontSize = 18.sp)
                Text(text = "Gestiona las filas de manera inteligente", fontSize = 15.sp)

                //componentes de Card reutilizable
                ActionCard(
                    iconRes = R.drawable.qr,
                    iconBackground = primary, //uso de los temas globales
                    title = "Unirse a una fila",
                    subtitle = "Utilice la cámara para escanear el QR del negocio para formar parte de la fila virtual",
                    onClick = onOpenScan
                )
                ActionCard(
                    iconRes = R.drawable.business,
                    iconBackground = secondary, //uso de los temas globales
                    title = "Gestionar fila",
                    subtitle = "Accede a las herramientas para administrar tu fila y notificar clientes",
                    onClick = onOpenManage
                )
                Text(text = "¿Como funciona?")
            }

        }


        itemsIndexed(pasosUso) { index, paso ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 6.dp, bottom = 6.dp)
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

        item {

            Row {
                Text(
                    text = "Mis Turnos Activos",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
                IconButton(onClick = { viewModel.cargarMisFilas() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Recargar", tint = MaterialTheme.colorScheme.primary)
                }
            }

        }

        // 2. LISTA DINÁMICA DE FILAS
        if (filas.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No estás formado en ninguna fila.", color = Color.Gray)
                        Text(
                            "¡Usa el escáner para unirte!",
                            fontWeight = FontWeight.Bold,
                            color = primary
                        )
                    }
                }
            }
        } else {
            items(filas) { fila ->
                // Reutilizamos tu FilaCard de la pantalla Rows para consistencia
                // O puedes crear una "MiTurnoCard" simplificada si prefieres
                FilaCard(fila)


            }
        }
    }
}
