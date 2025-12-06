package mx.edu.utez.itheraqr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import mx.edu.utez.itheraqr.ui.screens.HomeScreen
import mx.edu.utez.itheraqr.ui.theme.ItheraQRTheme
import mx.edu.utez.itheraqr.ui.screens.viewmodel.FilaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel = ViewModelProvider(this)[FilaViewModel::class.java]

        setContent {
            ItheraQRTheme {
                // Pasamos el viewModel a la pantalla principal
                HomeScreen(viewModel)
            }
        }
    }
}

