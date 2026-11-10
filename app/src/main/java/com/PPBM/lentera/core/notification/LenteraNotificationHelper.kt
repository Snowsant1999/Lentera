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
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.PPBM.lentera.MainActivity
import com.PPBM.lentera.R

object LenteraNotificationHelper {
    const val BASE_CHANNEL_ID = "lentera_system_notifications"
    const val CHANNEL_NAME = "Notifikasi LENTERA"
    private const val PREFS_NAME = "lentera_notif_prefs"
    private const val KEY_ALERTED_IDS = "alerted_notification_ids"

    fun getNotificationChannelId(context: Context): String {
        val soundResId = resolveCustomSoundResId(context)
        return if (soundResId != null) "${BASE_CHANNEL_ID}_custom_$soundResId" else BASE_CHANNEL_ID
    }

    private fun resolveCustomSoundResId(context: Context): Int? {
        val candidates = listOf("notif_sound", "lentera_notif", "custom_notif", "sound_notif")
        for (name in candidates) {
            val id = context.resources.getIdentifier(name, "raw", context.packageName)
            if (id != 0) return id
        }
        return null
    }

    fun resolveSoundUri(context: Context): Uri {
        val resId = resolveCustomSoundResId(context)
        return if (resId != null) {
            Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.packageName}/$resId")
        } else {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = getNotificationChannelId(context)
            val soundUri = resolveSoundUri(context)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val channel = NotificationChannel(
                channelId,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Saluran notifikasi pembaruan layanan dan operasional LENTERA"
                enableLights(true)
                enableVibration(true)
                setSound(soundUri, audioAttributes)
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }

    fun showSystemNotification(
        context: Context,
        title: String,
        message: String,
        notificationKey: String? = null
    ) {
        val appContext = context.applicationContext
        createNotificationChannel(appContext)

        // Cegah notifikasi yang sama berbunyi berulang kali jika sudah pernah ditampilkan
        if (!notificationKey.isNullOrBlank()) {
            val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val alerted = prefs.getStringSet(KEY_ALERTED_IDS, emptySet()) ?: emptySet()
            if (alerted.contains(notificationKey)) {
                return
            }
            prefs.edit().putStringSet(KEY_ALERTED_IDS, alerted + notificationKey).apply()
        }

        // Cek izin notifikasi Android 13+ (POST_NOTIFICATIONS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val channelId = getNotificationChannelId(appContext)
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

        val notification = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
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
}
