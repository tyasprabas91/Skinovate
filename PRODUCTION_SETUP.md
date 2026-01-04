# 📦 Production Configuration Guide

Guide untuk setup production build untuk aplikasi Skinovate.

---

## 🔐 Signing Configuration

### 1. Generate Keystore (jika belum ada)

Untuk production release, Anda perlu membuat keystore file untuk signing APK/AAB:

```bash
keytool -genkeypair -v -storetype PKCS12 -keystore skinovate-release.jks -alias skinovate-key -keyalg RSA -keysize 2048 -validity 10000
```

**Informasi yang akan diminta:**
- Password untuk keystore (simpan dengan aman!)
- Password untuk key alias (bisa sama dengan keystore)
- Nama, organisasi, dll.

**⚠️ PENTING:**
- Simpan keystore file dan password dengan AMAN
- JANGAN commit keystore ke version control
- Backup keystore file ke tempat yang aman
- Jika kehilangan keystore, tidak bisa update aplikasi di Play Store!

### 2. Setup keystore.properties

1. Copy template file:
   ```bash
   cp app/keystore.properties.example app/keystore.properties
   ```

2. Edit `app/keystore.properties` dan isi dengan informasi keystore Anda:
   ```properties
   storeFile=../keystore/skinovate-release.jks
   storePassword=your-actual-store-password
   keyAlias=skinovate-key
   keyPassword=your-actual-key-password
   ```

3. Pastikan `keystore.properties` ada di `.gitignore` (sudah ada)

### 3. Build Release APK

```bash
# Build release APK
./gradlew assembleRelease

# Output: app/build/outputs/apk/release/app-release.apk
```

### 4. Build Release AAB (untuk Play Store)

```bash
# Build release AAB (Android App Bundle)
./gradlew bundleRelease

# Output: app/build/outputs/bundle/release/app-release.aab
```

---

## 📱 Version Management

### Current Version
- **Version Code:** 1
- **Version Name:** 1.0.0

### Update Version

Untuk update version, edit `app/build.gradle.kts`:

```kotlin
defaultConfig {
    versionCode = 2  // Increment setiap release
    versionName = "1.0.1"  // Semantic versioning (MAJOR.MINOR.PATCH)
}
```

**Version Code Rules:**
- Harus increment setiap release
- Play Store menggunakan ini untuk menentukan versi terbaru
- Harus selalu naik (tidak bisa turun)

**Version Name Rules (Semantic Versioning):**
- **MAJOR** (1.0.0): Breaking changes
- **MINOR** (0.1.0): New features, backward compatible
- **PATCH** (0.0.1): Bug fixes

---

## 🔧 Build Configuration

### Current Setup

✅ **Debug Build:**
- Application ID: `com.example.skinovate.debug`
- Minify: Disabled
- Debuggable: Enabled
- Version: `1.0.0-debug`

✅ **Release Build:**
- Application ID: `com.example.skinovate`
- Minify: Enabled (R8/ProGuard)
- Shrink Resources: Enabled
- Debuggable: Disabled
- Code Optimization: Enabled

### ProGuard Rules

File `app/proguard-rules.pro` sudah dikonfigurasi untuk:
- ✅ Room Database
- ✅ Gson
- ✅ WorkManager
- ✅ Google Play Services (Sign-In)
- ✅ ML Kit Face Detection
- ✅ CameraX
- ✅ Compose & Navigation

---

## 📋 Pre-Release Checklist

Sebelum release ke production, pastikan:

### ✅ Code Quality
- [ ] Tidak ada TODO/FIXME di code production
- [ ] Tidak ada hardcoded credentials/API keys
- [ ] Error handling sudah lengkap
- [ ] Logging tidak expose sensitive data

### ✅ Testing
- [ ] Manual testing semua fitur utama
- [ ] Test di berbagai device sizes
- [ ] Test dengan berbagai Android versions (min SDK 24+)
- [ ] Test offline scenarios
- [ ] Test permission requests (camera, notifications)

### ✅ Performance
- [ ] APK size reasonable (< 50MB recommended)
- [ ] No memory leaks
- [ ] Smooth animations (60 FPS)
- [ ] Fast app startup time

### ✅ Security
- [ ] No sensitive data in logs
- [ ] Keystore file tidak di-commit
- [ ] ProGuard rules sudah benar
- [ ] Data encryption (jika diperlukan)

### ✅ Configuration
- [ ] Version code & name sudah benar
- [ ] Application ID sudah benar
- [ ] Keystore signing config sudah setup
- [ ] ProGuard rules sudah ditest

### ✅ Documentation
- [ ] README updated
- [ ] Changelog prepared
- [ ] Release notes prepared

---

## 🚀 Release Process

### 1. Prepare Release

```bash
# Update version
# Edit app/build.gradle.kts: versionCode & versionName

# Clean build
./gradlew clean

# Test build
./gradlew assembleRelease
```

### 2. Test Release Build

```bash
# Install release APK
adb install app/build/outputs/apk/release/app-release.apk

# Test aplikasi
# - Login/logout
# - All features
# - Performance
# - Crash testing
```

### 3. Generate AAB for Play Store

```bash
./gradlew bundleRelease
```

### 4. Upload to Play Store

1. Buka [Google Play Console](https://play.google.com/console)
2. Pilih aplikasi Skinovate
3. Create new release
4. Upload `app-release.aab`
5. Fill release notes
6. Submit for review

---

## 📊 Build Output Locations

- **Debug APK:** `app/build/outputs/apk/debug/app-debug.apk`
- **Release APK:** `app/build/outputs/apk/release/app-release.apk`
- **Release AAB:** `app/build/outputs/bundle/release/app-release.aab`

---

## 🔍 Troubleshooting

### Issue: "Keystore file not found"

**Solution:** Pastikan:
1. File `keystore.properties` ada di `app/` directory
2. Path `storeFile` di `keystore.properties` benar
3. Keystore file benar-benar ada di path tersebut

### Issue: "Signing config not found"

**Solution:** Build akan tetap berhasil, tapi APK tidak signed. Untuk release, pastikan:
1. `keystore.properties` sudah diisi dengan benar
2. Keystore file ada dan bisa diakses

### Issue: ProGuard errors

**Solution:** 
1. Check `proguard-rules.pro` untuk library yang error
2. Tambahkan `-keep` rules untuk classes yang diperlukan
3. Test build setelah menambah rules

### Issue: APK size terlalu besar

**Solution:**
- Enable R8/ProGuard (sudah enabled)
- Enable shrink resources (sudah enabled)
- Remove unused resources
- Use vector drawables instead of PNG
- Optimize images (compress)

---

## 📝 Notes

- **Development:** Gunakan debug build untuk development
- **Testing:** Test release build sebelum publish
- **Backup:** SELALU backup keystore file!
- **Version:** Increment version code setiap release
- **ProGuard:** Test aplikasi setelah enable ProGuard

---

**Last Updated:** Setelah setup production configuration  
**Maintained By:** Development Team


