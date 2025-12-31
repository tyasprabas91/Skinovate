package com.example.skinovate.data

object ProductRepository {

    val allProducts = listOf(
        // CLEANSER
        Product(
            id = "cl001",
            name = "Niacinamide Gentle Gel Cleanser",
            brand = "Somethinc",
            category = "Cleanser",
            description = "Gentle gel cleanser dengan niacinamide untuk membersihkan wajah tanpa membuat kering. Cocok untuk semua jenis kulit.",
            price = 65000.0,
            targetSkinConditions = listOf("Acne", "Oily Skin", "Dull Skin"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),
        Product(
            id = "cl002",
            name = "Lightening Facial Wash",
            brand = "Wardah",
            category = "Cleanser",
            description = "Facial wash dengan formula lightening untuk kulit lebih cerah dan bersih.",
            price = 35000.0,
            targetSkinConditions = listOf("Dull Skin", "Dark Spots"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),
        Product(
            id = "cl003",
            name = "Facial Wash Acne",
            brand = "Scarlett",
            category = "Cleanser",
            description = "Pembersih wajah khusus untuk kulit berjerawat dengan formula anti-acne.",
            price = 45000.0,
            targetSkinConditions = listOf("Acne", "Oily Skin"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),
        Product(
            id = "cl004",
            name = "Low pH Good Morning Gel Cleanser",
            brand = "COSRX",
            category = "Cleanser",
            description = "Gentle gel cleanser dengan pH rendah untuk membersihkan tanpa mengganggu skin barrier.",
            price = 120000.0,
            targetSkinConditions = listOf("Sensitive Skin", "Acne"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),
        Product(
            id = "cl005",
            name = "Heartleaf Pore Control Cleansing Oil",
            brand = "Anua",
            category = "Cleanser",
            description = "Cleansing oil dengan heartleaf untuk kontrol pori dan membersihkan makeup.",
            price = 180000.0,
            targetSkinConditions = listOf("Large Pores", "Oily Skin"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),
        Product(
            id = "cl006",
            name = "Gentle Skin Cleanser",
            brand = "Cetaphil",
            category = "Cleanser",
            description = "Cleanser lembut yang cocok untuk kulit sensitif dan kering.",
            price = 95000.0,
            targetSkinConditions = listOf("Sensitive Skin", "Dry Skin"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),
        Product(
            id = "cl007",
            name = "Hydrating Facial Cleanser",
            brand = "CeraVe",
            category = "Cleanser",
            description = "Hydrating cleanser dengan ceramides untuk menjaga kelembaban kulit.",
            price = 150000.0,
            targetSkinConditions = listOf("Dry Skin", "Sensitive Skin"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),
        Product(
            id = "cl008",
            name = "Sensibio H2O Micellar Water",
            brand = "Bioderma",
            category = "Cleanser",
            description = "Micellar water untuk kulit sensitif, membersihkan dan menenangkan.",
            price = 175000.0,
            targetSkinConditions = listOf("Sensitive Skin", "All Skin Types"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),

        // TONER
        Product(
            id = "tn001",
            name = "Miraculous Retinol Toner",
            brand = "Avoskin",
            category = "Toner",
            description = "Toner dengan retinol untuk anti-aging dan mencerahkan kulit.",
            price = 125000.0,
            targetSkinConditions = listOf("Aging", "Dark Spots", "Dull Skin"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),
        Product(
            id = "tn002",
            name = "Barrier+ Calming Toner Essence",
            brand = "Whitelab",
            category = "Toner",
            description = "Toner essence yang menenangkan dan memperkuat skin barrier.",
            price = 85000.0,
            targetSkinConditions = listOf("Sensitive Skin", "Irritated Skin"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),
        Product(
            id = "tn003",
            name = "Essence Toner",
            brand = "Pyunkang Yul",
            category = "Toner",
            description = "Essence toner dengan formula sederhana untuk hidrasi maksimal.",
            price = 180000.0,
            targetSkinConditions = listOf("Dry Skin", "Dehydrated Skin"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),
        Product(
            id = "tn004",
            name = "Heartleaf 77% Soothing Toner",
            brand = "Anua",
            category = "Toner",
            description = "Toner dengan 77% heartleaf extract untuk menenangkan kulit sensitif.",
            price = 150000.0,
            targetSkinConditions = listOf("Sensitive Skin", "Redness", "Acne"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),
        Product(
            id = "tn005",
            name = "AHA BHA PHA 30 Days Miracle Toner",
            brand = "Some By Mi",
            category = "Toner",
            description = "Exfoliating toner dengan AHA BHA PHA untuk kulit lebih halus.",
            price = 145000.0,
            targetSkinConditions = listOf("Acne", "Texture", "Dark Spots"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),
        Product(
            id = "tn006",
            name = "Skin Perfecting 2% BHA Liquid Exfoliant",
            brand = "Paula's Choice",
            category = "Toner",
            description = "BHA liquid exfoliant untuk mengatasi blackheads dan pori-pori besar.",
            price = 380000.0,
            targetSkinConditions = listOf("Acne", "Large Pores", "Blackheads"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),
        Product(
            id = "tn007",
            name = "Glycolic Acid 7% Toning Solution",
            brand = "The Ordinary",
            category = "Toner",
            description = "Toning solution dengan 7% glycolic acid untuk eksfoliasi.",
            price = 165000.0,
            targetSkinConditions = listOf("Dull Skin", "Texture", "Dark Spots"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),

        // SERUM
        Product(
            id = "sr001",
            name = "Niacinamide + Moisture Beet Serum",
            brand = "Somethinc",
            category = "Serum",
            description = "Serum niacinamide dengan beetroot untuk brightening dan hidrasi.",
            price = 95000.0,
            targetSkinConditions = listOf("Dull Skin", "Dark Spots", "Dehydrated Skin"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),
        Product(
            id = "sr002",
            name = "The Great Multi Tasker Serum",
            brand = "Avoskin",
            category = "Serum",
            description = "Multi-tasker serum dengan peptide dan retinol untuk anti-aging.",
            price = 135000.0,
            targetSkinConditions = listOf("Aging", "Fine Lines", "Wrinkles"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),
        Product(
            id = "sr003",
            name = "Hydrasoothe Sunscreen Serum",
            brand = "Azarine",
            category = "Serum",
            description = "Serum dengan SPF untuk perlindungan dan hidrasi sekaligus.",
            price = 65000.0,
            targetSkinConditions = listOf("All Skin Types", "Sun Protection"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),
        Product(
            id = "sr004",
            name = "Glow Serum Propolis + Niacinamide",
            brand = "Beauty of Joseon",
            category = "Serum",
            description = "Serum dengan propolis dan niacinamide untuk glowing skin.",
            price = 175000.0,
            targetSkinConditions = listOf("Dull Skin", "Acne", "Hyperpigmentation"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),
        Product(
            id = "sr005",
            name = "Madagascar Centella Ampoule",
            brand = "Skin1004",
            category = "Serum",
            description = "Ampoule dengan centella asiatica untuk menenangkan dan repair.",
            price = 195000.0,
            targetSkinConditions = listOf("Sensitive Skin", "Redness", "Irritation"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),
        Product(
            id = "sr006",
            name = "Green Tea Seed Serum",
            brand = "Innisfree",
            category = "Serum",
            description = "Serum dengan green tea untuk antioksidan dan hidrasi.",
            price = 250000.0,
            targetSkinConditions = listOf("Dry Skin", "Dull Skin"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),
        Product(
            id = "sr007",
            name = "Niacinamide 10% + Zinc 1%",
            brand = "The Ordinary",
            category = "Serum",
            description = "High-strength niacinamide untuk mengatasi pori dan blemishes.",
            price = 120000.0,
            targetSkinConditions = listOf("Acne", "Large Pores", "Oily Skin"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),
        Product(
            id = "sr008",
            name = "Effaclar Serum",
            brand = "La Roche-Posay",
            category = "Serum",
            description = "Serum untuk kulit berjerawat dengan niacinamide dan salicylic acid.",
            price = 380000.0,
            targetSkinConditions = listOf("Acne", "Oily Skin", "Large Pores"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),
        Product(
            id = "sr009",
            name = "Powerful-Strength Vitamin C Serum",
            brand = "Kiehl's",
            category = "Serum",
            description = "Vitamin C serum untuk brightening dan anti-aging.",
            price = 950000.0,
            targetSkinConditions = listOf("Dull Skin", "Dark Spots", "Aging"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),

        // MOISTURIZER
        Product(
            id = "ms001",
            name = "5X Ceramide Barrier Repair Moisture Gel",
            brand = "Skintific",
            category = "Moisturizer",
            description = "Moisture gel dengan 5x ceramide untuk repair skin barrier.",
            price = 110000.0,
            targetSkinConditions = listOf("Dry Skin", "Sensitive Skin", "Damaged Barrier"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),
        Product(
            id = "ms002",
            name = "Ceramide Skin Barrier Moisture Gel",
            brand = "Somethinc",
            category = "Moisturizer",
            description = "Gel moisturizer dengan ceramide untuk kulit lembab dan sehat.",
            price = 95000.0,
            targetSkinConditions = listOf("Dry Skin", "Dehydrated Skin"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),
        Product(
            id = "ms003",
            name = "Cera-Moist Hydrating Gel",
            brand = "Glad2Glow",
            category = "Moisturizer",
            description = "Hydrating gel dengan ceramide untuk hidrasi optimal.",
            price = 75000.0,
            targetSkinConditions = listOf("Dry Skin", "All Skin Types"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),
        Product(
            id = "ms004",
            name = "Advanced Snail 92 All in One Cream",
            brand = "COSRX",
            category = "Moisturizer",
            description = "All in one cream dengan 92% snail mucin untuk repair dan hidrasi.",
            price = 230000.0,
            targetSkinConditions = listOf("Dry Skin", "Damaged Skin", "Aging"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),
        Product(
            id = "ms005",
            name = "Ceramide Ato Concentrate Cream",
            brand = "Illiyoon",
            category = "Moisturizer",
            description = "Ceramide cream untuk kulit sangat kering dan sensitif.",
            price = 180000.0,
            targetSkinConditions = listOf("Dry Skin", "Eczema", "Sensitive Skin"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),
        Product(
            id = "ms006",
            name = "Moisturizing Cream",
            brand = "CeraVe",
            category = "Moisturizer",
            description = "Moisturizing cream dengan ceramides dan hyaluronic acid.",
            price = 200000.0,
            targetSkinConditions = listOf("Dry Skin", "Normal Skin"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),
        Product(
            id = "ms007",
            name = "Hydro Boost Water Gel",
            brand = "Neutrogena",
            category = "Moisturizer",
            description = "Water gel dengan hyaluronic acid untuk hidrasi intens.",
            price = 170000.0,
            targetSkinConditions = listOf("Dehydrated Skin", "Oily Skin"),
            imageResId = android.R.drawable.ic_menu_gallery
        ),
        Product(
            id = "ms008",
            name = "Moisture Surge 100H",
            brand = "Clinique",
            category = "Moisturizer",
            description = "Auto-replenishing hydrator untuk hidrasi 100 jam.",
            price = 595000.0,
            targetSkinConditions = listOf("Dry Skin", "Dehydrated Skin", "All Skin Types"),
            imageResId = android.R.drawable.ic_menu_gallery
        )
    )
}