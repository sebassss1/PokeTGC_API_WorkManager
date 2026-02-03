package com.example.poketgc_api.Data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
  Singleton que configura y provee la instancia única de Retrofit para toda la aplicación.
  Centraliza la configuración de la URL base y el conversor de JSON (GSON).
 */
object ApiClient {
    // URL base de la API de TCGdex.
    private const val BASE_URL = "https://api.tcgdex.net/v2/"


     //Cliente HTTP con tiempos de espera configurados para manejar respuestas grandes.

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()


     // Instancia de la interfaz [TcgDexApi] creada mediante lazy initialization.
    // Se encarga de realizar las llamadas de red definidas en la interfaz.

    val api: TcgDexApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TcgDexApi::class.java)
    }
}
