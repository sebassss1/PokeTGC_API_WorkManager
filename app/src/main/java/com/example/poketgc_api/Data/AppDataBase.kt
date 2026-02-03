package com.example.poketgc_api.Data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Punto de acceso principal para la base de datos Room.
 * Define las entidades que componen la base de datos y provee el DAO para interactuar con ellas.
 * Implementa el patrón Singleton para asegurar una única instancia de la base de datos.
 */
@Database(
    entities = [UsuarioEntidadLista::class, ListCardEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Provee el DAO necesario para realizar operaciones sobre las tablas de la base de datos.
     */
    abstract fun pokemonDao(): PokemonDAO

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        /**
         * Obtiene la instancia de la base de datos, creándola si no existe.
         * @param context Contexto de la aplicación.
         * @return Instancia única de [AppDatabase].
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "poketgc.db" // nombre del fichero real en el móvil
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
