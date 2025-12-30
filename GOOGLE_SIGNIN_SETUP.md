# Setup Google Sign-In untuk Skinovate

## Langkah-langkah Setup

### 1. Buat Project di Google Cloud Console

1. Buka [Google Cloud Console](https://console.cloud.google.com/)
2. Buat project baru atau pilih project yang sudah ada
3. Aktifkan **Google Sign-In API**:
   - Pergi ke "APIs & Services" > "Library"
   - Cari "Google Sign-In API"
   - Klik "Enable"

### 2. Buat OAuth 2.0 Client ID

1. Pergi ke "APIs & Services" > "Credentials"
2. Klik "Create Credentials" > "OAuth client ID"
3. Pilih "Android" sebagai application type
4. Isi informasi berikut:
   - **Name**: Skinovate Android
   - **Package name**: `com.example.skinovate` (sesuaikan dengan applicationId di build.gradle.kts)
   - **SHA-1 certificate fingerprint**: (lihat langkah 3)

### 3. Dapatkan SHA-1 Fingerprint

#### Untuk Debug Build:
```bash
# Windows (PowerShell)
cd android
.\gradlew signingReport

# Atau menggunakan keytool langsung
keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
```

#### Untuk Release Build:
```bash
keytool -list -v -keystore [path-to-your-keystore] -alias [your-key-alias]
```

**Catatan**: Copy SHA-1 fingerprint (tanpa `SHA1:`) dan paste ke Google Cloud Console

### 4. Download google-services.json (Opsional)

Jika ingin menggunakan Firebase (untuk fitur lanjutan), Anda bisa:
1. Buka [Firebase Console](https://console.firebase.google.com/)
2. Tambahkan Android app ke project
3. Download `google-services.json`
4. Place di `app/` directory

### 5. Update AndroidManifest.xml

Pastikan AndroidManifest.xml sudah memiliki permission internet (sudah ada di project ini).

### 6. Test Aplikasi

1. Build dan run aplikasi
2. Klik "Masuk dengan Google"
3. Pilih akun Google Anda
4. Aplikasi akan otomatis masuk ke home screen

## Troubleshooting

### Error: "Sign in failed"
- Pastikan SHA-1 fingerprint sudah benar
- Pastikan package name sesuai dengan yang di Google Cloud Console
- Tunggu beberapa menit setelah menambahkan SHA-1 (Google perlu waktu untuk propagate)

### Error: "10:"
- Ini berarti OAuth client ID tidak ditemukan
- Pastikan package name dan SHA-1 sudah benar di Google Cloud Console

### Error: "12500:"
- Google Sign-In API belum diaktifkan
- Aktifkan di Google Cloud Console

## Catatan Penting

- **Debug keystore**: Default keystore untuk development ada di `~/.android/debug.keystore`
- **Release keystore**: Anda perlu membuat keystore sendiri untuk production
- **Package name**: Harus sama persis dengan `applicationId` di `build.gradle.kts`
- **SHA-1**: Setiap build type (debug/release) memiliki SHA-1 berbeda

## Workflow Authentication

1. **App Launch** → Check `AuthRepository.isLoggedIn`
2. **Not Logged In** → Show `AuthScreen`
3. **User clicks "Masuk dengan Google"** → Launch Google Sign-In intent
4. **User selects account** → `onActivityResult` receives result
5. **AuthViewModel processes result** → Save user data to `AuthRepository`
6. **State changes** → `MainActivity` recomposes and shows `SkinovateApp`
7. **User clicks logout** → Clear auth state → Show `AuthScreen` again

## Fitur yang Sudah Diimplementasikan

✅ Google Sign-In integration
✅ Auth state persistence (SharedPreferences)
✅ Auto-login jika sudah pernah login
✅ Logout functionality
✅ User profile display di Home & Settings
✅ Protected routes (harus login untuk akses app)

## Fitur yang Bisa Ditambahkan

- [ ] Email/Password authentication
- [ ] Biometric authentication (fingerprint/face unlock)
- [ ] Remember me option
- [ ] Account deletion
- [ ] Profile photo upload
- [ ] Sync data dengan cloud (Firebase)

