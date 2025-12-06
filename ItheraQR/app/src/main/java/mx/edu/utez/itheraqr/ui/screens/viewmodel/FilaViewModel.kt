package mx.edu.utez.itheraqr.ui.screens.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import mx.edu.utez.itheraqr.data.network.model.Fila
import mx.edu.utez.itheraqr.data.network.repository.FilaRepository

class FilaViewModel(application: Application) : AndroidViewModel(application) {

    val repository = FilaRepository(application.applicationContext)

    val listaFilas = MutableStateFlow<List<Fila>>(emptyList())
    val errores = MutableStateFlow<String>("")

    val resultadoFormarse = MutableStateFlow<Boolean>(false)

    fun getFilas() {
        repository.getAll(
            onSuccess = { lista ->
                listaFilas.value = lista
            },
            onError = { mensaje ->
                errores.value = mensaje
            }
        )
    }

    // Función para formarse
    fun formarse(idFila: String, idUsuario: String) {
        repository.formarse(idFila, idUsuario,
            onSuccess = { exito ->
                resultadoFormarse.value = exito
            },
            onError = { mensaje ->
                errores.value = mensaje
            }
        )
    }
}