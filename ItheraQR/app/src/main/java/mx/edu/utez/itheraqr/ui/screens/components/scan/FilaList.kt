package mx.edu.utez.itheraqr.ui.screens.components.scan

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import mx.edu.utez.itheraqr.data.local.model.Fila

@Composable
fun FilaList(filas: List<Fila>, onDelete: (Fila)-> Unit, onJoin: (Fila)-> Unit) {

    LazyColumn {
        items(filas){fila->
            FilaCard(fila, onJoin = onJoin, onDelete = onDelete)
        }
    }

}
