package com.example.skinovate.notifications

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object NotificationSettingsRepository {
    private const val PREFS_NAME = "skinovate_notification_prefs"
    private const val KEY_ROUTINE_REMINDERS = "routine_reminders_enabled"
    private const val KEY_PRODUCT_RECOMMENDATIONS = "product_recommendations_enabled"
    private const val KEY_SCAN_REMINDERS = "scan_reminders_enabled"
    private const val KEY_WEEKLY_REPORTS = "weekly_reports_enabled"

    private lateinit var prefs: SharedPreferences

    private val _routineRemindersEnabled = MutableStateFlow(true)
    val routineRemindersEnabled: StateFlow<Boolean> = _routineRemindersEnabled.asStateFlow()

    private val _productRecommendationsEnabled = MutableStateFlow(true)
    val productRecommendationsEnabled: StateFlow<Boolean> = _productRecommendationsEnabled.asStateFlow()

    private val _scanRemindersEnabled = MutableStateFlow(false)
    val scanRemindersEnabled: StateFlow<Boolean> = _scanRemindersEnabled.asStateFlow()

    private val _weeklyReportsEnabled = MutableStateFlow(true)
    val weeklyReportsEnabled: StateFlow<Boolean> = _weeklyReportsEnabled.asStateFlow()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        
        // Load saved settings
        _routineRemindersEnabled.value = prefs.getBoolean(KEY_ROUTINE_REMINDERS, true)
        _productRecommendationsEnabled.value = prefs.getBoolean(KEY_PRODUCT_RECOMMENDATIONS, true)
        _scanRemindersEnabled.value = prefs.getBoolean(KEY_SCAN_REMINDERS, false)
        _weeklyReportsEnabled.value = prefs.getBoolean(KEY_WEEKLY_REPORTS, true)
    }

    fun setRoutineRemindersEnabled(enabled: Boolean, context: Context) {
        _routineRemindersEnabled.value = enabled
        prefs.edit().putBoolean(KEY_ROUTINE_REMINDERS, enabled).apply()
        
        // Schedule or cancel notifications based on setting
        if (enabled) {
            RoutineNotificationManager.scheduleRoutineNotifications(context)
        } else {
            RoutineNotificationManager.cancelRoutineNotifications(context)
        }
    }

    fun setProductRecommendationsEnabled(enabled: Boolean) {
        _productRecommendationsEnabled.value = enabled
        prefs.edit().putBoolean(KEY_PRODUCT_RECOMMENDATIONS, enabled).apply()
    }

    fun setScanRemindersEnabled(enabled: Boolean) {
        _scanRemindersEnabled.value = enabled
        prefs.edit().putBoolean(KEY_SCAN_REMINDERS, enabled).apply()
    }

    fun setWeeklyReportsEnabled(enabled: Boolean) {
        _weeklyReportsEnabled.value = enabled
        prefs.edit().putBoolean(KEY_WEEKLY_REPORTS, enabled).apply()
    }
}

