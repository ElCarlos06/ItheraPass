package mx.edu.utez.itheraqr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import mx.edu.utez.itheraqr.ui.screens.HomeScreen
import mx.edu.utez.itheraqr.ui.theme.ItheraQRTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ItheraQRTheme {
                HomeScreen()
            }
        }
    }
}

