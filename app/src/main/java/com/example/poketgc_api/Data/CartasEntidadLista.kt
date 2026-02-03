package com.example.poketgc_api.Data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Entidad de Room que representa el vínculo entre una lista de usuario y una carta Pokémon.
 * Implementa una relación de muchos a muchos simplificada (una carta puede estar en una lista).
 * Incluye una clave foránea que apunta a [UsuarioEntidadLista] con borrado en cascada.
 *
 * @property listId ID de la lista a la que pertenece la carta (Clave primaria compuesta).
 * @property cardId ID único de la carta (Clave primaria compuesta).
 * @property localId ID local de la carta.
 * @property nombre Nombre de la carta.
 * @property imagen URL de la imagen.
 * @property rarity Rareza de la carta.
 * @property types Tipos de la carta concatenados por comas.
 */
@Entity(
    tableName = "list_cards",
    primaryKeys = ["listId", "cardId"],
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntidadLista::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("listId")]
)
data class ListCardEntity(
    val listId: Long,
    val cardId: String,
    val localId: String?,
    val nombre: String?,
    val imagen: String?,
    val rarity: String?,
    val types: String?
)
