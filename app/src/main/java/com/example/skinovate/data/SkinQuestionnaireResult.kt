package com.example.skinovate.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

/**
 * Data class untuk hasil questionnaire analisis kulit
 */
data class SkinQuestionnaireResult(
    val skinType: String, // oily, dry, combination, sensitive, normal
    val sensitivity: String, // low, medium, high
    val elasticity: String, // low, medium, high
    val acneTendency: String, // none, low, medium, high
    val skinColor: String, // fair, medium, olive, tan, dark
    val texture: String, // smooth, rough, uneven, combination
    val recommendation: String,
    val date: String = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
)

/**
 * Repository untuk menyimpan hasil questionnaire
 */
object SkinQuestionnaireRepository {
    
    private val _lastResult = MutableStateFlow<SkinQuestionnaireResult?>(null)
    val lastResult: StateFlow<SkinQuestionnaireResult?> = _lastResult.asStateFlow()
    
    /**
     * Save hasil questionnaire
     */
    fun saveResult(result: SkinQuestionnaireResult) {
        _lastResult.value = result
        // TODO: Save to database if needed
    }
    
    /**
     * Get hasil terakhir
     */
    fun getLastResult(): SkinQuestionnaireResult? = _lastResult.value
}

