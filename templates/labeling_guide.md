# Labeling Guide untuk Skin Analysis Dataset

## 📋 Panduan Labeling

Gunakan template CSV ini untuk label semua images dalam dataset.

---

## 🏷️ Label Definitions

### 1. **skin_type** (Required)
Klasifikasi tipe kulit:
- `oily` - Kulit berminyak, terlihat shiny
- `dry` - Kulit kering, terlihat kusam, mungkin terlihat flakes
- `combination` - Kombinasi oily (T-zone) dan dry (cheeks)
- `sensitive` - Kulit sensitif, mudah iritasi, kemerahan
- `normal` - Kulit seimbang, tidak terlalu oily atau dry

### 2. **acne_type** (Required)
Jenis jerawat yang terlihat:
- `none` - Tidak ada jerawat
- `blackheads` - Komedo hitam (open comedones)
- `whiteheads` - Komedo putih (closed comedones)
- `papules` - Jerawat kecil merah, tidak ada pus
- `pustules` - Jerawat dengan pus (white/yellow head)
- `cystic` - Jerawat besar, dalam, nyeri
- `nodular` - Jerawat besar, keras, dalam
- `mixed` - Kombinasi beberapa jenis

### 3. **acne_scars** (Required)
Apakah ada bekas jerawat:
- `0` - Tidak ada bekas jerawat
- `1` - Ada bekas jerawat

### 4. **acne_scar_severity** (Required if acne_scars = 1)
Tingkat keparahan bekas jerawat:
- `0` - Tidak ada (jika acne_scars = 0)
- `1` - Mild (sedikit bekas, tidak terlalu mencolok)
- `2` - Moderate (bekas terlihat jelas, beberapa area)
- `3` - Severe (banyak bekas, sangat mencolok)

### 5. **dark_spots** (Required)
Apakah ada dark spots/hyperpigmentation:
- `0` - Tidak ada dark spots
- `1` - Ada dark spots

### 6. **dark_spots_severity** (Required if dark_spots = 1)
Tingkat keparahan dark spots:
- `0` - Tidak ada (jika dark_spots = 0)
- `1` - Mild (sedikit bintik hitam, tidak terlalu mencolok)
- `2` - Moderate (beberapa bintik hitam terlihat jelas)
- `3` - Severe (banyak bintik hitam, sangat mencolok)

### 7. **wrinkles** (Required)
Apakah ada kerutan:
- `0` - Tidak ada kerutan
- `1` - Ada kerutan

### 8. **wrinkle_severity** (Required if wrinkles = 1)
Tingkat keparahan kerutan:
- `0` - Tidak ada (jika wrinkles = 0)
- `1` - Mild (fine lines, hanya terlihat dekat)
- `2` - Moderate (kerutan terlihat jelas, beberapa area)
- `3` - Severe (banyak kerutan dalam, sangat mencolok)

### 9. **pores** (Required)
Apakah pores terlihat:
- `0` - Pores tidak terlihat
- `1` - Pores terlihat

### 10. **pore_size** (Required if pores = 1)
Ukuran pores:
- `0` - Small (pores kecil, hampir tidak terlihat)
- `1` - Medium (pores terlihat, ukuran sedang)
- `2` - Large (pores besar, sangat terlihat)

### 11. **texture** (Required)
Klasifikasi texture kulit:
- `smooth` - Kulit halus, permukaan rata
- `rough` - Kulit kasar, permukaan tidak rata
- `bumpy` - Kulit bergelombang, ada benjolan
- `uneven` - Kulit tidak merata, kombinasi smooth dan rough

### 12. **texture_score** (Required)
Score kualitas texture (0-100):
- `0-30` - Poor texture (very rough, very bumpy)
- `31-60` - Fair texture (rough, some bumps)
- `61-80` - Good texture (mostly smooth, minor roughness)
- `81-100` - Excellent texture (very smooth, even)

### 13. **hydration** (Required)
Tingkat hidrasi kulit:
- `0` - Severely dehydrated (kulit sangat kering, terlihat kusam)
- `1` - Dehydrated (kulit kering, kurang kelembaban)
- `2` - Moderate (hidrasi cukup)
- `3` - Well-hydrated (kulit terlihat lembab, healthy)

### 14. **hydration_score** (Required)
Score hidrasi (0-100):
- `0-30` - Severely dehydrated
- `31-50` - Dehydrated
- `51-70` - Moderate hydration
- `71-90` - Well-hydrated
- `91-100` - Excellent hydration

### 15. **redness** (Required)
Apakah ada kemerahan/irritation:
- `0` - Tidak ada kemerahan
- `1` - Ada kemerahan

### 16. **redness_level** (Required if redness = 1)
Tingkat kemerahan:
- `0` - Tidak ada (jika redness = 0)
- `1` - Mild (sedikit kemerahan, tidak terlalu mencolok)
- `2` - Moderate (kemerahan jelas terlihat)
- `3` - Severe (sangat merah, iritasi parah)

### 17. **uneven_tone** (Required)
Tingkat keseragaman skin tone (1-5):
- `1` - Very uneven (banyak perbedaan warna, sangat tidak merata)
- `2` - Uneven (beberapa perbedaan warna)
- `3` - Moderate (sedikit perbedaan warna)
- `4` - Even (warna cukup merata)
- `5` - Very even (warna sangat merata, uniform)

### 18. **tone_score** (Required)
Score keseragaman skin tone (0-100):
- `0-30` - Very uneven
- `31-50` - Uneven
- `51-70` - Moderate
- `71-90` - Even
- `91-100` - Very even

### 19. **sun_damage** (Required)
Tingkat sun damage:
- `0` - None (tidak ada tanda sun damage)
- `1` - Mild (sedikit sunspots/freckles)
- `2` - Moderate (beberapa sunspots, sedikit perubahan warna)
- `3` - Severe (banyak sunspots, perubahan warna jelas)

### 20. **overall_score** (Required)
Overall skin health score (0-100):
- `0-40` - Poor (banyak masalah kulit)
- `41-60` - Fair (beberapa masalah kulit)
- `61-75` - Good (kulit dalam kondisi baik, minor issues)
- `76-90` - Very good (kulit sehat, sedikit masalah)
- `91-100` - Excellent (kulit sangat sehat, hampir tidak ada masalah)

### 21. **notes** (Optional)
Catatan tambahan tentang image:
- Lighting conditions
- Special features
- Any anomalies
- etc.

---

## 📝 Labeling Workflow

### Step 1: Preparation
1. Download dataset images
2. Open labeling_template.csv
3. Review first few images

### Step 2: Labeling Process
1. Open image di image viewer
2. Analyze semua features
3. Fill semua columns di CSV
4. Save progress regularly

### Step 3: Quality Check
1. Review random samples
2. Check consistency
3. Fix any errors
4. Get second opinion jika perlu (optional)

### Step 4: Validation
1. Split into train/val/test
2. Verify distributions
3. Final review

---

## ⚠️ Labeling Tips

1. **Be Consistent**
   - Gunakan criteria yang sama untuk semua images
   - Refer to this guide jika ragu

2. **Be Honest**
   - Jika tidak yakin, jangan guess
   - Better to skip atau mark as uncertain

3. **Focus on Visible Features**
   - Label berdasarkan yang terlihat di image
   - Jangan assume berdasarkan context

4. **Multiple Features**
   - Satu image bisa punya multiple features
   - Label semua yang terlihat

5. **Lighting Consideration**
   - Be aware bahwa lighting bisa affect visibility
   - Label berdasarkan yang visible dalam kondisi tersebut

---

## 📊 Example Labels

### Example 1: Healthy Oily Skin
```
skin_type: oily
acne_type: none
acne_scars: 0
dark_spots: 0
wrinkles: 0
pores: 1
pore_size: 1
texture: smooth
texture_score: 85
hydration: 3
hydration_score: 90
redness: 0
uneven_tone: 4
tone_score: 85
sun_damage: 0
overall_score: 88
```

### Example 2: Acne-Prone Skin
```
skin_type: combination
acne_type: mixed (blackheads, papules)
acne_scars: 1
acne_scar_severity: 1
dark_spots: 1
dark_spots_severity: 1
wrinkles: 0
pores: 1
pore_size: 2
texture: bumpy
texture_score: 60
hydration: 2
hydration_score: 75
redness: 1
redness_level: 1
uneven_tone: 3
tone_score: 70
sun_damage: 0
overall_score: 72
```

### Example 3: Aging Skin
```
skin_type: dry
acne_type: none
acne_scars: 0
dark_spots: 1
dark_spots_severity: 2
wrinkles: 1
wrinkle_severity: 2
pores: 0
texture: rough
texture_score: 65
hydration: 1
hydration_score: 60
redness: 0
uneven_tone: 2
tone_score: 65
sun_damage: 2
overall_score: 70
```

---

## ✅ Quality Checklist

Sebelum submit labeling, pastikan:
- [ ] Semua required fields filled
- [ ] Values sesuai dengan valid ranges
- [ ] Consistency checked (similar images have similar labels)
- [ ] No obvious errors
- [ ] At least 1000+ images labeled (minimum untuk training)
- [ ] Balanced distribution across classes
- [ ] Train/val/test split created

---

## 📚 Additional Resources

- Dermatology reference images
- Skin analysis guidelines
- Training materials untuk labelers

