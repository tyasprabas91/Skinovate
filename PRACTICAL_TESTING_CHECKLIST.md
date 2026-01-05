# ✅ Practical Testing Checklist - Skinovate MVP

**Tujuan:** Testing cepat dan efisien untuk memastikan core features berfungsi sebelum release.

**Estimasi Waktu:** 1-2 jam untuk complete testing

---

## 🎯 Quick Test (30 menit) - Critical Paths Only

### ✅ Pre-Testing
- [ ] Clean install app (uninstall previous version)
- [ ] Test di device/emulator Android 7.0+
- [ ] Enable USB debugging untuk logging

---

## 🔐 1. Authentication (5 menit)

### Google Sign-In
- [ ] Tap "Masuk dengan Google" → Pilih akun → Berhasil masuk ke Home
- [ ] Logout dari Profile → Kembali ke Login screen

### Manual Login
- [ ] Input email & password → Login berhasil
- [ ] Input password salah → Error message muncul
- [ ] Field validation: email format & password min length

**✅ Pass jika:** Bisa login dan logout tanpa crash

---

## 🏠 2. Home Screen (5 menit)

- [ ] Screen load tanpa crash
- [ ] Personalized greeting muncul (Hello, [name])
- [ ] Quick actions accessible:
  - [ ] Tap "Analisis Wajah" → Navigate ke Face Analysis
  - [ ] Tap "Buat Routine" → Navigate ke Routine Maker
- [ ] Navigate ke Products, Features, Profile dari bottom bar

**✅ Pass jika:** Semua navigation bekerja, screen tidak blank

---

## 📸 3. Face Analysis - CRITICAL (15 menit)

### Camera & Capture
- [ ] Camera permission request muncul → Grant permission
- [ ] Camera preview muncul
- [ ] Tap capture button → Photo taken
- [ ] Tap "Analyze" → Analyzing screen muncul

### Analysis Results
- [ ] Analysis completes (1-5 detik)
- [ ] Results screen muncul dengan:
  - [ ] Overall score (0-100)
  - [ ] Skin type (Oily, Dry, Combination, dll)
  - [ ] Acne percentage
  - [ ] Dryness percentage
  - [ ] Recommendations list
- [ ] Tap "Save" → Results tersimpan
- [ ] Kembali ke Home → Last scan summary muncul

### Edge Cases
- [ ] Deny camera permission → Error message muncul
- [ ] Capture di kondisi low light → Handle gracefully (tidak crash)

**✅ Pass jika:** Analysis flow lengkap bekerja, results ditampilkan dengan benar

---

## 🛍️ 4. Product Screen (5 menit)

- [ ] Product list load tanpa crash
- [ ] Scroll list smooth
- [ ] Tap search bar → Input keyword → Results filtered
- [ ] Tap filter chip → Products filtered by category
- [ ] Tap product item → Bottom sheet detail muncul
- [ ] Close bottom sheet → Kembali ke list

**✅ Pass jika:** Search, filter, dan detail product bekerja

---

## 📋 5. Routine Maker (10 menit)

### Morning Routine
- [ ] Morning routine list muncul
- [ ] Tap "Add Step" → Bottom sheet muncul
- [ ] Select step type & product → Save
- [ ] Step muncul di list
- [ ] Tap step → Edit/Delete berfungsi

### Evening Routine
- [ ] Switch ke Evening tab
- [ ] Repeat test seperti Morning routine
- [ ] Save routine

### Data Persistence
- [ ] Close app completely
- [ ] Reopen app → Routines masih tersimpan

**✅ Pass jika:** Bisa create/edit/delete routines, data persist setelah restart

---

## 👤 6. Profile & Settings (10 menit)

### Profile Screen
- [ ] User info tampil (name, email)
- [ ] All settings options accessible:
  - [ ] Informasi Pribadi
  - [ ] Notifikasi
  - [ ] Privasi
  - [ ] Bantuan & Dukungan
  - [ ] Tentang

### Notification Settings
- [ ] Toggle routine reminders ON/OFF
- [ ] Toggle product recommendations ON/OFF
- [ ] Settings tersimpan setelah close app

### Privacy Settings
- [ ] Tap "Unduh Data Saya" → File exported ke Downloads
- [ ] Tap "Hapus Semua Data" → Confirmation dialog muncul
- [ ] Confirm deletion → Data terhapus, kembali ke Login

### Help & Support
- [ ] Tap "Email Support" → Email client opens
- [ ] Tap "FAQ Lengkap" → FAQ screen muncul
- [ ] FAQ items bisa di-expand/collapse

**✅ Pass jika:** Semua settings screens accessible, data export/deletion bekerja

---

## 🧭 7. Navigation & Back Button (5 menit)

- [ ] Bottom navigation: Home, Features, Products, Profile
- [ ] Back button di semua screens berfungsi
- [ ] Deep navigation: Home → Face Analysis → Back → Home
- [ ] Profile → Settings → Back → Profile

**✅ Pass jika:** Navigation smooth, back button consistent

---

## 💾 8. Data Persistence (5 menit)

- [ ] Create routine → Close app → Reopen → Routine masih ada
- [ ] Do face analysis → Save → Close app → Reopen → History masih ada
- [ ] Change settings → Close app → Reopen → Settings masih sama
- [ ] Login → Close app → Reopen → Masih logged in

**✅ Pass jika:** Semua data persist setelah app restart

---

## ⚠️ 9. Error Handling (5 menit)

- [ ] No camera permission → Error message user-friendly
- [ ] Invalid login credentials → Error message muncul
- [ ] No internet (jika using AI Vision API) → Fallback bekerja
- [ ] Empty data states → Empty state UI muncul (tidak blank screen)

**✅ Pass jika:** Error messages jelas, app tidak crash

---

## 🎨 10. UI/UX Quick Check (5 menit)

- [ ] Loading indicators muncul saat data loading
- [ ] Empty states muncul saat tidak ada data
- [ ] Colors & typography consistent
- [ ] Animations smooth (no lag)
- [ ] No blank screens

**✅ Pass jika:** UI polished, no obvious visual bugs

---

## 📱 11. Device Compatibility (Optional - 10 menit)

- [ ] Test di Android 7.0 (API 24) - minSdk
- [ ] Test di Android 13+ (API 33+) - untuk notification permissions
- [ ] Test di landscape orientation → Layout tidak broken

**✅ Pass jika:** App works di minimum SDK, layout responsive

---

## ✅ Final Verification

### Critical Path Test (15 menit)
Jalankan complete user journey:
1. **Login** → 2. **Home** → 3. **Face Analysis** (capture & analyze) → 4. **Save results** → 
5. **Home** (lihat last scan) → 6. **Products** (browse & search) → 7. **Routine Maker** (create routine) → 
8. **Profile** → 9. **Settings** → 10. **Logout**

**✅ MVP READY jika:**
- ✅ Semua critical paths bekerja
- ✅ Tidak ada crash dalam testing session
- ✅ Data persist setelah app restart
- ✅ Error handling bekerja
- ✅ UI polished & responsive

---

## 🐛 Bug Tracking

**Issues Found:**
1. _________________________________________________
2. _________________________________________________
3. _________________________________________________

**Priority:**
- 🔴 Critical (app crash, data loss)
- 🟡 High (feature tidak bekerja)
- 🟢 Low (UI polish, minor bugs)

---

## 📝 Testing Summary

**Tester:** ________________  
**Date:** ________________  
**Device:** ________________  
**Android Version:** ________________  
**Build Version:** ________________  

**Total Test Cases:** ___ / ___  
**Passed:** ___  
**Failed:** ___  
**Skipped:** ___  

**Overall Status:** ⬜ ✅ PASS  ⬜ ❌ FAIL  ⬜ ⚠️ NEEDS RETEST

**Notes:**
_________________________________________________
_________________________________________________
_________________________________________________

---

## 🚀 Next Steps

Jika semua tests PASS:
1. ✅ Fix any critical bugs found
2. ✅ Create release build (APK/AAB)
3. ✅ Test release build
4. ✅ Prepare for deployment!

---

**Last Updated:** After AI Vision API implementation  
**Version:** 1.0.0


