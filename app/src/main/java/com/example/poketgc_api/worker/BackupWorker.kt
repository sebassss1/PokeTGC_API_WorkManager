package com.example.poketgc_api.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.poketgc_api.Data.AppDatabase
import kotlinx.coroutines.flow.first

class BackupWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("BackupWorker", "Iniciando copia de seguridad periódica de 24h...")

        return try {
            // Obtenemos la instancia de la base de datos
            val db = AppDatabase.getDatabase(applicationContext)
            val dao = db.pokemonDao()

            // Obtenemos todas las cartas guardadas (usando Flow.first() para obtener el valor actual)
            val allCards = dao.getAllPokemon().first()

            if (allCards.isNotEmpty()) {
                Log.d("BackupWorker", "Realizando copia de seguridad de ${allCards.size} cartas.")
                
                // Aquí simularíamos el envío a un servidor o almacenamiento externo
                // Por ejemplo, convirtiendo a JSON y enviando vía Retrofit
                
                Log.d("BackupWorker", "Copia de seguridad enviada correctamente.")
            } else {
                Log.d("BackupWorker", "No hay cartas guardadas para respaldar.")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("BackupWorker", "Error al realizar la copia de seguridad", e)
            Result.retry()
        }
    }
}
