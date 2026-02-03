package com.example.poketgc_api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.poketgc_api.Data.PokeCard
import com.example.poketgc_api.Data.PokemonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona el estado de las cartas Pokémon para la interfaz de usuario.
 * Utiliza el repositorio para obtener datos y los expone mediante un StateFlow reactivo.
 */
class PokemonViewModel(private val repository: PokemonRepository) : ViewModel() {

    private val _pokeCards = MutableStateFlow<List<PokeCard>>(emptyList())
    /**
     * StateFlow que emite la lista actual de cartas cargadas.
     */
    val pokeCards: StateFlow<List<PokeCard>> = _pokeCards

    init {
        // Carga inicial de cartas al crear el ViewModel
        viewModelScope.launch {
            _pokeCards.value = repository.getAllCards()
        }
    }
}

/**
 * Factoría para crear instancias de [PokemonViewModel] con sus dependencias.
 * Necesaria para pasar el [PokemonRepository] al constructor del ViewModel.
 */
class PokemonViewModelFactory(private val repository: PokemonRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PokemonViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PokemonViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
