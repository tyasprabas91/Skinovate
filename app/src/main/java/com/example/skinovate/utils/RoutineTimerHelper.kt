package com.example.skinovate.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object RoutineTimerHelper {
    
    private const val CHANNEL_ID_TIMER = "routine_timer_channel"
    private const val NOTIFICATION_ID_TIMER = 2000
    
    private var timerJob: Job? = null
    private val _timerState = MutableStateFlow<TimerState>(TimerState.Idle)
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()
    
    private var mediaPlayer: MediaPlayer? = null
    
    sealed class TimerState {
        data object Idle : TimerState()
        data class Running(val remainingSeconds: Int, val totalSeconds: Int) : TimerState()
        data object Finished : TimerState()
    }
    
    /**
     * Create notification channel for timer
     */
    fun createTimerChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_TIMER,
                "Timer Rutinitas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi ketika timer rutinitas skincare selesai"
                enableVibration(true)
                enableLights(true)
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private var onTimerFinished: (() -> Unit)? = null
    
    /**
     * Start timer for a routine step (duration in seconds)
     */
    fun startTimer(durationSeconds: Int, stepName: String, context: Context, onFinished: (() -> Unit)? = null) {
        // Cancel existing timer if any
        cancelTimer()
        
        currentStepName = stepName
        onTimerFinished = onFinished
        _timerState.value = TimerState.Running(durationSeconds, durationSeconds)
        
        timerJob = CoroutineScope(Dispatchers.Main).launch {
            var remaining = durationSeconds
            
            while (remaining > 0 && isActive) {
                delay(1000) // Wait 1 second
                remaining--
                _timerState.value = TimerState.Running(remaining, durationSeconds)
            }
            
            if (isActive) {
                // Timer finished
                _timerState.value = TimerState.Finished
                playAlarmSound(context)
                showTimerFinishedNotification(context, stepName)
                onTimerFinished?.invoke()
            }
        }
    }
    
    /**
     * Cancel current timer
     */
    fun cancelTimer() {
        timerJob?.cancel()
        timerJob = null
        _timerState.value = TimerState.Idle
        stopAlarmSound()
        onTimerFinished = null
    }
    
    /**
     * Get current step name for the timer
     */
    var currentStepName: String = ""
        private set
    
    /**
     * Play alarm sound when timer finishes
     */
    private fun playAlarmSound(context: Context) {
        try {
            stopAlarmSound() // Stop any existing sound
            
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            mediaPlayer = MediaPlayer.create(context, alarmUri)
            mediaPlayer?.apply {
                isLooping = false
                start()
                setOnCompletionListener {
                    release()
                    mediaPlayer = null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Stop alarm sound
     */
    private fun stopAlarmSound() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
            mediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Show notification when timer finishes
     */
    private fun showTimerFinishedNotification(context: Context, stepName: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_TIMER)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Timer Selesai!")
            .setContentText("Waktu untuk $stepName telah habis")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .build()
        
        try {
            @Suppress("DEPRECATION")
            notificationManager.notify(NOTIFICATION_ID_TIMER, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Format seconds to MM:SS format
     */
    fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }
}

