package com.example.skinovate.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

object NotificationHelper {
    const val CHANNEL_ID_ROUTINE = "routine_reminders"
    const val CHANNEL_ID_PRODUCT = "product_recommendations"
    const val CHANNEL_ID_SCAN = "scan_reminders"
    const val CHANNEL_ID_WEEKLY = "weekly_reports"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Routine Reminders Channel
            val routineChannel = NotificationChannel(
                CHANNEL_ID_ROUTINE,
                "Pengingat Rutinitas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi untuk mengingatkan rutinitas skincare pagi dan malam"
                enableVibration(true)
                enableLights(true)
            }

            // Product Recommendations Channel
            val productChannel = NotificationChannel(
                CHANNEL_ID_PRODUCT,
                "Rekomendasi Produk",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifikasi tentang produk baru yang cocok untuk kulitmu"
            }

            // Scan Reminders Channel
            val scanChannel = NotificationChannel(
                CHANNEL_ID_SCAN,
                "Pengingat Scan",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Ingatkan untuk melakukan face analysis secara berkala"
            }

            // Weekly Reports Channel
            val weeklyChannel = NotificationChannel(
                CHANNEL_ID_WEEKLY,
                "Laporan Mingguan",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Ringkasan progress skincaremu setiap minggu"
            }

            notificationManager.createNotificationChannels(
                listOf(routineChannel, productChannel, scanChannel, weeklyChannel)
            )
        }
    }

    fun createRoutineNotification(
        context: Context,
        title: String,
        message: String
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID_ROUTINE)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
    }
}

