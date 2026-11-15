package com.PPBM.lentera.core.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.PPBM.lentera.MainActivity
import com.PPBM.lentera.R

/**
 * Pengelola Notifikasi Khusus LENTERA (N1).
 * - Menggunakan channel tetap berversi: `lentera_alerts_v1` (N1.1).
 * - Membersihkan saluran lama yang mengunci suara default.
 * - Menggunakan berkas suara khas aplikasi: `res/raw/notif_sound.wav` (N1.2).
 * - Urutan pengecekan izin sebelum kunci deduplikasi (N1.5).
 * - Ikon kecil monokrom khusus `ic_notification_small` (N1.7).
 * - Fitur uji bunyi dan getar (N1.8).
 */
object LenteraNotificationHelper {
    const val CHANNEL_ID = "lentera_alerts_v1"
    const val CHANNEL_NAME = "Notifikasi LENTERA"
    private const val PREFS_NAME = "lentera_notif_prefs"
    private const val KEY_ALERTED_IDS = "alerted_notification_ids"

    fun getNotificationChannelId(context: Context): String = CHANNEL_ID

    fun resolveSoundUri(context: Context): Uri {
        return try {
            Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.packageName}/${R.raw.notif_sound}")
        } catch (_: Exception) {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }
    }

    /**
     * Memeriksa apakah izin notifikasi aktif di HP.
     */
    fun isNotificationPermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    /**
     * Membuat saluran notifikasi `lentera_alerts_v1` dan menghapus saluran usang.
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

            // Hapus channel lama agar tidak menumpuk dan suara tidak terkunci di HP pengguna
            try {
                manager.notificationChannels?.forEach { ch ->
                    if (ch.id != CHANNEL_ID) {
                        manager.deleteNotificationChannel(ch.id)
                    }
                }
            } catch (_: Exception) {}

            val soundUri = resolveSoundUri(context)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Saluran notifikasi pembaruan layanan dan operasional LENTERA"
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 200, 300)
                setSound(soundUri, audioAttributes)
            }

            manager.createNotificationChannel(channel)
        }
    }

    /**
     * Menampilkan notifikasi sistem dengan suara dan getar khas Lentera.
     */
    fun showSystemNotification(
        context: Context,
        title: String,
        message: String,
        notificationKey: String? = null
    ) {
        val appContext = context.applicationContext
        createNotificationChannel(appContext)

        // 1. Cek izin notifikasi TERLEBIH DAHULU (N1.5)
        // Jika izin belum diberikan, jangan tandai kunci sebagai terkirim!
        if (!isNotificationPermissionGranted(appContext)) {
            return
        }

        // 2. Cegah notifikasi duplikat jika kunci sudah pernah ditampilkan
        if (!notificationKey.isNullOrBlank()) {
            val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val alerted = prefs.getStringSet(KEY_ALERTED_IDS, emptySet()) ?: emptySet()
            if (alerted.contains(notificationKey)) {
                return
            }
            prefs.edit().putStringSet(KEY_ALERTED_IDS, alerted + notificationKey).apply()
        }

        val soundUri = resolveSoundUri(appContext)
        val intent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            appContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_small)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 300, 200, 300))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        runCatching {
            val notificationId = (notificationKey?.hashCode() ?: System.currentTimeMillis().toInt())
            NotificationManagerCompat.from(appContext).notify(notificationId, notification)
        }
    }

    /**
     * Uji bunyi audio notifikasi Lentera langsung melalui MediaPlayer (N1.8).
     */
    fun playTestSound(context: Context) {
        runCatching {
            val mediaPlayer = MediaPlayer.create(context.applicationContext, R.raw.notif_sound)
            mediaPlayer.setOnCompletionListener { mp -> mp.release() }
            mediaPlayer.start()
        }
    }

    /**
     * Uji sistem notifikasi lengkap: menampilkan notifikasi pop-up dan bersuara (N1.8).
     */
    fun triggerTestNotification(context: Context) {
        showSystemNotification(
            context = context,
            title = "Uji Notifikasi LENTERA",
            message = "Suara dan getar khas aplikasi berhasil aktif di HP kamu.",
            notificationKey = "test_alert_${System.currentTimeMillis()}"
        )
    }

    /**
     * Membuka pengaturan notifikasi aplikasi di Pengaturan Sistem Android.
     */
    fun openNotificationSettings(context: Context) {
        runCatching {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
            } else {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}
