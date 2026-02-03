package com.example.poketgc_api.Data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de Room que representa una lista de colección creada por el usuario.
 * Cada instancia de esta clase corresponde a una fila en la tabla `user_lists`.
 *
 * @property id Clave primaria autogenerada que identifica de forma única cada lista.
 * @property name Nombre de la lista, definido por el usuario.
 */
@Entity(tableName = "user_lists")
data class UsuarioEntidadLista(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
) {
}
