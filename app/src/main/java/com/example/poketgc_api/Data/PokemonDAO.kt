package com.example.poketgc_api.Data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/*
  Objeto de Acceso a Datos (DAO) para Room.
  Define las operaciones de base de datos para gestionar listas de usuarios y cartas asociadas.
 */
@Dao
interface PokemonDAO {
    /*
      Obtiene todas las listas creadas por el usuario, ordenadas por ID descendente.
      @return Un Flow que emite la lista actualizada de [UsuarioEntidadLista].
     */
    @Query("SELECT * FROM user_lists ORDER BY id DESC")
    fun getAllLists(): Flow<List<UsuarioEntidadLista>>

    /*
      Inserta una nueva lista en la base de datos.
      @param list La entidad de la lista a insertar.
      @return El ID de la fila insertada.
     */
    @Insert
    suspend fun insertList(list: UsuarioEntidadLista): Long

    /*
      Elimina una lista de la base de datos.
      @param list La entidad de la lista a eliminar.
     */
    @Delete
    suspend fun deleteList(list: UsuarioEntidadLista)

    /*
      Obtiene todas las cartas asociadas a una lista específica.
      @param listId El ID de la lista.
      @return Un Flow que emite las cartas de la lista.
     */
    @Query("SELECT * FROM list_cards WHERE listId = :listId")
    fun getCardsForList(listId: Long): Flow<List<ListCardEntity>>

    /**
     * Inserta o actualiza una carta en una lista.
     * @param card La entidad de la carta vinculada a la lista.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: ListCardEntity)

    /**
     * Elimina una carta específica de una lista determinada.
     * @param listId ID de la lista.
     * @param cardId ID de la carta.
     */
    @Query("DELETE FROM list_cards WHERE listId = :listId AND cardId = :cardId")
    suspend fun deleteCardFromList(listId: Long, cardId: String)
}
