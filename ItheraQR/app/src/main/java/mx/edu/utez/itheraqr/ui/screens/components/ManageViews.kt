package mx.edu.utez.itheraqr.ui.screens.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.utez.itheraqr.ui.screens.components.manage.Create
import mx.edu.utez.itheraqr.ui.screens.components.manage.FilaSwitch
import mx.edu.utez.itheraqr.ui.screens.components.manage.Manage
import mx.edu.utez.itheraqr.ui.screens.components.manage.asdf
import mx.edu.utez.itheraqr.ui.screens.components.manage.preubas
import mx.edu.utez.itheraqr.ui.screens.viewmodel.FilaViewModel

@Composable
fun ManageViews(viewModel: FilaViewModel){
    var selectedIndex by remember { mutableIntStateOf(0) }


    LazyColumn {

        item {

            // 2. EL CONTROL: Le pasamos el estado actual y actualizamos cuando cambia
            Box(modifier = Modifier.padding(16.dp)) {
                FilaSwitch (
                    defaultSelectedItemIndex = selectedIndex,
                    onItemSelection = { index ->
                        selectedIndex = index // Actualizamos el estado
                    }
                )
            }
        }

        item {
            Crossfade(targetState = selectedIndex, label = "ScreenTransition") { index ->
            when (index) {
                0-> {}
                1 -> {

                }
            }
        } }
    }
}