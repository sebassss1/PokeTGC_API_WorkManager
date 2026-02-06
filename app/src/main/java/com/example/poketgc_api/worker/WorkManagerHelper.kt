package com.example.poketgc_api.worker

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object WorkManagerHelper {

    private const val SYNC_WORK_NAME = "poke_sync_work"

    /**
     * Programa una tarea de sincronización única.
     */
    fun scheduleOneTimeSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Solo si hay internet
            .setRequiresBatteryNotLow(true)               // Solo si tiene batería suficiente
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .addTag("sync")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            SYNC_WORK_NAME,
            ExistingWorkPolicy.REPLACE, // Reemplaza si ya hay una pendiente
            syncRequest
        )
    }

    /**
     * Programa una tarea de sincronización periódica (cada 24 horas por ejemplo).
     */
    fun schedulePeriodicSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED) // Solo WiFi
            .setRequiresCharging(true)                    // Solo cargando
            .build()

        val periodicRequest = PeriodicWorkRequestBuilder<SyncWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "periodic_sync",
            ExistingPeriodicWorkPolicy.KEEP, // Mantiene la existente si ya está programada
            periodicRequest
        )
    }
}
