package mx.edu.utez.itheraqr.ui.screens.components.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.edu.utez.itheraqr.ui.theme.primary

@Composable
fun EmptyStateCard(
    message: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(message, color = Color.Gray)
            if (actionText != null && onAction != null) {
                TextButton(
                    onClick = onAction,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        actionText,
                        fontWeight = FontWeight.Bold,
                        color = primary
                    )
                }
            }
        }
    }
}

