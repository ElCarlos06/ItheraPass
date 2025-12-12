package mx.edu.utez.itheraqr.ui.screens.components

import Scan
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import mx.edu.utez.itheraqr.R
import mx.edu.utez.itheraqr.data.network.model.Fila
import mx.edu.utez.itheraqr.ui.components.ScanCamera
import mx.edu.utez.itheraqr.ui.screens.components.manage.Create
import mx.edu.utez.itheraqr.ui.screens.components.manage.Manage
import mx.edu.utez.itheraqr.ui.screens.viewmodel.FilaViewModel
import mx.edu.utez.itheraqr.ui.theme.primary

//valores de navegacion
private const val ROUTE_HOME = "home"
private const val ROUTE_SCAN = "scan"
private const val ROUTE_ROWS = "rows"
private const val ROUTE_CREATE = "create"
private const val ROUTE_CAMERA = "camera"
private const val ROUTE_MANAGE = "manage"


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BotttomBar() {
    //uso del nav
    val navController = rememberNavController()
    // guardar el qr
    var scannedCode by remember { mutableStateOf("") }
    ///lista de ejemplo
    val sample = emptyList<Fila>()

    val filaViewModel: FilaViewModel = viewModel()

    //ya respeta lso elementos del tel (statu8s bar)
    Scaffold(
        //modifier = Modifier.fillMaxSize(),
        ///minusculas atributo mayus componente
        //header queda pendiente borrar el component
        topBar = {
            TopAppBar(
                modifier = Modifier.height(80.dp),
                title = {
                    Column(verticalArrangement = Arrangement.Center) {
                        Text(text = "ItheraPass", color = Color.White, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Filas virtuales",
                            color = Color.White,
                            fontStyle = FontStyle.Italic
                        )
                    }
                },

                navigationIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "logo",
                        modifier = Modifier
                            .padding(start = 12.dp).size(50.dp),
                        tint = Color.White
                    )
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
                            Icon(
                                painter = painterResource(id = R.drawable.qr),
                                contentDescription = "Scan",
                                modifier = Modifier.size(24.dp)
                            )
                            Text(text = "Escanear")
                        }
                    },
                )
                /*
                NavigationBarItem(
                    selected = navController.currentBackStackEntryAsState().value?.destination?.route == ROUTE_ROWS,
                    onClick = { navController.navigate(ROUTE_ROWS) { launchSingleTop = true } },
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(id = R.drawable.rows),
                                contentDescription = "Rows",
                                modifier = Modifier.size(24.dp)
                            )
                            Text(text = "Mis filas")
                        }
                    }
                )
                 */
                NavigationBarItem(
                    selected = navController.currentBackStackEntryAsState().value?.destination?.route == ROUTE_CREATE,
                    onClick = { navController.navigate(ROUTE_CREATE) { launchSingleTop = true } },
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(id = R.drawable.business),
                                contentDescription = "Business",
                                modifier = Modifier.size(24.dp)
                            )
                            Text(text = "Crear")
                        }
                    }
                )
                NavigationBarItem(
                    selected = navController.currentBackStackEntryAsState().value?.destination?.route == ROUTE_MANAGE,
                    onClick = { navController.navigate(ROUTE_MANAGE) { launchSingleTop = true } },
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(id = R.drawable.rows),
                                contentDescription = "Business",
                                modifier = Modifier.size(24.dp)
                            )
                            Text(text = "Gestionar")
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
                Home(
                    onOpenScan = { navController.navigate(ROUTE_SCAN) },
                    onOpenManage = { navController.navigate(ROUTE_MANAGE) },
                    viewModel = filaViewModel
                )
            }
            /*
            composable(ROUTE_ROWS) {
                Rows(viewModel = filaViewModel)
            }
*/
            composable(ROUTE_CREATE) {
                Create(
                    viewModel = filaViewModel,
                    onInsert = { texto, a, b, c, d ->
                        //filaViewModel.insertarFila(texto,a)
                    }
                )
            }

            composable(ROUTE_SCAN) {
                Scan(
                    viewModel = filaViewModel,
                    items = sample,
                    onScan = { navController.navigate(ROUTE_CAMERA) },
                    scannedCode = scannedCode,
                    onJoin = {
                        scannedCode = ""
                        navController.navigate(ROUTE_HOME)
                    }
                )
            }

            composable(ROUTE_MANAGE) {
                Manage(filaViewModel)
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