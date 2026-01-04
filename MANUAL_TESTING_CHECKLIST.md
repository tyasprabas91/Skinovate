# 🧪 Manual Testing Checklist - Skinovate App

Checklist untuk manual testing sebelum release MVP.

---

## ✅ Pre-Testing Setup

- [ ] Clean install aplikasi (uninstall previous version)
- [ ] Clear app data & cache
- [ ] Test di device/emulator dengan Android 7.0+ (API 24+)
- [ ] Enable developer options
- [ ] Enable USB debugging (untuk logging)

---

## 🔐 1. Authentication Flow

### 1.1 Google Sign-In
- [ ] Klik "Masuk dengan Google"
- [ ] Pilih akun Google
- [ ] Berhasil masuk dan redirect ke Home
- [ ] Logout berfungsi dengan benar
- [ ] Setelah logout, tidak bisa akses protected screens

### 1.2 Manual Login
- [ ] Input email & password valid
- [ ] Login berhasil
- [ ] Input email/password salah → error message muncul
- [ ] Field validation (email format, password min length)
- [ ] Register new account (jika available)

---

## 🏠 2. Home Screen

- [ ] Screen load tanpa crash
- [ ] Personalized greeting muncul (Hello, [name])
- [ ] Last scan summary muncul (jika ada)
- [ ] Quick actions accessible
- [ ] Navigation ke semua screens dari Home berfungsi
- [ ] Pull to refresh (jika ada)
- [ ] Empty state muncul jika belum ada data

---

## 📸 3. Face Analysis Screen

### 3.1 Camera & Capture
- [ ] Camera permission request muncul
- [ ] Camera preview muncul setelah permission granted
- [ ] Capture foto berfungsi
- [ ] Retake foto berfungsi
- [ ] Analyze button berfungsi setelah capture

### 3.2 Analysis Results
- [ ] Analysis berhasil (loading state muncul)
- [ ] Results screen muncul dengan:
  - Score
  - Skin type
  - Acne percentage
  - Dry percentage
  - Recommendations
- [ ] Save results berfungsi
- [ ] Navigate back berfungsi

### 3.3 Edge Cases
- [ ] No camera permission → error message
- [ ] Camera tidak tersedia → error message
- [ ] Low light condition → handle gracefully

---

## 🛍️ 4. Product Screen

### 4.1 Product List
- [ ] Product list load
- [ ] Scroll list smooth
- [ ] Product images load
- [ ] Product details tampil (name, brand, category, price)

### 4.2 Search & Filter
- [ ] Search by name berfungsi
- [ ] Filter by category berfungsi
- [ ] Clear search/filter berfungsi
- [ ] Empty search result → empty state muncul
- [ ] Case-insensitive search

### 4.3 Product Details
- [ ] Klik product → detail screen muncul
- [ ] Product info lengkap tampil
- [ ] "Add to Routine" button (jika ada)
- [ ] Navigate back berfungsi

---

## 📋 5. Routine Maker Screen

### 5.1 Morning Routine
- [ ] Morning routine list muncul
- [ ] Add step berfungsi
- [ ] Remove step berfungsi
- [ ] Edit step time berfungsi
- [ ] Reorder steps (jika ada)
- [ ] Save routine berfungsi
- [ ] Routine tersimpan setelah close app

### 5.2 Evening Routine
- [ ] Evening routine list muncul
- [ ] Add step berfungsi
- [ ] Remove step berfungsi
- [ ] Edit step time berfungsi
- [ ] Save routine berfungsi
- [ ] Routine tersimpan setelah close app

### 5.3 Edge Cases
- [ ] Empty routine → empty state muncul
- [ ] Maximum steps limit (jika ada)
- [ ] Invalid time format → validation error

---

## 👤 6. Profile & Settings Screens

### 6.1 Profile Screen
- [ ] User info tampil (name, email)
- [ ] Profile picture (jika ada)
- [ ] Edit profile berfungsi
- [ ] Logout button berfungsi

### 6.2 Settings Screen
- [ ] All settings options accessible
- [ ] Navigate ke sub-settings berfungsi

### 6.3 Notification Settings
- [ ] Toggle routine reminders berfungsi
- [ ] Toggle product recommendations berfungsi
- [ ] Toggle scan reminders berfungsi
- [ ] Toggle weekly reports berfungsi
- [ ] Settings tersimpan setelah close app
- [ ] Notifications muncul sesuai settings

### 6.4 Privacy Settings
- [ ] Data export berfungsi
- [ ] File exported ke Downloads folder
- [ ] File format valid (JSON)
- [ ] Data deletion dengan confirmation dialog
- [ ] Data benar-benar terhapus setelah confirm
- [ ] App kembali ke login screen setelah data deletion

### 6.5 Help & Support
- [ ] Email support button → opens email client
- [ ] FAQ screen accessible
- [ ] FAQ items expandable
- [ ] All FAQ items readable

---

## 🔔 7. Notifications

### 7.1 Routine Reminders
- [ ] Morning routine notification muncul di waktu yang ditentukan
- [ ] Evening routine notification muncul di waktu yang ditentukan
- [ ] Notification title & message correct
- [ ] Tap notification → opens app
- [ ] Notification channel settings correct

### 7.2 Notification Settings
- [ ] Disable routine reminders → notifications stop
- [ ] Enable routine reminders → notifications resume
- [ ] Change routine time → notifications reschedule

### 7.3 Edge Cases
- [ ] App closed → notifications still work
- [ ] Device reboot → notifications still work (WorkManager)
- [ ] Multiple notifications → all handled correctly

---

## 💾 8. Data Persistence

### 8.1 App Restart Test
- [ ] Close app completely
- [ ] Reopen app
- [ ] Login state persisted (tidak logout)
- [ ] Last scan data masih ada
- [ ] Routines masih tersimpan
- [ ] Settings masih tersimpan

### 8.2 Data Consistency
- [ ] Add product to routine → save → close app → reopen → product masih ada
- [ ] Change settings → save → close app → reopen → settings masih sama
- [ ] Multiple scans → semua history tersimpan
- [ ] Delete scan → benar-benar terhapus

---

## 🧭 9. Navigation

### 9.1 Bottom Navigation
- [ ] All tabs accessible
- [ ] Tab icons & labels correct
- [ ] Selected state visual correct
- [ ] Navigate between tabs smooth
- [ ] Deep links (jika ada) berfungsi

### 9.2 Back Navigation
- [ ] Back button berfungsi di semua screens
- [ ] Back stack correct (tidak skip screens)
- [ ] Exit app dengan back button (di Home)

---

## ⚠️ 10. Error Handling

### 10.1 Network Errors (jika applicable)
- [ ] No internet → error message user-friendly
- [ ] Timeout → error message
- [ ] Server error → error message

### 10.2 Database Errors
- [ ] Database corrupt → handle gracefully
- [ ] Migration error → handle gracefully

### 10.3 Permission Errors
- [ ] Camera permission denied → error message
- [ ] Notification permission (Android 13+) → handle correctly

### 10.4 Input Validation
- [ ] Invalid email format → error message
- [ ] Empty required fields → error message
- [ ] Invalid time format → error message

---

## 🎨 11. UI/UX

### 11.1 Visual Consistency
- [ ] Material 3 design system consistent
- [ ] Colors sesuai theme
- [ ] Typography consistent
- [ ] Spacing consistent
- [ ] Icons consistent

### 11.2 Loading States
- [ ] Loading indicators muncul saat data loading
- [ ] Skeleton loaders (jika ada) smooth
- [ ] No blank screens saat loading

### 11.3 Empty States
- [ ] Empty states muncul saat tidak ada data
- [ ] Empty state messages helpful
- [ ] Action buttons di empty states berfungsi

### 11.4 Error States
- [ ] Error messages user-friendly (tidak technical)
- [ ] Retry buttons (jika ada) berfungsi
- [ ] Error screens tidak blank

---

## 📱 12. Device Compatibility

### 12.1 Different Screen Sizes
- [ ] Test di phone (small screen)
- [ ] Test di tablet (large screen)
- [ ] Test di landscape orientation
- [ ] Test di portrait orientation
- [ ] Layout tidak broken di berbagai sizes

### 12.2 Android Versions
- [ ] Test di Android 7.0 (API 24) - minSdk
- [ ] Test di Android 8.0 (API 26)
- [ ] Test di Android 10 (API 29)
- [ ] Test di Android 12 (API 31)
- [ ] Test di Android 13+ (API 33+) - notification permissions

---

## ⚡ 13. Performance

### 13.1 App Startup
- [ ] Cold start < 3 seconds
- [ ] Warm start < 1 second
- [ ] No ANR (Application Not Responding)

### 13.2 Memory
- [ ] No memory leaks (check dengan LeakCanary jika ada)
- [ ] Memory usage reasonable
- [ ] No crashes karena out of memory

### 13.3 Smoothness
- [ ] Scroll smooth (60 FPS)
- [ ] Animations smooth
- [ ] Screen transitions smooth
- [ ] No lag saat loading data

---

## 🔒 14. Security & Privacy

- [ ] No sensitive data di logs
- [ ] Passwords tidak di-hardcode
- [ ] API keys (jika ada) tidak exposed
- [ ] Data export hanya export user's own data
- [ ] Data deletion benar-benar menghapus data

---

## ✅ Final Checks

- [ ] All critical bugs fixed
- [ ] No crashes dalam testing session
- [ ] App feels stable & ready
- [ ] All core features working
- [ ] User bisa complete main user journeys tanpa issues

---

## 📝 Testing Notes

**Tester:** ________________  
**Date:** ________________  
**Device:** ________________  
**Android Version:** ________________  

**Issues Found:**
1. 
2. 
3. 

**Status:** ⬜ Pass  ⬜ Fail  ⬜ Needs Retest

---

**Last Updated:** After production configuration setup  
**Version:** 1.0.0

