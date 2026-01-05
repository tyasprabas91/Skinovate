package com.example.skinovate.data

import android.graphics.BitmapFactory
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File

object GeminiRepository {

    // TODO: PASTE YOUR REAL API KEY HERE
    private const val API_KEY = "AIzaSyCS488h6xEFCPEje6k4rqbvSvRMgG8IeTE"

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = API_KEY
    )

    // --- FEATURE 1: FACE SCAN ANALYSIS ---
    suspend fun analyzeImage(imageFile: File): SkinAnalysisResult? = withContext(Dispatchers.IO) {
        try {
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath) ?: return@withContext null

            val prompt = """
                Analyze this face for skincare. Return raw JSON:
                {
                  "score": 0-100,
                  "skinType": "Oily/Dry/Combination/Normal",
                  "acnePercentage": 0-100,
                  "dryPercentage": 0-100,
                  "recommendation": "Short summary",
                  "tips": ["Tip 1", "Tip 2", "Tip 3"]
                }
            """.trimIndent()

            val response = generativeModel.generateContent(content {
                image(bitmap)
                text(prompt)
            })

            parseAnalysisResult(response.text ?: "")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // --- FEATURE 2: PERSONALIZED TIPS (Text Only) ---
    suspend fun getPersonalizedTips(skinType: String, concerns: String): List<String> = withContext(Dispatchers.IO) {
        try {
            val prompt = "Give me 5 short, actionable skincare tips for someone with $skinType skin who is worried about $concerns. Return ONLY the tips as a JSON Array of strings."

            val response = generativeModel.generateContent(prompt)
            parseTipsList(response.text ?: "")
        } catch (e: Exception) {
            listOf("Stay hydrated.", "Wear sunscreen daily.")
        }
    }

    // --- PARSING HELPERS ---
    private fun parseAnalysisResult(jsonString: String): SkinAnalysisResult {
        return try {
            val cleanJson = jsonString.replace("```json", "").replace("```", "").trim()
            val json = JSONObject(cleanJson)

            // Parse Tips
            val tipsList = mutableListOf<String>()
            val tipsArray = json.optJSONArray("tips")
            if (tipsArray != null) {
                for (i in 0 until tipsArray.length()) tipsList.add(tipsArray.getString(i))
            }

            SkinAnalysisResult(
                score = json.optInt("score", 75),
                skinType = json.optString("skinType", "Normal"),
                acnePercentage = json.optInt("acnePercentage", 10),
                dryPercentage = json.optInt("dryPercentage", 10),
                recommendation = json.optString("recommendation", "Maintain routine"),
                tips = tipsList
            )
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback
            SkinAnalysisResult(75, "Normal", 10, 10, "Could not parse AI response")
        }
    }

    private fun parseTipsList(jsonString: String): List<String> {
        val cleanJson = jsonString.replace("```json", "").replace("```", "").trim()
        val list = mutableListOf<String>()
        try {
            val jsonArray = org.json.JSONArray(cleanJson)
            for (i in 0 until jsonArray.length()) {
                list.add(jsonArray.getString(i))
            }
        } catch (e: Exception) {
            list.add(cleanJson)
        }
        return list
    }

    // Add this to GeminiRepository object
    suspend fun debugAvailableModels() = withContext(Dispatchers.IO) {
        try {
            val url = java.net.URL("https://generativelanguage.googleapis.com/v1beta/models?key=$API_KEY")
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "GET"

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            println("GEMINI AVAILABLE MODELS: $response") // Check Logcat for this!
        } catch (e: Exception) {
            println("GEMINI DEBUG ERROR: ${e.message}")
            // If this fails with 400/403, your API Key is invalid or API is disabled.
        }
    }
}