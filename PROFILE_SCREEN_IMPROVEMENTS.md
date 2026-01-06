# Rekomendasi Perbaikan Fitur Profil & Settings

## 📊 Analisis Fitur Profil Saat Ini

### ✅ Yang Sudah Ada:
1. **ProfileScreen** - Tampilan utama profil dengan:
   - Card informasi pribadi (nama, email) dengan icon placeholder
   - Menu navigasi ke: Informasi Pribadi, Notifikasi, Privasi, Bantuan
   - Logout button
   
2. **PersonalInformationScreen** - Edit informasi pribadi:
   - Edit nama dan email
   - Edit password (untuk manual login)
   - Validasi form
   - Success/Error feedback
   
3. **NotificationSettingsScreen** - Pengaturan notifikasi:
   - Toggle untuk: Routine Reminders, Product Recommendations, Scan Reminders, Weekly Reports
   
4. **PrivacySettingsScreen** - Pengaturan privasi (perlu dicek lebih detail)

---

## 🔍 Masalah yang Ditemukan

### 1. **Profile Picture Tidak Ditampilkan** (PRIORITAS TINGGI)
   **Masalah:**
   - User model memiliki `photoUrl` (dari Google login)
   - ProfileScreen hanya menampilkan icon placeholder gradient
   - Tidak menggunakan foto profil dari Google account
   
   **Solusi:**
   - Tambahkan Coil library untuk load image dari URL
   - Tampilkan foto profil Google di ProfileScreen
   - Fallback ke icon placeholder jika photoUrl null

### 2. **Kurang Informasi Statistik/Progress User** (PRIORITAS TINGGI)
   **Masalah:**
   - Aplikasi memiliki scan history, routines, dll
   - ProfileScreen tidak menampilkan statistik/ringkasan progress user
   - User tidak bisa melihat overview progress skincare mereka
   
   **Rekomendasi:**
   - Tambahkan statistik card di ProfileScreen:
     - Total scans dilakukan
     - Total routines created
     - Last scan date & score
     - Skin type current
     - Progress tracking (jika ada goals)

### 3. **ProfileScreen Layout Kurang Informatif** (PRIORITAS MEDIUM)
   **Masalah:**
   - Layout terlalu sederhana
   - Tidak ada visual hierarchy yang jelas
   - Card profil terlalu besar, menghabiskan ruang
   
   **Rekomendasi:**
   - Perbaiki layout dengan section yang lebih jelas
   - Tambahkan quick stats section
   - Perbaiki spacing dan visual hierarchy

### 4. **Personal Information Screen - Tidak Ada Edit Photo** (PRIORITAS MEDIUM)
   **Masalah:**
   - User tidak bisa mengubah foto profil
   - Foto hanya dari Google (jika login dengan Google)
   - Manual users tidak punya foto profil
   
   **Rekomendasi:**
   - Tambahkan opsi untuk upload/ubah foto profil (future: butuh storage)
   - Atau minimal tampilkan foto dari Google dengan jelas
   - Untuk manual users, tetap gunakan placeholder yang lebih menarik

### 5. **Tidak Ada Quick Actions atau Shortcuts** (PRIORITAS LOW)
   **Rekomendasi:**
   - Tambahkan quick action buttons:
     - "Lihat Scan History" → navigate ke HistoryAnalysisScreen
     - "Edit Routine" → navigate ke RoutineMakerScreen
     - "New Scan" → navigate ke SkinQuestionnaireScreen

### 6. **About Screen Tidak Menampilkan Versi** (PRIORITAS LOW)
   **Rekomendasi:**
   - Tampilkan app version
   - Tampilkan build number
   - Link ke changelog (jika ada)

---

## 📋 Rekomendasi Perbaikan Prioritas Tinggi

### 1. **Tampilkan Foto Profil dari Google Account**

**File:** `SettingsScreen.kt` (ProfileScreen)

**Perubahan:**
```kotlin
// Tambahkan Coil dependency untuk load image
implementation("io.coil-kt:coil-compose:2.5.0")

// Di ProfileSection/ProfileOptionSection:
// Ganti Box dengan gradient menjadi AsyncImage
AsyncImage(
    model = user?.photoUrl,
    contentDescription = "Profile Picture",
    modifier = Modifier
        .size(64.dp)
        .clip(CircleShape),
    placeholder = painterResource(R.drawable.ic_profile_placeholder),
    error = painterResource(R.drawable.ic_profile_placeholder),
    contentScale = ContentScale.Crop
)
```

### 2. **Tambahkan Stats/Summary Section**

**File:** `SettingsScreen.kt` (ProfileScreen)

**Rekomendasi Layout:**
- Profile Header Card (foto, nama, email) - lebih compact
- Quick Stats Section:
  - Card dengan grid 2x2:
    - Total Scans
    - Current Skin Type
    - Active Routines
    - Last Scan Score
- Settings Menu (seperti sekarang)
- Logout Button

**Data yang perlu diambil:**
- Scan history count (dari UserRepository/ScanHistoryDao)
- Last scan result (dari UserRepository.lastScan)
- Routine count (dari RoutineRepository)
- Skin type dari last scan

---

## 📋 Rekomendasi Perbaikan Prioritas Medium

### 3. **Improve ProfileScreen Layout**

**Rekomendasi:**
```kotlin
Column {
    // 1. Compact Profile Header
    CompactProfileHeader(user)
    
    // 2. Quick Stats Grid
    ProfileStatsSection()
    
    Spacer(16.dp)
    
    // 3. Settings Menu
    ProfileOptionSection(navController)
    
    Spacer(weight = 1f)
    
    // 4. Logout
    LogoutButton()
}
```

### 4. **Enhance Personal Information Screen**

**Rekomendasi:**
- Tampilkan foto profil di header (jika ada)
- Perbaiki layout form dengan grouping yang lebih jelas
- Tambahkan section untuk "Account Information"
- Tambahkan divider antara sections

---

## 📋 Rekomendasi Perbaikan Prioritas Rendah

### 5. **Add Quick Actions Section**

**Rekomendasi:**
- Tambahkan horizontal scrollable chips/buttons untuk quick actions
- Actions: "View History", "New Scan", "Edit Routine", "View Products"

### 6. **About Screen Enhancement**

**Rekomendasi:**
- Display app version & build number
- Add app description
- Add developer credits
- Add links (privacy policy, terms of service - jika ada)

### 7. **Privacy Settings Enhancement**

**Rekomendasi:**
- Add data export functionality (sudah ada DataExportHelper?)
- Add data deletion option
- Add account deletion option
- Add privacy policy link

---

## 🎨 Design Suggestions

### ProfileScreen Layout:
```
┌─────────────────────────────┐
│   Profile Header (Compact)  │
│   [Photo] Name & Email      │
└─────────────────────────────┘
┌─────────────────────────────┐
│   Quick Stats (2x2 Grid)    │
│   [Scans] [Skin Type]       │
│   [Routines] [Last Score]   │
└─────────────────────────────┘
┌─────────────────────────────┐
│   Settings Menu Items       │
│   - Informasi Pribadi       │
│   - Notifikasi              │
│   - Privasi                 │
│   - Bantuan                 │
└─────────────────────────────┘
┌─────────────────────────────┐
│   [Logout Button]           │
└─────────────────────────────┘
```

### Compact Profile Header:
- Foto profil (64dp) di kiri
- Nama (title) di kanan atas
- Email (subtitle) di kanan bawah
- Arrow icon di paling kanan (clickable ke PersonalInformationScreen)
- Total height: ~80dp (lebih compact dari sekarang)

---

## 📝 Action Items

### Immediate (PRIORITAS TINGGI):
1. **Add Coil dependency** untuk load image dari URL
2. **Tampilkan foto profil Google** di ProfileScreen
3. **Tambahkan Stats Section** dengan data dari repositories
4. **Perbaiki ProfileScreen layout** menjadi lebih informatif

### Short-term (PRIORITAS MEDIUM):
1. **Improve PersonalInformationScreen** layout
2. **Compact Profile Header** design
3. **Enhance visual hierarchy** di semua profile screens

### Long-term (PRIORITAS LOW):
1. **Add Quick Actions** section
2. **Enhance About Screen**
3. **Add Data Export/Delete** functionality di Privacy Settings
4. **Add Account Deletion** option

---

## 🔧 Technical Implementation Notes

### Untuk Load Image dari URL:
1. Tambahkan Coil dependency:
```kotlin
implementation("io.coil-kt:coil-compose:2.5.0")
```

2. Import di file:
```kotlin
import coil.compose.AsyncImage
```

3. Gunakan AsyncImage:
```kotlin
AsyncImage(
    model = user?.photoUrl,
    contentDescription = "Profile Picture",
    modifier = Modifier.size(64.dp).clip(CircleShape),
    placeholder = painterResource(R.drawable.ic_profile_placeholder),
    error = painterResource(R.drawable.ic_profile_placeholder),
    contentScale = ContentScale.Crop
)
```

### Untuk Stats Section:
1. Ambil data dari repositories:
   - Scan count: `ScanHistoryDao().getScanCount(userId)`
   - Last scan: `UserRepository.lastScan.value`
   - Routine count: `RoutineRepository.morningRoutine.steps.size + eveningRoutine.steps.size`

2. Buat composable `ProfileStatsSection()` dengan grid layout 2x2

---

## 💡 Additional Suggestions

1. **Add Profile Completion Indicator** - Progress bar untuk completion profile
2. **Add Achievement/Badges** - Jika user mencapai milestone (contoh: 10 scans, 30 days routine, dll)
3. **Add Theme/Appearance Settings** - Dark mode toggle (jika belum ada)
4. **Add Language Settings** - Language selection (jika aplikasi support multiple languages)
5. **Add Export Data Feature** - Export scan history, routines ke JSON/CSV
6. **Add Backup/Restore** - Backup data ke cloud (future feature)

