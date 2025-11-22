package mx.edu.utez.itheraqr.ui.screens.components

import QueueItem
import Scan
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import mx.edu.utez.itheraqr.R
import mx.edu.utez.itheraqr.ui.components.ScanCamera
import mx.edu.utez.itheraqr.ui.theme.primary

//valores de navegacion
private const val ROUTE_HOME = "home"
private const val ROUTE_SCAN = "scan"
private const val ROUTE_ROWS = "rows"
private const val ROUTE_MANAGE = "manage"
private const val ROUTE_CAMERA = "scan_camera"


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BotttomBar() {
    //uso del nav
    val navController = rememberNavController()
    var scannedCode by remember { mutableStateOf<String?>(null) }
    ///lista de ejemplo
    val sample = listOf(
        QueueItem("Cafe \"El halcon\"", "Cafetería", 18, 45),
        QueueItem("Cafe \"El balcon\"", "Cafetería", 8, 32),
        QueueItem("Tacos de Oscar", "Restaurante", 15, 58),
        QueueItem("Cinepolis", "Cine", 6, 47)
    )

    //ya respeta lso elementos del tel (statu8s bar)
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        ///minusculas atributo mayus componente
        //header queda pendiente borrar el component
        topBar = {
            TopAppBar(
                modifier = Modifier.height(80.dp),
                title = {
                    Column(verticalArrangement = Arrangement.Center) {
                        Text(text = "ItheraPass", color = Color.White, fontWeight = FontWeight.Bold)
                        Text(text = "Filas virtuales", color = Color.White, fontStyle = FontStyle.Italic)
                    }
                        },
                //si no va a hacer nada quitar el onclick
                navigationIcon = {
                    IconButton(onClick = { /* acción */ }) {
                        Icon(painter = painterResource(id = R.drawable.logo),
                            contentDescription = "logo",
                            modifier = Modifier.size(50.dp),
                            tint = Color.White
                        )
                    }
                },

                ///establecer menu lateral para las notis y para el logout
                actions = {
                    IconButton(onClick = { /* acción */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Opciones", tint = Color.White)
                    }
                },
                backgroundColor = primary
            )
        },
        //navbarbottom
        bottomBar = {
            BottomAppBar {
                NavigationBarItem(
                    selected = navController.currentBackStackEntryAsState().value?.destination?.route == ROUTE_HOME,
                    onClick = { navController.navigate(ROUTE_HOME) { launchSingleTop = true } },
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home"

                            )
                            Text(text = "Inicio")
                        }

                    }
                )
                NavigationBarItem(
                    selected = navController.currentBackStackEntryAsState().value?.destination?.route == ROUTE_SCAN,
                    onClick = { navController.navigate(ROUTE_SCAN) { launchSingleTop = true } },
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(painter = painterResource(id = R.drawable.qr), contentDescription = "Scan", modifier = Modifier.size(24.dp))
                            Text(text = "Escanear")
                        }
                    },
                )
                NavigationBarItem(
                    selected = navController.currentBackStackEntryAsState().value?.destination?.route == ROUTE_ROWS,
                    onClick = { navController.navigate(ROUTE_ROWS) { launchSingleTop = true } },
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(painter = painterResource(id = R.drawable.rows), contentDescription = "Rows", modifier = Modifier.size(24.dp))
                            Text(text = "Mis filas")
                        }
                    }
                )
                NavigationBarItem(
                    selected = navController.currentBackStackEntryAsState().value?.destination?.route == ROUTE_MANAGE,
                    onClick = { navController.navigate(ROUTE_MANAGE) { launchSingleTop = true } },
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(painter = painterResource(id = R.drawable.business), contentDescription = "Business", modifier = Modifier.size(24.dp))
                            Text(text = "Negocio")
                        }
                    }
                )
            }
        }

    ) { innerpadding ->
        ///navegacion por el navigation
        NavHost(
            navController = navController,
            startDestination = ROUTE_HOME,
            modifier = Modifier.padding(innerpadding)
        ) {
            composable(ROUTE_HOME) {
                Home(onOpenScan = { navController.navigate(ROUTE_SCAN) },
                    onOpenManage = {navController.navigate(ROUTE_MANAGE)})
            }
            composable(ROUTE_ROWS) {
                Rows()
            }

            composable(ROUTE_MANAGE) {
                Manage()
            }

            composable(ROUTE_SCAN) {
                Scan(
                    items = sample,
                    onScan = {navController.navigate(ROUTE_CAMERA)},
                    scannedCode = scannedCode,
                    onJoin = { navController.navigate(ROUTE_ROWS) }
                )
            }

            composable(ROUTE_CAMERA) {
                ScanCamera(
                    onScanned = { code ->
                        scannedCode = code
                        navController.popBackStack()
                        // navController.navigate("detail/$code")  // algo asi vamos a ocupar w
                    },
                    onClose = {
                        navController.popBackStack()
                    }
                )
            }

        }
    }
}