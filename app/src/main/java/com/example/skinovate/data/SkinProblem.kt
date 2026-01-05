package com.example.skinovate.data

/**
 * Data model untuk permasalahan kulit wajah
 */
data class SkinProblem(
    val id: String,
    val title: String,
    val description: String,
    val icon: String, // Emoji atau icon identifier
    val causes: List<String>,
    val symptoms: List<String>,
    val prevention: List<String>,
    val treatment: List<String>,
    val recommendedCategories: List<String>, // Kategori produk yang direkomendasikan
    val recommendedProductIds: List<String> = emptyList() // ID produk spesifik yang direkomendasikan
)

/**
 * Repository untuk permasalahan kulit
 */
object SkinProblemRepository {
    
    private val problemsList = listOf(
        SkinProblem(
            id = "inflamed_acne",
            title = "Jerawat Radang",
            description = "Jerawat radang adalah jerawat yang meradang, biasanya berwarna merah, bengkak, dan terasa sakit. Jenis jerawat ini terjadi ketika pori-pori tersumbat oleh minyak, sel kulit mati, dan bakteri.",
            icon = "🔴",
            causes = listOf(
                "Produksi minyak berlebih",
                "Penumpukan sel kulit mati",
                "Bakteri Propionibacterium acnes",
                "Hormon yang tidak seimbang",
                "Pori-pori tersumbat",
                "Peradangan pada folikel rambut"
            ),
            symptoms = listOf(
                "Benjolan merah dan bengkak",
                "Terasa sakit saat disentuh",
                "Kulit kemerahan di sekitar jerawat",
                "Kadang disertai nanah"
            ),
            prevention = listOf(
                "Cuci muka 2 kali sehari dengan cleanser lembut",
                "Jangan memencet jerawat",
                "Gunakan produk non-comedogenic",
                "Jaga kebersihan handphone dan bantal",
                "Kurangi makanan berminyak dan tinggi gula",
                "Kelola stres dengan baik"
            ),
            treatment = listOf(
                "Gunakan cleanser dengan salicylic acid atau benzoyl peroxide",
                "Aplikasikan spot treatment dengan kandungan anti-inflamasi",
                "Gunakan toner dengan kandungan niacinamide",
                "Moisturize dengan produk non-comedogenic",
                "Gunakan sunscreen setiap hari",
                "Konsultasi dengan dermatologis jika parah"
            ),
            recommendedCategories = listOf("Cleanser", "Toner", "Serum", "Moisturizer"),
            recommendedProductIds = listOf("cl003", "tn002", "sr001", "ms001")
        ),
        
        SkinProblem(
            id = "small_acne",
            title = "Jerawat Kecil",
            description = "Jerawat kecil (komedo dan whiteheads) adalah pori-pori yang tersumbat. Komedo hitam terjadi ketika pori terbuka dan teroksidasi, sedangkan whiteheads adalah pori tertutup.",
            icon = "⚪",
            causes = listOf(
                "Pori-pori tersumbat oleh sebum",
                "Penumpukan sel kulit mati",
                "Pemakaian makeup yang tidak dibersihkan dengan benar",
                "Produk skincare yang terlalu berat",
                "Faktor genetik"
            ),
            symptoms = listOf(
                "Bintik hitam kecil di hidung dan dagu (komedo)",
                "Benjolan putih kecil (whiteheads)",
                "Tekstur kulit tidak merata",
                "Pori-pori terlihat besar"
            ),
            prevention = listOf(
                "Double cleansing di malam hari",
                "Eksfoliasi 2-3 kali seminggu",
                "Gunakan produk oil-free",
                "Cuci muka setelah berkeringat",
                "Jangan menyentuh wajah dengan tangan kotor"
            ),
            treatment = listOf(
                "Gunakan cleanser dengan AHA/BHA",
                "Eksfoliasi dengan produk mengandung salicylic acid",
                "Gunakan toner untuk menyeimbangkan pH",
                "Aplikasikan serum dengan niacinamide",
                "Gunakan clay mask 1-2 kali seminggu"
            ),
            recommendedCategories = listOf("Cleanser", "Exfoliator", "Toner", "Serum"),
            recommendedProductIds = listOf("cl001", "ex001", "tn001", "sr002")
        ),
        
        SkinProblem(
            id = "acne_scars",
            title = "Bekas Jerawat",
            description = "Bekas jerawat adalah bekas luka yang tertinggal setelah jerawat sembuh. Ada berbagai jenis bekas jerawat seperti hiperpigmentasi (bekas hitam), atrophic scars (lekukan), dan hypertrophic scars (menonjol).",
            icon = "🔵",
            causes = listOf(
                "Memencet jerawat",
                "Jerawat radang yang parah",
                "Proses penyembuhan yang tidak optimal",
                "Paparan sinar matahari berlebih",
                "Genetik"
            ),
            symptoms = listOf(
                "Bekas hitam atau coklat (hiperpigmentasi)",
                "Lekukan pada kulit (atrophic scars)",
                "Benjolan menonjol (hypertrophic scars)",
                "Tekstur kulit tidak merata"
            ),
            prevention = listOf(
                "Jangan memencet jerawat",
                "Gunakan sunscreen setiap hari",
                "Rawat jerawat dengan benar sejak awal",
                "Gunakan produk dengan kandungan anti-inflamasi",
                "Hindari paparan sinar matahari langsung"
            ),
            treatment = listOf(
                "Gunakan serum dengan vitamin C untuk mencerahkan",
                "Aplikasikan produk dengan retinol untuk regenerasi sel",
                "Gunakan serum dengan niacinamide untuk mengurangi kemerahan",
                "Eksfoliasi rutin dengan AHA",
                "Gunakan produk dengan centella asiatica untuk mempercepat penyembuhan",
                "Konsultasi untuk perawatan laser jika parah"
            ),
            recommendedCategories = listOf("Serum", "Retinol", "Exfoliator", "Moisturizer"),
            recommendedProductIds = listOf("sr003", "rt001", "ex001", "ms004")
        ),
        
        SkinProblem(
            id = "dark_spots",
            title = "Flek Hitam & Hiperpigmentasi",
            description = "Flek hitam atau hiperpigmentasi adalah kondisi dimana area kulit menjadi lebih gelap dari sekitarnya akibat produksi melanin berlebih. Bisa disebabkan oleh jerawat, paparan matahari, atau perubahan hormon.",
            icon = "🌑",
            causes = listOf(
                "Paparan sinar UV berlebihan",
                "Bekas jerawat yang tidak dirawat",
                "Perubahan hormon (kehamilan, pil KB)",
                "Peradangan kulit",
                "Penuaan"
            ),
            symptoms = listOf(
                "Bintik hitam atau coklat pada wajah",
                "Warna kulit tidak merata",
                "Flek lebih terlihat setelah paparan matahari",
                "Kulit terlihat kusam"
            ),
            prevention = listOf(
                "Gunakan sunscreen SPF 30+ setiap hari",
                "Hindari paparan sinar matahari langsung",
                "Pakai topi atau payung saat siang hari",
                "Jangan memencet jerawat",
                "Gunakan produk dengan anti-oksidan"
            ),
            treatment = listOf(
                "Gunakan serum dengan vitamin C di pagi hari",
                "Aplikasikan produk dengan niacinamide",
                "Gunakan retinol di malam hari",
                "Eksfoliasi dengan AHA secara rutin",
                "Gunakan produk dengan arbutin atau kojic acid",
                "Gunakan sunscreen setiap hari tanpa terkecuali"
            ),
            recommendedCategories = listOf("Serum", "Retinol", "Sunscreen", "Exfoliator"),
            recommendedProductIds = listOf("sr003", "rt001", "ss001", "ex001")
        ),
        
        SkinProblem(
            id = "oily_skin",
            title = "Kulit Berminyak",
            description = "Kulit berminyak terjadi ketika kelenjar sebaceous memproduksi sebum berlebih. Kulit terlihat mengilap, pori-pori besar, dan rentan berjerawat.",
            icon = "💧",
            causes = listOf(
                "Produksi sebum berlebih secara genetik",
                "Perubahan hormon",
                "Cuaca panas dan lembab",
                "Penggunaan produk skincare yang terlalu berat",
                "Mencuci wajah terlalu sering"
            ),
            symptoms = listOf(
                "Wajah terlihat mengilap terutama zona T",
                "Pori-pori besar",
                "Makeup cepat luntur",
                "Rentan berjerawat",
                "Kulit terlihat kusam"
            ),
            prevention = listOf(
                "Cuci muka 2 kali sehari, tidak lebih",
                "Gunakan produk oil-free dan non-comedogenic",
                "Hindari over-washing yang dapat memicu produksi minyak",
                "Gunakan blotting paper jika perlu",
                "Jaga kebersihan handphone dan bantal"
            ),
            treatment = listOf(
                "Gunakan cleanser gel dengan salicylic acid",
                "Toner dengan kandungan witch hazel atau niacinamide",
                "Serum dengan niacinamide untuk kontrol sebum",
                "Moisturizer gel atau lotion ringan",
                "Clay mask 1-2 kali seminggu",
                "Sunscreen non-comedogenic wajib digunakan"
            ),
            recommendedCategories = listOf("Cleanser", "Toner", "Serum", "Moisturizer"),
            recommendedProductIds = listOf("cl001", "tn002", "sr001", "ms002")
        ),
        
        SkinProblem(
            id = "dry_skin",
            title = "Kulit Kering",
            description = "Kulit kering terjadi ketika kulit tidak mampu mempertahankan kelembaban dengan baik. Kulit terasa kencang, kasar, dan kadang mengelupas.",
            icon = "🏜️",
            causes = listOf(
                "Produksi sebum kurang",
                "Cuaca dingin dan kering",
                "Penggunaan produk dengan alkohol",
                "Pencucian wajah terlalu sering",
                "Kondisi medis tertentu"
            ),
            symptoms = listOf(
                "Kulit terasa kencang setelah cuci muka",
                "Kulit kasar dan mengelupas",
                "Garis halus lebih terlihat",
                "Kemerahan dan iritasi",
                "Kulit terlihat kusam"
            ),
            prevention = listOf(
                "Gunakan cleanser lembut tanpa sulfat",
                "Hindari air terlalu panas",
                "Gunakan humidifier di ruangan",
                "Minum air putih yang cukup",
                "Hindari produk dengan alkohol tinggi"
            ),
            treatment = listOf(
                "Cleanser cream atau oil-based",
                "Toner hydrating tanpa alkohol",
                "Serum dengan hyaluronic acid",
                "Moisturizer kaya dengan ceramides",
                "Face oil untuk locking moisture",
                "Gunakan sunscreen untuk melindungi skin barrier"
            ),
            recommendedCategories = listOf("Cleanser", "Toner", "Serum", "Moisturizer"),
            recommendedProductIds = listOf("cl006", "tn003", "sr004", "ms003")
        ),
        
        SkinProblem(
            id = "sensitive_skin",
            title = "Kulit Sensitif",
            description = "Kulit sensitif mudah mengalami reaksi seperti kemerahan, gatal, atau iritasi terhadap produk atau faktor lingkungan tertentu.",
            icon = "🌿",
            causes = listOf(
                "Skin barrier rusak",
                "Reaksi alergi terhadap bahan tertentu",
                "Faktor genetik",
                "Penggunaan produk terlalu keras",
                "Perubahan cuaca ekstrem"
            ),
            symptoms = listOf(
                "Kemerahan dan iritasi",
                "Kulit terasa gatal atau terbakar",
                "Reaksi terhadap produk tertentu",
                "Kulit mudah memerah",
                "Kulit terlihat tipis dan rapuh"
            ),
            prevention = listOf(
                "Hindari produk dengan fragrance",
                "Patch test produk baru",
                "Gunakan produk dengan minimal ingredients",
                "Hindari eksfoliasi terlalu sering",
                "Jaga skin barrier dengan baik"
            ),
            treatment = listOf(
                "Cleanser lembut tanpa sulfat dan fragrance",
                "Toner dengan centella asiatica atau chamomile",
                "Serum dengan niacinamide untuk memperbaiki barrier",
                "Moisturizer dengan ceramides dan fatty acids",
                "Sunscreen mineral untuk perlindungan maksimal",
                "Hindari produk dengan alkohol, AHA/BHA kuat"
            ),
            recommendedCategories = listOf("Cleanser", "Toner", "Serum", "Moisturizer", "Sunscreen"),
            recommendedProductIds = listOf("cl004", "tn004", "sr005", "ms001", "ss002")
        ),
        
        SkinProblem(
            id = "large_pores",
            title = "Pori-Pori Besar",
            description = "Pori-pori besar adalah kondisi dimana pori-pori terlihat lebih besar dari normalnya. Biasanya terjadi di zona T dan pipi.",
            icon = "🔍",
            causes = listOf(
                "Faktor genetik",
                "Produksi sebum berlebih",
                "Penumpukan sel kulit mati",
                "Penuaan dan hilangnya elastisitas kulit",
                "Paparan sinar matahari berlebihan"
            ),
            symptoms = listOf(
                "Pori-pori terlihat jelas terutama di hidung dan pipi",
                "Kulit terlihat tidak halus",
                "Makeup mudah masuk ke pori-pori",
                "Rentan komedo"
            ),
            prevention = listOf(
                "Jaga kebersihan wajah",
                "Eksfoliasi rutin",
                "Gunakan sunscreen",
                "Hindari memencet komedo",
                "Gunakan produk non-comedogenic"
            ),
            treatment = listOf(
                "Double cleansing untuk membersihkan pori",
                "Toner dengan BHA (salicylic acid)",
                "Serum dengan niacinamide",
                "Clay mask untuk deep cleansing",
                "Retinol untuk meningkatkan elastisitas",
                "Sunscreen untuk mencegah kerusakan lebih lanjut"
            ),
            recommendedCategories = listOf("Cleanser", "Toner", "Serum", "Exfoliator"),
            recommendedProductIds = listOf("cl005", "tn002", "sr001", "ex001")
        )
    )
    
    fun getAllProblems(): List<SkinProblem> {
        return problemsList
    }
    
    fun getProblemById(id: String): SkinProblem? {
        return problemsList.find { it.id == id }
    }
}

