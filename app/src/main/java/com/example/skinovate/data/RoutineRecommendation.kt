package com.example.skinovate.data

/**
 * Data class untuk rekomendasi rutinitas skincare
 */
data class RoutineRecommendation(
    val id: String,
    val title: String,
    val description: String,
    val icon: String, // Emoji atau icon identifier
    val morningRoutine: Routine,
    val eveningRoutine: Routine,
    val benefits: List<String>,
    val suitableFor: List<String>
)

/**
 * Repository untuk rekomendasi rutinitas
 */
object RoutineRecommendationRepository {
    
    /**
     * Rekomendasi Rutin Pemula
     * Cocok untuk yang baru mulai skincare routine
     */
    val beginnerRoutine = RoutineRecommendation(
        id = "beginner",
        title = "Rekomendasi Rutin Pemula",
        description = "Rutinitas sederhana untuk pemula yang baru memulai perawatan kulit",
        icon = "🌱",
        morningRoutine = Routine(
            title = "Morning Routine - Pemula",
            time = "08:00 AM",
            steps = listOf(
                RoutineStep(type = SkincareStep.CLEANSER, productName = "Gentle Cleanser", duration = 60),
                RoutineStep(type = SkincareStep.MOISTURIZER, productName = "Lightweight Moisturizer", duration = 60),
                RoutineStep(type = SkincareStep.SUNSCREEN, productName = "SPF 30+", duration = 60)
            )
        ),
        eveningRoutine = Routine(
            title = "Evening Routine - Pemula",
            time = "09:00 PM",
            steps = listOf(
                RoutineStep(type = SkincareStep.CLEANSER, productName = "Gentle Cleanser", duration = 60),
                RoutineStep(type = SkincareStep.MOISTURIZER, productName = "Lightweight Moisturizer", duration = 60)
            )
        ),
        benefits = listOf(
            "Mudah diikuti untuk pemula",
            "Hanya 3 langkah di pagi hari",
            "Membantu membangun kebiasaan skincare",
            "Minimal risiko iritasi"
        ),
        suitableFor = listOf(
            "Pemula skincare",
            "Kulit normal",
            "Yang ingin rutinitas sederhana"
        )
    )
    
    /**
     * Bersihkan dan Seimbangkan
     * Fokus pada pembersihan dan keseimbangan kulit
     */
    val cleanseAndBalance = RoutineRecommendation(
        id = "cleanse_balance",
        title = "Bersihkan dan Seimbangkan",
        description = "Rutinitas untuk membersihkan kulit secara menyeluruh dan menyeimbangkan pH kulit",
        icon = "💧",
        morningRoutine = Routine(
            title = "Morning Routine - Bersihkan & Seimbangkan",
            time = "08:00 AM",
            steps = listOf(
                RoutineStep(type = SkincareStep.CLEANSER, productName = "Balancing Cleanser", duration = 60),
                RoutineStep(type = SkincareStep.TONER, productName = "pH Balancing Toner", duration = 60),
                RoutineStep(type = SkincareStep.SERUM, productName = "Hydrating Serum", duration = 60),
                RoutineStep(type = SkincareStep.MOISTURIZER, productName = "Balancing Moisturizer", duration = 60),
                RoutineStep(type = SkincareStep.SUNSCREEN, productName = "SPF 50", duration = 60)
            )
        ),
        eveningRoutine = Routine(
            title = "Evening Routine - Bersihkan & Seimbangkan",
            time = "09:00 PM",
            steps = listOf(
                RoutineStep(type = SkincareStep.CLEANSER, productName = "Oil Cleanser", duration = 60),
                RoutineStep(type = SkincareStep.CLEANSER, productName = "Water-based Cleanser", duration = 60),
                RoutineStep(type = SkincareStep.TONER, productName = "pH Balancing Toner", duration = 60),
                RoutineStep(type = SkincareStep.SERUM, productName = "Niacinamide Serum", duration = 60),
                RoutineStep(type = SkincareStep.MOISTURIZER, productName = "Balancing Moisturizer", duration = 60)
            )
        ),
        benefits = listOf(
            "Double cleansing untuk pembersihan maksimal",
            "Menyeimbangkan pH kulit",
            "Mengontrol produksi minyak",
            "Mencegah pori-pori tersumbat"
        ),
        suitableFor = listOf(
            "Kulit berminyak",
            "Kulit kombinasi",
            "Kulit dengan pori-pori besar"
        )
    )
    
    /**
     * Kencangkan dan Pulihkan
     * Fokus pada firming dan recovery kulit
     */
    val firmAndRecover = RoutineRecommendation(
        id = "firm_recover",
        title = "Kencangkan dan Pulihkan",
        description = "Rutinitas untuk mengencangkan kulit dan mempercepat pemulihan",
        icon = "✨",
        morningRoutine = Routine(
            title = "Morning Routine - Kencangkan & Pulihkan",
            time = "08:00 AM",
            steps = listOf(
                RoutineStep(type = SkincareStep.CLEANSER, productName = "Gentle Cleanser", duration = 60),
                RoutineStep(type = SkincareStep.TONER, productName = "Firming Toner", duration = 60),
                RoutineStep(type = SkincareStep.SERUM, productName = "Peptide Serum", duration = 60),
                RoutineStep(type = SkincareStep.EYE_CREAM, productName = "Firming Eye Cream", duration = 60),
                RoutineStep(type = SkincareStep.MOISTURIZER, productName = "Firming Moisturizer", duration = 60),
                RoutineStep(type = SkincareStep.SUNSCREEN, productName = "SPF 50", duration = 60)
            )
        ),
        eveningRoutine = Routine(
            title = "Evening Routine - Kencangkan & Pulihkan",
            time = "09:00 PM",
            steps = listOf(
                RoutineStep(type = SkincareStep.CLEANSER, productName = "Gentle Cleanser", duration = 60),
                RoutineStep(type = SkincareStep.TONER, productName = "Firming Toner", duration = 60),
                RoutineStep(type = SkincareStep.SERUM, productName = "Retinol Serum", duration = 60),
                RoutineStep(type = SkincareStep.EYE_CREAM, productName = "Firming Eye Cream", duration = 60),
                RoutineStep(type = SkincareStep.MOISTURIZER, productName = "Recovery Moisturizer", duration = 60)
            )
        ),
        benefits = listOf(
            "Mengencangkan kulit",
            "Meningkatkan elastisitas",
            "Mempercepat regenerasi sel",
            "Mengurangi tanda penuaan"
        ),
        suitableFor = listOf(
            "Kulit mulai menua",
            "Kulit kendur",
            "Yang ingin anti-aging"
        )
    )
    
    /**
     * Ratakan dan Cerahkan
     * Fokus pada even skin tone dan brightening
     */
    val evenAndBrighten = RoutineRecommendation(
        id = "even_brighten",
        title = "Ratakan dan Cerahkan",
        description = "Rutinitas untuk meratakan warna kulit dan mencerahkan wajah",
        icon = "🌟",
        morningRoutine = Routine(
            title = "Morning Routine - Ratakan & Cerahkan",
            time = "08:00 AM",
            steps = listOf(
                RoutineStep(type = SkincareStep.CLEANSER, productName = "Brightening Cleanser", duration = 60),
                RoutineStep(type = SkincareStep.TONER, productName = "Vitamin C Toner", duration = 60),
                RoutineStep(type = SkincareStep.SERUM, productName = "Vitamin C Serum", duration = 60),
                RoutineStep(type = SkincareStep.MOISTURIZER, productName = "Brightening Moisturizer", duration = 60),
                RoutineStep(type = SkincareStep.SUNSCREEN, productName = "SPF 50", duration = 60)
            )
        ),
        eveningRoutine = Routine(
            title = "Evening Routine - Ratakan & Cerahkan",
            time = "09:00 PM",
            steps = listOf(
                RoutineStep(type = SkincareStep.CLEANSER, productName = "Gentle Cleanser", duration = 60),
                RoutineStep(type = SkincareStep.TONER, productName = "AHA/BHA Toner", duration = 60),
                RoutineStep(type = SkincareStep.SERUM, productName = "Niacinamide Serum", duration = 60),
                RoutineStep(type = SkincareStep.SERUM, productName = "Arbutin Serum", duration = 60),
                RoutineStep(type = SkincareStep.MOISTURIZER, productName = "Brightening Moisturizer", duration = 60)
            )
        ),
        benefits = listOf(
            "Meratakan warna kulit",
            "Mencerahkan wajah",
            "Mengurangi dark spots",
            "Meningkatkan glow alami"
        ),
        suitableFor = listOf(
            "Kulit dengan dark spots",
            "Kulit kusam",
            "Hyperpigmentation",
            "Yang ingin kulit lebih cerah"
        )
    )
    
    /**
     * Haluskan dan Perbaiki
     * Fokus pada texture improvement dan repair
     */
    val smoothAndRepair = RoutineRecommendation(
        id = "smooth_repair",
        title = "Haluskan dan Perbaiki",
        description = "Rutinitas untuk memperbaiki tekstur kulit dan memperbaiki kerusakan",
        icon = "🔧",
        morningRoutine = Routine(
            title = "Morning Routine - Haluskan & Perbaiki",
            time = "08:00 AM",
            steps = listOf(
                RoutineStep(type = SkincareStep.CLEANSER, productName = "Gentle Cleanser", duration = 60),
                RoutineStep(type = SkincareStep.TONER, productName = "Hydrating Toner", duration = 60),
                RoutineStep(type = SkincareStep.SERUM, productName = "Ceramide Serum", duration = 60),
                RoutineStep(type = SkincareStep.MOISTURIZER, productName = "Repair Moisturizer", duration = 60),
                RoutineStep(type = SkincareStep.SUNSCREEN, productName = "SPF 50", duration = 60)
            )
        ),
        eveningRoutine = Routine(
            title = "Evening Routine - Haluskan & Perbaiki",
            time = "09:00 PM",
            steps = listOf(
                RoutineStep(type = SkincareStep.CLEANSER, productName = "Gentle Cleanser", duration = 60),
                RoutineStep(type = SkincareStep.EXFOLIATOR, productName = "AHA Exfoliator", duration = 60),
                RoutineStep(type = SkincareStep.TONER, productName = "Hydrating Toner", duration = 60),
                RoutineStep(type = SkincareStep.SERUM, productName = "Ceramide Serum", duration = 60),
                RoutineStep(type = SkincareStep.MOISTURIZER, productName = "Repair Moisturizer", duration = 60)
            )
        ),
        benefits = listOf(
            "Memperbaiki tekstur kulit",
            "Menghaluskan permukaan kulit",
            "Memperbaiki skin barrier",
            "Mengurangi bekas jerawat"
        ),
        suitableFor = listOf(
            "Kulit kasar",
            "Bekas jerawat",
            "Kulit rusak",
            "Yang ingin tekstur lebih halus"
        )
    )
    
    /**
     * Tenang dan Redakan
     * Fokus pada calming dan soothing untuk kulit sensitif
     */
    val calmAndSoothe = RoutineRecommendation(
        id = "calm_soothe",
        title = "Tenang dan Redakan",
        description = "Rutinitas untuk menenangkan dan meredakan kulit sensitif atau iritasi",
        icon = "🌿",
        morningRoutine = Routine(
            title = "Morning Routine - Tenang & Redakan",
            time = "08:00 AM",
            steps = listOf(
                RoutineStep(type = SkincareStep.CLEANSER, productName = "Gentle Cleanser", duration = 60),
                RoutineStep(type = SkincareStep.TONER, productName = "Calming Toner", duration = 60),
                RoutineStep(type = SkincareStep.SERUM, productName = "Centella Serum", duration = 60),
                RoutineStep(type = SkincareStep.MOISTURIZER, productName = "Soothing Moisturizer", duration = 60),
                RoutineStep(type = SkincareStep.SUNSCREEN, productName = "Mineral SPF 50", duration = 60)
            )
        ),
        eveningRoutine = Routine(
            title = "Evening Routine - Tenang & Redakan",
            time = "09:00 PM",
            steps = listOf(
                RoutineStep(type = SkincareStep.CLEANSER, productName = "Gentle Cleanser", duration = 60),
                RoutineStep(type = SkincareStep.TONER, productName = "Calming Toner", duration = 60),
                RoutineStep(type = SkincareStep.SERUM, productName = "Centella Serum", duration = 60),
                RoutineStep(type = SkincareStep.MOISTURIZER, productName = "Soothing Moisturizer", duration = 60),
                RoutineStep(type = SkincareStep.FACE_MASK, productName = "Calming Mask (2x/week)", duration = 60)
            )
        ),
        benefits = listOf(
            "Menghilangkan kemerahan",
            "Meredakan iritasi",
            "Menyejukkan kulit",
            "Memperkuat skin barrier"
        ),
        suitableFor = listOf(
            "Kulit sensitif",
            "Kulit iritasi",
            "Rosacea",
            "Kulit kemerahan"
        )
    )
    
    /**
     * Get all recommendations
     */
    fun getAllRecommendations(): List<RoutineRecommendation> {
        return try {
            listOf(
                beginnerRoutine,
                cleanseAndBalance,
                firmAndRecover,
                evenAndBrighten,
                smoothAndRepair,
                calmAndSoothe
            )
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    /**
     * Get recommendation by ID
     */
    fun getRecommendationById(id: String): RoutineRecommendation? {
        return getAllRecommendations().find { it.id == id }
    }
    
    /**
     * Get recommendation based on skin analysis result
     */
    fun getRecommendationForAnalysis(
        skinType: String,
        sensitivity: String,
        acneTendency: String,
        texture: String
    ): RoutineRecommendation {
        // Scoring system: higher score = better match
        val scores = getAllRecommendations().associateWith { recommendation ->
            var score = 0
            
            // Match based on skin type
            val skinTypeLower = skinType.lowercase()
            recommendation.suitableFor.forEach { condition ->
                val conditionLower = condition.lowercase()
                when {
                    skinTypeLower == "oily" && (conditionLower.contains("berminyak") || conditionLower.contains("oily")) -> score += 3
                    skinTypeLower == "dry" && conditionLower.contains("kering") -> score += 3
                    skinTypeLower == "sensitive" && (conditionLower.contains("sensitif") || conditionLower.contains("sensitive")) -> score += 3
                    skinTypeLower == "combination" && conditionLower.contains("kombinasi") -> score += 3
                    skinTypeLower == "normal" -> score += 1 // Normal skin can use most routines
                }
            }
            
            // Match based on sensitivity
            if (sensitivity.lowercase() == "high") {
                if (recommendation.id == "calm_soothe") score += 5
                else if (recommendation.id == "beginner") score += 2
            }
            
            // Match based on acne tendency
            if (acneTendency.lowercase() in listOf("medium", "high")) {
                if (recommendation.id == "cleanse_balance") score += 4
                else if (recommendation.id == "smooth_repair") score += 3
            }
            
            // Match based on texture
            if (texture.lowercase() in listOf("rough", "uneven")) {
                if (recommendation.id == "smooth_repair") score += 4
                else if (recommendation.id == "even_brighten") score += 3
            }
            
            score
        }
        
        // Return recommendation with highest score, default to beginner if no good match
        return scores.maxByOrNull { it.value }?.key ?: beginnerRoutine
    }
}



