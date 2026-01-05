package com.example.skinovate.ai

import android.content.Context
import com.example.skinovate.R
import com.example.skinovate.chat.GroqApiService
import com.example.skinovate.data.SkinQuestionnaireResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Service untuk generate penjelasan dan rekomendasi menggunakan AI
 * berdasarkan hasil skin questionnaire
 */
class SkinAnalysisAiService(private val apiKey: String) {
    
    private val groqService = GroqApiService(apiKey)
    
    companion object {
        private const val SYSTEM_PROMPT = """Anda adalah dermatologis dan ahli skincare profesional.
Tugas Anda adalah menganalisis hasil questionnaire analisis kulit dan memberikan:
1. Penjelasan yang jelas dan mudah dipahami tentang kondisi kulit user
2. Rekomendasi produk skincare yang personalized
3. Tips perawatan yang spesifik

Gunakan Bahasa Indonesia yang ramah, profesional, dan mudah dipahami.
Berikan penjelasan yang detail namun tidak terlalu teknis.
"""
    }
    
    /**
     * Generate penjelasan lengkap tentang hasil analisis kulit
     */
    suspend fun generateExplanation(result: SkinQuestionnaireResult): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = buildExplanationPrompt(result)
                groqService.sendMessage(prompt)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Generate rekomendasi produk dan routine yang personalized
     */
    suspend fun generateRecommendation(result: SkinQuestionnaireResult): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = buildRecommendationPrompt(result)
                groqService.sendMessage(prompt)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Generate penjelasan + rekomendasi dalam satu response
     */
    suspend fun generateFullAnalysis(result: SkinQuestionnaireResult): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = buildFullAnalysisPrompt(result)
                groqService.sendMessage(prompt)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    private fun buildExplanationPrompt(result: SkinQuestionnaireResult): String {
        return """Berdasarkan hasil analisis kulit berikut, berikan penjelasan yang detail dan mudah dipahami:

**Hasil Analisis:**
- Tipe Kulit: ${result.skinType}
- Sensitivitas: ${result.sensitivity}
- Elastisitas: ${result.elasticity}
- Kecenderungan Jerawat: ${result.acneTendency}
- Warna Kulit: ${result.skinColor}
- Tekstur: ${result.texture}

Tolong jelaskan:
1. Apa artinya setiap aspek tersebut untuk kondisi kulit user
2. Apa kelebihan dan tantangan dari tipe kulit ini
3. Faktor-faktor yang perlu diperhatikan

Berikan penjelasan yang ramah, mudah dipahami, dan informatif dalam Bahasa Indonesia."""
    }
    
    private fun buildRecommendationPrompt(result: SkinQuestionnaireResult): String {
        return """Berdasarkan hasil analisis kulit berikut, berikan rekomendasi skincare yang personalized:

**Hasil Analisis:**
- Tipe Kulit: ${result.skinType}
- Sensitivitas: ${result.sensitivity}
- Elastisitas: ${result.elasticity}
- Kecenderungan Jerawat: ${result.acneTendency}
- Warna Kulit: ${result.skinColor}
- Tekstur: ${result.texture}

Tolong berikan rekomendasi:
1. Rutinitas skincare pagi dan malam yang cocok
2. Jenis produk yang direkomendasikan (cleanser, toner, serum, moisturizer, sunscreen)
3. Kandungan aktif yang baik untuk kondisi kulit ini
4. Kandungan yang sebaiknya dihindari
5. Tips perawatan khusus

Format rekomendasi dengan jelas dan mudah diikuti. Gunakan Bahasa Indonesia."""
    }
    
    private fun buildFullAnalysisPrompt(result: SkinQuestionnaireResult): String {
        return """Sebagai dermatologis profesional, analisis hasil questionnaire berikut dan berikan penjelasan lengkap + rekomendasi:

**Hasil Analisis Kulit:**
- Tipe Kulit: ${result.skinType}
- Tingkat Sensitivitas: ${result.sensitivity}
- Elastisitas: ${result.elasticity}
- Kecenderungan Jerawat: ${result.acneTendency}
- Warna Kulit: ${result.skinColor}
- Tekstur Kulit: ${result.texture}

Tolong berikan:
1. **Penjelasan Kondisi Kulit:**
   - Penjelasan setiap aspek (tipe kulit, sensitivitas, dll)
   - Arti dan implikasi untuk perawatan kulit
   - Kelebihan dan tantangan kondisi kulit ini

2. **Rekomendasi Perawatan:**
   - Rutinitas skincare pagi (step by step)
   - Rutinitas skincare malam (step by step)
   - Jenis produk yang direkomendasikan
   - Kandungan aktif yang baik
   - Kandungan yang harus dihindari

3. **Tips Tambahan:**
   - Tips perawatan harian
   - Hal-hal yang perlu diperhatikan
   - Konsultasi ke dermatologis jika perlu

Format jawaban dengan jelas, mudah dibaca, dan profesional. Gunakan Bahasa Indonesia."""
    }
}

