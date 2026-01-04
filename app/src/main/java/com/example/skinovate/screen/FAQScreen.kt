package com.example.skinovate.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAQScreen(navController: NavController) {
    val faqItems = remember {
        listOf(
            FAQItem(
                question = "Bagaimana cara menggunakan Face Analysis?",
                answer = "Buka menu Face Analysis dari bottom navigation (tombol tengah yang kuning), izinkan akses kamera ketika diminta, posisikan wajahmu di dalam frame, lalu tap tombol capture. Aplikasi akan menganalisis kulitmu dan menampilkan hasil dalam beberapa detik."
            ),
            FAQItem(
                question = "Bagaimana cara membuat rutinitas skincare?",
                answer = "Pergi ke Routine Maker dari menu, pilih Morning atau Evening Routine, lalu tap tombol \"Add new activity\" untuk menambahkan langkah-langkah skincare. Kamu bisa mengatur waktu untuk setiap langkah dan menghapus langkah yang tidak diinginkan."
            ),
            FAQItem(
                question = "Apakah data saya aman?",
                answer = "Ya, semua data Anda disimpan secara lokal di perangkat Anda dan tidak dibagikan dengan pihak ketiga tanpa izin Anda. Anda dapat mengatur privasi data di menu Profile > Privacy Settings."
            ),
            FAQItem(
                question = "Bagaimana cara mengubah pengaturan notifikasi?",
                answer = "Buka menu Profile, pilih Notifikasi, lalu aktifkan atau nonaktifkan notifikasi sesuai preferensi Anda. Anda dapat mengatur pengingat rutinitas, rekomendasi produk, pengingat scan, dan laporan mingguan."
            ),
            FAQItem(
                question = "Bagaimana cara mencari produk?",
                answer = "Buka menu Products, gunakan search bar untuk mencari produk berdasarkan nama atau brand. Anda juga bisa menggunakan filter kategori untuk menyaring produk berdasarkan jenisnya (Cleanser, Moisturizer, dll)."
            ),
            FAQItem(
                question = "Apakah aplikasi ini gratis?",
                answer = "Ya, aplikasi Skinovate saat ini gratis untuk digunakan. Semua fitur utama dapat diakses tanpa biaya."
            ),
            FAQItem(
                question = "Bagaimana cara mengatur waktu rutinitas?",
                answer = "Di Routine Maker, kamu bisa mengatur waktu untuk setiap langkah skincare dengan menambahkan waktu spesifik. Waktu utama rutinitas (Morning/Evening) dapat diatur saat membuat rutinitas."
            ),
            FAQItem(
                question = "Dapatkah saya menghapus semua data saya?",
                answer = "Ya, kamu dapat menghapus semua data dengan pergi ke Profile > Privacy Settings > Kelola Data, lalu tap \"Hapus Semua Data\". Tindakan ini tidak dapat dibatalkan."
            )
        )
    }
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "FAQ Lengkap",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Pertanyaan Umum",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Temukan jawaban untuk pertanyaan yang sering diajukan",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            faqItems.forEach { faqItem ->
                ExpandableFAQItem(
                    question = faqItem.question,
                    answer = faqItem.answer
                )
            }
        }
    }
}

data class FAQItem(
    val question: String,
    val answer: String
)

@Composable
fun ExpandableFAQItem(
    question: String,
    answer: String
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }
            
            if (expanded) {
                Divider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                )
                Text(
                    text = answer,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

