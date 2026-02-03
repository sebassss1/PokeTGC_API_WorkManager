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

/**
 * Enumeración que define las pantallas disponibles en la navegación de la aplicación.
 */
enum class Screen {
    Home, MyLists, ViewList, EditList
}

/**
 Representa la información de una lista de usuario optimizada para la UI.
  Incluye la entidad de la base de datos y la lista de cartas cargadas.

  @property entity Entidad de Room asociada a la lista.
 @property cards Lista observable de cartas pertenecientes a esta lista.
 */
class UserListUI(
    val entity: UsuarioEntidadLista,
    val cards: MutableList<PokeCard> = mutableStateListOf()
)


//Composable principal que define la estructura y lógica de navegación de la aplicación Pokémon.
//Gestiona el filtrado de cartas, la interacción con Room para las listas y el Drawer de navegación.

