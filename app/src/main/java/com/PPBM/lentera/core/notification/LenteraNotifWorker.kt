package com.PPBM.lentera.core.notification

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.PPBM.lentera.backend.core.SessionManager
import com.PPBM.lentera.backend.core.SupabaseConfig
import com.PPBM.lentera.backend.mahasiswa.repository.MahasiswaRepository
import io.github.jan.supabase.auth.auth
import java.util.concurrent.TimeUnit

/**
 * Worker latar belakang untuk memeriksa notifikasi baru saat aplikasi ditutup atau di background (N1).
 */
class LenteraNotifWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val session = SessionManager.getInstance(applicationContext)
        if (!session.isLoggedIn()) return Result.success()

        return try {
            runCatching { SupabaseConfig.client.auth.awaitInitialization() }
            if (SupabaseConfig.client.auth.currentUserOrNull() == null) {
                return Result.success()
            }

            val repo = MahasiswaRepository()
            val notifs = repo.getNotifikasi().getOrNull().orEmpty()
            if (notifs.isNotEmpty()) {
                val sentIds = LenteraNotificationHelper.showPendingServerNotifications(
                    applicationContext,
                    notifs
                )
                if (sentIds.isNotEmpty()) {
                    repo.tandaiNotifikasiTerkirim(sentIds)
                }
            }
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "lentera_notif_sync_work"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<LenteraNotifWorker>(
                15,
                TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context.applicationContext).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
