package com.example.skinovate.notifications

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.example.skinovate.data.RoutineRepository
import java.util.*
import java.util.concurrent.TimeUnit

class RoutineNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val routineTitle = inputData.getString(KEY_ROUTINE_TITLE) ?: "Rutinitas Skincare"
            val routineMessage = inputData.getString(KEY_ROUTINE_MESSAGE) ?: "Waktunya untuk rutinitas skincaremu!"
            val workName = inputData.getString(KEY_WORK_NAME) ?: ""

            val notification = NotificationHelper.createRoutineNotification(
                context = applicationContext,
                title = routineTitle,
                message = routineMessage
            ).build()

            val notificationManager = NotificationManagerCompat.from(applicationContext)
            val notificationId = inputData.getInt(KEY_NOTIFICATION_ID, System.currentTimeMillis().toInt())

            notificationManager.notify(notificationId, notification)

            // Schedule next notification for tomorrow (24 hours from now)
            scheduleNextNotification(workName)

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private suspend fun scheduleNextNotification(workName: String) {
        val workManager = WorkManager.getInstance(applicationContext)
        
        // Get routine based on work name
        val routine = when (workName) {
            RoutineNotificationManager.WORK_NAME_MORNING -> RoutineRepository.morningRoutine.value
            RoutineNotificationManager.WORK_NAME_EVENING -> RoutineRepository.eveningRoutine.value
            else -> return
        }
        
        val notificationId = when (workName) {
            RoutineNotificationManager.WORK_NAME_MORNING -> RoutineNotificationManager.NOTIFICATION_ID_MORNING
            RoutineNotificationManager.WORK_NAME_EVENING -> RoutineNotificationManager.NOTIFICATION_ID_EVENING
            else -> return
        }
        
        // Parse time and calculate delay for next day (always schedule for tomorrow at the same time)
        val targetTime = parseTimeForTomorrow(routine.time)
        val now = Calendar.getInstance()
        val nextDelay = targetTime.timeInMillis - now.timeInMillis
        
        val nextWorkRequest = OneTimeWorkRequestBuilder<RoutineNotificationWorker>()
            .setInitialDelay(nextDelay, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    KEY_ROUTINE_TITLE to routine.title,
                    KEY_ROUTINE_MESSAGE to "Waktunya untuk ${routine.title}! ${routine.steps.size} langkah menantimu.",
                    KEY_NOTIFICATION_ID to notificationId,
                    KEY_WORK_NAME to workName
                )
            )
            .addTag(workName)
            .build()
        
        workManager.enqueue(nextWorkRequest)
    }

    private fun parseTimeForTomorrow(timeString: String): Calendar {
        val calendar = Calendar.getInstance()
        try {
            val parts = timeString.split(" ")
            val timePart = parts[0] // "08:00"
            val amPm = if (parts.size > 1) parts[1] else "AM" // "AM" or "PM"
            
            val timeParts = timePart.split(":")
            var hour = timeParts[0].toInt()
            val minute = timeParts[1].toInt()
            
            // Convert to 24-hour format
            if (amPm.equals("PM", ignoreCase = true) && hour != 12) {
                hour += 12
            } else if (amPm.equals("AM", ignoreCase = true) && hour == 12) {
                hour = 0
            }
            
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            
            // Always schedule for tomorrow
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        } catch (e: Exception) {
            // Default to 8:00 AM tomorrow if parsing fails
            calendar.set(Calendar.HOUR_OF_DAY, 8)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        
        return calendar
    }

    companion object {
        const val KEY_ROUTINE_TITLE = "routine_title"
        const val KEY_ROUTINE_MESSAGE = "routine_message"
        const val KEY_NOTIFICATION_ID = "notification_id"
        const val KEY_WORK_NAME = "work_name"
    }
}

