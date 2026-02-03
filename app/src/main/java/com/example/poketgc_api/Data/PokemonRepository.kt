package com.example.poketgc_api.Data

/**
 * Clase mediadora que abstrae la fuente de datos (en este caso la API) del resto de la aplicación.
 * Sigue el patrón Repository para centralizar el acceso a los datos.
 *
 * @property api Instancia de [TcgDexApi] utilizada para realizar las peticiones de red.
 */
class PokemonRepository(private val api: TcgDexApi) {
    /**
     * Obtiene todas las cartas disponibles desde la API.
     * @return Una lista de objetos [PokeCard].
     */
    suspend fun getAllCards(): List<PokeCard> {
        return api.getAllCard()
    }
}
