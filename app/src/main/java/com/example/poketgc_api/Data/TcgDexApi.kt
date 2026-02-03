package com.example.poketgc_api.Data

import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Interfaz que define los endpoints de la API de TCGdex utilizando Retrofit.
 * Especifica las operaciones de red disponibles para obtener datos de cartas Pokémon.
 */
interface TcgDexApi {
    /**
     * Obtiene una lista de todas las cartas Pokémon disponibles en inglés.
     * @return Una lista de objetos [PokeCard].
     */
    @GET("en/cards")
    suspend fun getAllCard(): List<PokeCard>

    /**
     * Obtiene una carta específica por su ID.
     * @param id El ID de la carta a solicitar.
     * @return Un objeto [PokeCard] que corresponde al ID proporcionado.
     */
    @GET("en/cards/{id}")
    suspend fun getCardById(
        @Path("id") id: String
    ): PokeCard
}
