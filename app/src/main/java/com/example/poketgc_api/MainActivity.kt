package com.example.poketgc_api

import com.example.poketgc_api.Data.UsuarioEntidadLista
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.poketgc_api.Data.*
import com.example.poketgc_api.ui.Pantalla.PokemonApp
import com.example.poketgc_api.ui.Pantalla.PokemonPantalla
import com.example.poketgc_api.ui.theme.PokeTGC_APITheme
import kotlinx.coroutines.launch

/**
 * Actividad principal de la aplicación.
 * Se encarga de inicializar la interfaz de usuario con Jetpack Compose,
 * gestionar el estado global de la navegación y coordinar el acceso a la base de datos y ajustes.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val managerAjustes = ManagerAjustes(this)
        val database = AppDatabase.getDatabase(this)
        val dao = database.pokemonDao()

        setContent {
            val isDarkMode by managerAjustes.isDarkMode.collectAsState(initial = false)
            val scope = rememberCoroutineScope()

            PokeTGC_APITheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PokemonApp(
                        isDarkMode = isDarkMode,
                        onDarkModeToggle = { enabled ->
                            scope.launch { managerAjustes.setDarkMode(enabled) }
                        },
                        dao = dao
                    )
                }
            }
        }
    }
}
