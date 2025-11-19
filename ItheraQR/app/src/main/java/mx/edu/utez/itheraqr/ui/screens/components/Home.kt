package mx.edu.utez.itheraqr.ui.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.itheraqr.R
import mx.edu.utez.itheraqr.ui.screens.home.ActionCard

@Composable
fun Home(onOpenScan: () -> Unit = {}, onOpenManage: () -> Unit = {}) {
    val pasosUso = listOf(
        "Escanea el código QR del negocio",
        "Espera cómodamente donde quieras",
        "Recibe notificaciones del avance"
    )


    Column(Modifier.padding(24.dp)) {
        Text(text = "Más facil, más organizado", fontSize = 18.sp)
        Text(text = "Gestiona las filas de manera inteligente", fontSize = 15.sp)

        ActionCard(
            iconRes = R.drawable.qr,
            iconBackground = Color(0xFF155DFC),
            title = "Unirse a una fila",
            subtitle = "Utilice la cámara para escanear el QR del negocio para formar parte de la fila virtual",
            onClick = onOpenScan
        )

        ActionCard(
            iconRes = R.drawable.business,
            iconBackground = Color(0xFF00A63E),
            title = "Gestionar fila",
            subtitle = "Accede a las herramientas para administrar tu fila y notificar clientes",
            onClick = onOpenManage
        )

        Text(text = "¿Como funciona?")

        pasosUso.forEachIndexed { index, paso ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 12.dp)) {
                Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFF155DFC)),
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

@Preview(showBackground = true)
@Composable
fun HomePreview(){
    Home()
}