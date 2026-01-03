package com.example.skinovate.notifications

import android.content.Context
import androidx.work.*
import com.example.skinovate.data.RoutineRepository
import java.util.*
import java.util.concurrent.TimeUnit

object RoutineNotificationManager {
    
    const val WORK_NAME_MORNING = "routine_notification_morning"
    const val WORK_NAME_EVENING = "routine_notification_evening"
    const val NOTIFICATION_ID_MORNING = 1001
    const val NOTIFICATION_ID_EVENING = 1002

    /**
     * Parse time string seperti "08:00 AM" ke Calendar
     */
    fun parseTime(timeString: String): Calendar {
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
            
            // If time has passed today, schedule for tomorrow
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        } catch (e: Exception) {
            // Default to 8:00 AM if parsing fails
            calendar.set(Calendar.HOUR_OF_DAY, 8)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
        }
        
        return calendar
    }

    /**
     * Calculate delay in milliseconds until the target time
     */
    private fun calculateDelay(targetTime: Calendar): Long {
        val now = Calendar.getInstance()
        val delay = targetTime.timeInMillis - now.timeInMillis
        return if (delay > 0) delay else delay + TimeUnit.DAYS.toMillis(1) // If passed, schedule for next day
    }

    /**
     * Schedule routine notifications
     * Uses OneTimeWorkRequest with chaining for daily notifications
     */
    fun scheduleRoutineNotifications(context: Context) {
        val workManager = WorkManager.getInstance(context)
        
        // Cancel existing work first
        cancelRoutineNotifications(context)
        
        // Get routines
        val morningRoutine = RoutineRepository.morningRoutine.value
        val eveningRoutine = RoutineRepository.eveningRoutine.value
        
        // Schedule morning routine notification
        val morningTime = parseTime(morningRoutine.time)
        val morningDelay = calculateDelay(morningTime)
        
        val morningWorkRequest = OneTimeWorkRequestBuilder<RoutineNotificationWorker>()
            .setInitialDelay(morningDelay, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    RoutineNotificationWorker.KEY_ROUTINE_TITLE to morningRoutine.title,
                    RoutineNotificationWorker.KEY_ROUTINE_MESSAGE to "Waktunya untuk ${morningRoutine.title}! ${morningRoutine.steps.size} langkah menantimu.",
                    RoutineNotificationWorker.KEY_NOTIFICATION_ID to NOTIFICATION_ID_MORNING,
                    RoutineNotificationWorker.KEY_WORK_NAME to WORK_NAME_MORNING
                )
            )
            .addTag(WORK_NAME_MORNING)
            .build()
        
        // Schedule evening routine notification
        val eveningTime = parseTime(eveningRoutine.time)
        val eveningDelay = calculateDelay(eveningTime)
        
        val eveningWorkRequest = OneTimeWorkRequestBuilder<RoutineNotificationWorker>()
            .setInitialDelay(eveningDelay, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    RoutineNotificationWorker.KEY_ROUTINE_TITLE to eveningRoutine.title,
                    RoutineNotificationWorker.KEY_ROUTINE_MESSAGE to "Waktunya untuk ${eveningRoutine.title}! ${eveningRoutine.steps.size} langkah menantimu.",
                    RoutineNotificationWorker.KEY_NOTIFICATION_ID to NOTIFICATION_ID_EVENING,
                    RoutineNotificationWorker.KEY_WORK_NAME to WORK_NAME_EVENING
                )
            )
            .addTag(WORK_NAME_EVENING)
            .build()
        
        // Enqueue work requests
        workManager.enqueue(morningWorkRequest)
        workManager.enqueue(eveningWorkRequest)
    }

    /**
     * Cancel routine notifications
     */
    fun cancelRoutineNotifications(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWorkByTag(WORK_NAME_MORNING)
        workManager.cancelAllWorkByTag(WORK_NAME_EVENING)
    }

    /**
     * Reschedule notifications (call when routine time changes)
     */
    fun rescheduleRoutineNotifications(context: Context) {
        // Only reschedule if notifications are enabled
        if (NotificationSettingsRepository.routineRemindersEnabled.value) {
            scheduleRoutineNotifications(context)
        }
    }
}

