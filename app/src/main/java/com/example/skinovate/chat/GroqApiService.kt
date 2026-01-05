package com.example.skinovate.chat

import android.content.Context
import com.example.skinovate.R
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class GroqMessage(
    val role: String, // "user" or "assistant" or "system"
    val content: String
)

data class GroqChatRequest(
    val messages: List<GroqMessage>,
    val model: String = "llama-3.1-8b-instant", // Fast and efficient model
    val temperature: Double = 0.7,
    val max_tokens: Int = 1024
)

data class GroqChoice(
    val message: GroqMessage,
    val finish_reason: String?
)

data class GroqResponse(
    val choices: List<GroqChoice>
)

class GroqApiService(private val apiKey: String) {
    
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            gson()
        }
    }
    
    private val baseUrl = "https://api.groq.com/openai/v1/chat/completions"
    
    companion object {
        // System prompt untuk membatasi hanya permasalahan wajah/skincare
        private const val SYSTEM_PROMPT = """Anda adalah asisten konsultasi skincare yang ahli. 
Anda hanya dapat menjawab pertanyaan terkait:
- Permasalahan kulit wajah (jerawat, flek hitam, kulit kering, berminyak, sensitif, dll)
- Perawatan kulit wajah (skincare routine, produk skincare, tips perawatan)
- Rekomendasi produk skincare
- Tips kesehatan kulit wajah

Jika user bertanya di luar topik skincare/permasalahan wajah, tolak dengan sopan dan arahkan kembali ke topik skincare.

Jawablah dalam Bahasa Indonesia dengan bahasa yang ramah dan mudah dipahami."""
    }
    
    suspend fun sendMessage(userMessage: String, conversationHistory: List<GroqMessage> = emptyList()): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                // Build messages dengan system prompt
                val messages = mutableListOf<GroqMessage>().apply {
                    add(GroqMessage("system", SYSTEM_PROMPT))
                    addAll(conversationHistory)
                    add(GroqMessage("user", userMessage))
                }
                
                val request = GroqChatRequest(messages = messages)
                
                val response: GroqResponse = client.post(baseUrl) {
                    header(HttpHeaders.Authorization, "Bearer $apiKey")
                    header(HttpHeaders.ContentType, ContentType.Application.Json)
                    setBody(request)
                }.body()
                
                val assistantMessage = response.choices.firstOrNull()?.message?.content
                    ?: "Maaf, terjadi kesalahan. Silakan coba lagi."
                
                Result.success(assistantMessage)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    fun isValidQuestion(question: String): Boolean {
        // Keywords terkait skincare/permasalahan wajah
        val skincareKeywords = listOf(
            "jerawat", "acne", "kulit", "wajah", "skincare", "perawatan",
            "cleanser", "toner", "serum", "moisturizer", "sunscreen",
            "flek", "hitam", "kering", "berminyak", "sensitif", "kombinasi",
            "pori", "komedo", "bekas", "scar", "pigmentasi", "hiperpigmentasi",
            "rutinitas", "routine", "produk", "rekomendasi", "tips", "cara",
            "masker", "exfoliator", "retinol", "vitamin c", "niacinamide"
        )
        
        val lowerQuestion = question.lowercase()
        return skincareKeywords.any { keyword -> lowerQuestion.contains(keyword) }
    }
}

