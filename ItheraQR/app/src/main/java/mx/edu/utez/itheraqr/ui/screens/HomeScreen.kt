package mx.edu.utez.itheraqr.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mx.edu.utez.itheraqr.ui.screens.components.BotttomBar
import mx.edu.utez.itheraqr.ui.screens.viewmodel.FilaViewModel

@Composable
fun HomeScreen(viewModel: FilaViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.statusBarsPadding().fillMaxSize()
    ) {

        BotttomBar()
    }

}