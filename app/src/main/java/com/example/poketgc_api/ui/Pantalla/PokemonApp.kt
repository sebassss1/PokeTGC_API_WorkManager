package com.example.poketgc_api.ui.Pantalla

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.poketgc_api.Data.ApiClient
import com.example.poketgc_api.Data.ListCardEntity
import com.example.poketgc_api.Data.PokeCard
import com.example.poketgc_api.Data.PokemonDAO
import com.example.poketgc_api.Data.UsuarioEntidadLista
import com.example.poketgc_api.Data.model.Screen
import com.example.poketgc_api.Data.model.UserListUI
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonApp(isDarkMode: Boolean, onDarkModeToggle: (Boolean) -> Unit, dao: PokemonDAO) {
    var cards by remember { mutableStateOf<List<PokeCard>>(emptyList()) }

    // Listas cargadas desde Room
    val userLists = remember { mutableStateListOf<UserListUI>() }
    var activeList by remember { mutableStateOf<UserListUI?>(null) }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isAscending by remember { mutableStateOf(true) }
    var currentScreen by remember { mutableStateOf(Screen.Home) }

    var searchQuery by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedRarity by remember { mutableStateOf("Todas") }
    var selectedType by remember { mutableStateOf("Todos") }

    var showNewListDialog by remember { mutableStateOf(false) }
    var newListName by remember { mutableStateOf("") }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val api = remember { ApiClient.api }

    // Cargar cartas de la API
    LaunchedEffect(Unit) {
        try {
            Log.d("PokemonApp", "Iniciando carga de cartas...")
            val result = api.getAllCard()
            Log.d("PokemonApp", "Cartas recibidas: ${result.size}")
            cards = result.take(200)
            isLoading = false
        } catch (e: Exception) {
            Log.e("PokemonApp", "Error cargando cartas: ${e.message}", e)
            errorMessage = e.message
            isLoading = false
        }
    }

    // Cargar listas desde Room al iniciar
    LaunchedEffect(Unit) {
        dao.getAllLists().collect { entities ->
            userLists.clear()
            entities.forEach { entity ->
                val uiList = UserListUI(entity)
                userLists.add(uiList)
                // Cargar cartas para esta lista
                launch {
                    dao.getCardsForList(entity.id).collect { cardEntities ->
                        uiList.cards.clear()
                        uiList.cards.addAll(cardEntities.map {
                            PokeCard(
                                it.cardId,
                                it.localId,
                                it.nombre,
                                it.imagen,
                                it.rarity,
                                it.types?.split(",")
                            )
                        })
                    }
                }
            }
        }
    }

    val filteredAndSortedCards =
        remember(cards, isAscending, searchQuery, selectedRarity, selectedType) {
            cards.filter { card ->
                val matchesSearch =
                    searchQuery.isEmpty() || (card.nombre?.contains(searchQuery, ignoreCase = true)
                        ?: false)
                val matchesRarity = selectedRarity == "Todas" || card.rarity == selectedRarity
                val matchesType =
                    selectedType == "Todos" || (card.types?.contains(selectedType) ?: false)
                val isNotUnown = card.nombre != "Unown" && !card.imagen.isNullOrEmpty()
                matchesSearch && matchesRarity && matchesType && isNotUnown
            }.let { list ->
                if (isAscending) list.sortedBy { it.localId }
                else list.sortedByDescending { it.localId }
            }
        }

    val rarities = remember(cards) {
        listOf("Todas") + cards.mapNotNull { it.rarity }.distinct().sorted()
    }

    val types = remember(cards) {
        listOf("Todos") + cards.flatMap { it.types ?: emptyList() }.distinct().sorted()
    }

    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("Filtros") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Rareza", style = MaterialTheme.typography.titleSmall)
                    rarities.forEach { rarity ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedRarity = rarity }) {
                            RadioButton(
                                selected = selectedRarity == rarity,
                                onClick = { selectedRarity = rarity })
                            Text(rarity)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Tipo", style = MaterialTheme.typography.titleSmall)
                    types.forEach { type ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedType = type }) {
                            RadioButton(
                                selected = selectedType == type,
                                onClick = { selectedType = type })
                            Text(type)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showFilterDialog = false
                }) { Text("Aceptar") }
            }
        )
    }

    if (showNewListDialog) {
        AlertDialog(
            onDismissRequest = { showNewListDialog = false },
            title = { Text("Nueva Lista") },
            text = {
                TextField(
                    value = newListName,
                    onValueChange = { newListName = it },
                    placeholder = { Text("Nombre") })
            },
            confirmButton = {
                Button(onClick = {
                    if (newListName.isNotBlank()) {
                        scope.launch {
                            val id = dao.insertList(UsuarioEntidadLista(name = newListName))
                            // El LaunchedEffect cargará la nueva lista automáticamente
                            newListName = ""
                            showNewListDialog = false
                        }
                    }
                }) { Text("Crear") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showNewListDialog = false
                }) { Text("Cancelar") }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "PokeTGC Menu",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Inicio") },
                    selected = currentScreen == Screen.Home,
                    onClick = { currentScreen = Screen.Home; scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Home, null) }
                )
                NavigationDrawerItem(
                    label = { Text("Mis Listas (${userLists.size})") },
                    selected = currentScreen == Screen.MyLists,
                    onClick = {
                        currentScreen = Screen.MyLists; scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.List, null) }
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode, null)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Modo Oscuro")
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(checked = isDarkMode, onCheckedChange = onDarkModeToggle)
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = {
                            when (currentScreen) {
                                Screen.ViewList -> currentScreen = Screen.MyLists
                                Screen.EditList -> currentScreen = Screen.ViewList
                                else -> scope.launch { drawerState.open() }
                            }
                        }) {
                            Icon(
                                if (currentScreen == Screen.ViewList || currentScreen == Screen.EditList) Icons.Default.ArrowBack else Icons.Default.Menu,
                                null,
                                tint = Color.White
                            )
                        }
                    },
                    title = {
                        if (currentScreen == Screen.EditList) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = {
                                    Text(
                                        "Buscar...",
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    cursorColor = Color.White,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                        } else {
                            Text(
                                if (currentScreen == Screen.ViewList) activeList?.entity?.name
                                    ?: "Lista" else "PokeTGC API", color = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAF50)),
                    actions = {
                        if (currentScreen == Screen.Home || currentScreen == Screen.EditList) {
                            IconButton(onClick = {
                                showFilterDialog = true
                            }) { Icon(Icons.Default.FilterList, null, tint = Color.White) }
                            IconButton(onClick = {
                                isAscending = !isAscending
                            }) {
                                Icon(
                                    if (isAscending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                    null,
                                    tint = Color.White
                                )
                            }
                        } else if (currentScreen == Screen.ViewList) {
                            IconButton(onClick = {
                                currentScreen = Screen.EditList
                            }) { Icon(Icons.Default.Add, null, tint = Color.White) }
                        }
                    }
                )
            },
            floatingActionButton = {
                if (currentScreen == Screen.MyLists) {
                    ExtendedFloatingActionButton(
                        onClick = { showNewListDialog = true },
                        icon = { Icon(Icons.Default.Create, null) },
                        text = { Text("Nueva Lista") },
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    )
                }
            }
        ) { padding ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(padding)) {
                when (currentScreen) {
                    Screen.Home -> {
                        if (isLoading) {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = Color(0xFF4CAF50))
                                Spacer(Modifier.height(8.dp))
                                Text("Cargando cartas...")
                            }
                        } else if (errorMessage != null) {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Error: $errorMessage", color = Color.Red)
                                Button(onClick = {
                                    isLoading = true
                                    errorMessage = null
                                    scope.launch {
                                        try {
                                            cards = api.getAllCard().take(200)
                                            isLoading = false
                                        } catch (e: Exception) {
                                            errorMessage = e.message
                                            isLoading = false
                                        }
                                    }
                                }) { Text("Reintentar") }
                            }
                        } else if (filteredAndSortedCards.isEmpty()) {
                            Text("No se encontraron cartas", modifier = Modifier.align(Alignment.Center))
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(8.dp)
                            ) {
                                items(filteredAndSortedCards) { card ->
                                    PokemonPantalla(
                                        pokeCard = card,
                                        isAdded = false,
                                        onToggleList = {})
                                }
                            }
                        }
                    }

                    Screen.MyLists -> {
                        LazyColumn(Modifier
                            .fillMaxSize()
                            .padding(8.dp)) {
                            items(userLists) { list ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            activeList = list; currentScreen = Screen.ViewList
                                        }) {
                                    Row(
                                        Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Folder, null, tint = Color(0xFF4CAF50))
                                        Spacer(Modifier.width(16.dp))
                                        Column {
                                            Text(list.entity.name); Text(
                                            "${list.cards.size} cartas",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        }
                                        Spacer(Modifier.weight(1f))
                                        IconButton(onClick = { scope.launch { dao.deleteList(list.entity) } }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                null,
                                                tint = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Screen.ViewList -> {
                        activeList?.let { list ->
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(8.dp)
                            ) {
                                items(list.cards) { card ->
                                    PokemonPantalla(
                                        pokeCard = card,
                                        isAdded = true,
                                        onToggleList = {
                                            scope.launch {
                                                val cardId = it.id ?: return@launch
                                                dao.deleteCardFromList(list.entity.id, cardId)
                                            }
                                        })
                                }
                            }
                        }
                    }

                    Screen.EditList -> {
                        activeList?.let { list ->
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(8.dp)
                            ) {
                                items(filteredAndSortedCards) { card ->
                                    val inList = list.cards.any { it.id == card.id }

                                    PokemonPantalla(
                                        pokeCard = card,
                                        isAdded = inList,
                                        onToggleList = { selected ->
                                            scope.launch {
                                                // selected es la carta que te llega del componente
                                                val cardId = selected.id
                                                if (cardId.isNullOrBlank()) {
                                                    // Si no hay id, no se puede guardar en Room (porque cardId es clave)
                                                    return@launch
                                                }

                                                val listId = list.entity.id

                                                if (inList) {
                                                    dao.deleteCardFromList(listId, cardId)
                                                } else {
                                                    dao.insertCard(
                                                        ListCardEntity(
                                                            listId = listId,
                                                            cardId = cardId,
                                                            localId = selected.localId,
                                                            nombre = selected.nombre,
                                                            imagen = selected.imagen,
                                                            rarity = selected.rarity,
                                                            types = selected.types?.joinToString(",")
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    )
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}
