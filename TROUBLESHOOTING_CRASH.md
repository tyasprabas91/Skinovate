# Troubleshooting: Aplikasi Crash "Skinovate keeps stopping"

## Langkah-langkah untuk Mengatasi Crash

### 1. **Uninstall Aplikasi Lama**
```bash
adb uninstall com.example.skinovate
```

Atau manual:
- Settings → Apps → Skinovate → Uninstall

### 2. **Clean Build**
```bash
cd E:\Project\Skinovate
./gradlew clean
./gradlew assembleDebug
```

### 3. **Install Ulang**
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

Atau install langsung dari Android Studio.

### 4. **Cek Logcat untuk Error**
```bash
adb logcat | grep -i "skinovate\|error\|exception\|crash"
```

Atau di Android Studio:
- View → Tool Windows → Logcat
- Filter: "Skinovate" atau "Error"

### 5. **Kemungkinan Penyebab Crash**

#### A. Database Not Initialized
**Gejala**: Crash saat membuka Routine Recommendation atau Routine Maker
**Solusi**: Sudah diperbaiki dengan error handling

#### B. Context Null
**Gejala**: NullPointerException
**Solusi**: Sudah diperbaiki dengan try-catch

#### C. Navigation Route Error
**Gejala**: Crash saat navigate ke screen tertentu
**Solusi**: Pastikan semua route sudah terdaftar di NavHost

#### D. Missing Import
**Gejala**: Unresolved reference
**Solusi**: Sudah diperbaiki semua import

### 6. **Jika Masih Crash**

1. **Cek Logcat Error**:
   - Buka Android Studio
   - View → Tool Windows → Logcat
   - Filter: "FATAL" atau "AndroidRuntime"
   - Copy error message lengkap

2. **Cek Build Output**:
   - Build → Rebuild Project
   - Lihat apakah ada error di build

3. **Cek Device/Emulator**:
   - Pastikan Android version >= API 24
   - Pastikan ada storage space
   - Restart device/emulator

### 7. **Quick Fix**

Jika masih crash, coba:
1. Uninstall aplikasi
2. Clean project (Build → Clean Project)
3. Rebuild project (Build → Rebuild Project)
4. Install ulang

### 8. **Error yang Sudah Diperbaiki**

✅ Database initialization error handling
✅ Context null check
✅ Navigation route registration
✅ Import statements
✅ Error handling di RoutineRecommendationScreen
✅ Error handling di applyRecommendation

### 9. **Jika Masih Ada Error**

Kirimkan:
1. Logcat error message lengkap
2. Screenshot error dialog
3. Langkah-langkah untuk reproduce error

