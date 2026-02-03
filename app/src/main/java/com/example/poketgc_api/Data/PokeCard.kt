package com.example.poketgc_api.Data

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos que representa una carta Pokémon proveniente de la API de TCGdex.
 * Utiliza anotaciones de GSON para mapear los nombres de los campos de la respuesta JSON
 * a las propiedades de la clase en Kotlin.
 *
 * @property id Identificador único de la carta en la API.
 * @property localId Identificador local de la carta dentro de su set.
 * @property nombre Nombre de la carta (mapeado desde "name").
 * @property imagen URL de la imagen de la carta (mapeado desde "image").
 * @property rarity Rareza de la carta (ej. "Common", "Rare Holo").
 * @property types Lista de tipos asociados a la carta (ej. ["Fire", "Water"]).
 */
data class PokeCard(
    val id: String,
    val localId: String?,
    @SerializedName("name")
    val nombre: String?,
    @SerializedName("image")
    val imagen: String?,
    val rarity: String?,
    val types: List<String>?
)
