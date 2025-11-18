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

@Composable
fun Home(onOpenScan: () -> Unit = {}, onOpenManage: () -> Unit = {}) {

    Column(Modifier.padding(24.dp)) {
        Text(text = "Más facil, más organizado", fontSize = 18.sp)
        Text(text = "Gestiona las filas de manera inteligente", fontSize = 15.sp)

        Card(modifier = Modifier.padding(vertical = 8.dp), onClick = onOpenScan) {
            Row(modifier = Modifier.padding(horizontal = 20.dp, vertical = 15.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(50.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFF155DFC)),
                    contentAlignment = Alignment.Center
                ){
                    Image(painter = painterResource(id = R.drawable.qr), colorFilter = ColorFilter.tint(color = Color.White),
                        contentDescription = "qr", modifier = Modifier.size(28.dp)
                    )
                }

                Column(Modifier.padding(start = 12.dp)) {
                    Text(text = "Unirse a una fila", fontSize = 16.sp)
                    Text(text = "Utilice la camara para escanear el QR del negocio para formar parte de la fila virtual", fontSize = 12.sp, lineHeight = 14.sp)
                }
            }
        }

        Card(modifier = Modifier.padding(vertical = 8.dp), onClick = onOpenManage) {
            Row(modifier = Modifier.padding(horizontal = 20.dp, vertical = 15.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(50.dp).clip(RoundedCornerShape(14.dp)).background(Color(
                    0xFF00A63E
                )
                ),
                    contentAlignment = Alignment.Center
                ){
                    Image(painter = painterResource(id = R.drawable.business), contentDescription = "business",
                        modifier = Modifier.size(28.dp), colorFilter =  ColorFilter.tint(Color.White)
                    )
                }

                Column(Modifier.padding(start = 12.dp)) {
                    Text(text = "Unirse a una fila", fontSize = 16.sp)
                    Text(text = "Utilice la camara para escanear el QR del negocio para formar parte de la fila virtual", fontSize = 12.sp, lineHeight = 14.sp)
                }
            }
        }

        Text(text = "¿Como funciona?")

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 12.dp)) {
            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFF155DFC)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "1", color = Color.White)
            }
            Spacer(Modifier.width(12.dp))
            Text(text = "Escanea el código QR del negocio")
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 12.dp)) {
            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFF155DFC)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "2", color = Color.White)
            }
            Spacer(Modifier.width(12.dp))
            Text(text = "Espera cómodamente donde quieras")
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 12.dp)) {
            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFF155DFC)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "3", color = Color.White)
            }
            Spacer(Modifier.width(12.dp))
            Text(text = "Recibe notificaciones del avance")
        }




    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview(){
    Home()
}