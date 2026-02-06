package com.example.poketgc_api.Data.model

import androidx.compose.runtime.mutableStateListOf
import com.example.poketgc_api.Data.PokeCard
import com.example.poketgc_api.Data.UsuarioEntidadLista


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