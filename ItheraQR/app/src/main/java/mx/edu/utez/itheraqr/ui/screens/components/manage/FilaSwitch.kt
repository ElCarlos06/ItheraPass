package mx.edu.utez.itheraqr.ui.screens.components.manage

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FilaSwitch(
    defaultSelectedItemIndex: Int = 0,
    onItemSelection: (selectedItemIndex: Int) -> Unit
) {
    val opciones = listOf("Generar QR", "Gestionar Fila")
    val selectedIndex = remember { mutableIntStateOf(defaultSelectedItemIndex) }

    // Colores basados en tu imagen
    val backgroundColor = Color(0xFFECECEE) // Gris claro de fondo
    val indicatorColor = Color.White
    val contentColor = Color(0xFF333333)

    // Contenedor Principal (La píldora gris)
    BoxWithConstraints(
        modifier = Modifier
            .padding(8.dp)
            .height(48.dp) // Altura del control
            .clip(CircleShape)
            .background(backgroundColor)
            .padding(4.dp) // Espacio entre el borde gris y el indicador blanco
    ) {
        val maxWidth = this.maxWidth
        val itemWidth = maxWidth / opciones.size

        // Animación de posición del indicador
        val indicatorOffset by animateDpAsState(
            targetValue = itemWidth * selectedIndex.intValue,
            animationSpec = tween(durationMillis = 300), // Duración de la animación
            label = "indicator animation"
        )

        // 1. El Indicador (El fondo blanco que se mueve)
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(itemWidth)
                .fillMaxHeight()
                .shadow(elevation = 2.dp, shape = CircleShape) // Sombra sutil
                .background(indicatorColor, CircleShape)
        )

        // 2. Los Textos (Superpuestos encima)
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            opciones.forEachIndexed { index, item ->
                Box(
                    modifier = Modifier
                        .width(itemWidth)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null // Quita el efecto "ripple" al hacer click
                        ) {
                            selectedIndex.intValue = index
                            onItemSelection(index)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = contentColor
                    )
                }
            }
        }
    }
}