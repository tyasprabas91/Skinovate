# TensorFlow Lite Dataset Guide untuk Skinovate

## 📋 Data yang Diperlukan untuk Skin Analysis App

### ✅ Fitur Utama (Sudah disebutkan):
1. **Skin Type** - Oily, Dry, Combination, Sensitive, Normal
2. **Acne Type** - Blackheads, Whiteheads, Cystic, Nodular, Papules, Pustules
3. **Acne Scars** - Yes/No, Severity (mild, moderate, severe), Types (ice pick, boxcar, rolling)

### ➕ Fitur Tambahan yang Direkomendasikan:

#### 1. **Dark Spots / Hyperpigmentation**
   - **Deteksi**: Bintik hitam, melasma, post-inflammatory hyperpigmentation
   - **Klasifikasi**: Present/Not Present, Severity (0-3)
   - **Penting untuk**: Rekomendasi brightening products, sun protection

#### 2. **Wrinkles / Fine Lines**
   - **Deteksi**: Fine lines, deep wrinkles, crow's feet, forehead lines
   - **Klasifikasi**: None, Mild, Moderate, Severe
   - **Penting untuk**: Anti-aging recommendations

#### 3. **Pore Size / Visibility**
   - **Deteksi**: Large pores, visible pores
   - **Klasifikasi**: Small, Medium, Large
   - **Penting untuk**: Pore-minimizing product recommendations

#### 4. **Skin Texture**
   - **Klasifikasi**: Smooth, Rough, Bumpy, Uneven
   - **Score**: 0-100 (texture quality)
   - **Penting untuk**: Exfoliation recommendations

#### 5. **Hydration Level**
   - **Klasifikasi**: Well-hydrated, Dehydrated, Severely Dehydrated
   - **Score**: 0-100
   - **Penting untuk**: Moisturizer recommendations

#### 6. **Redness / Inflammation**
   - **Deteksi**: Redness, irritation, rosacea-like conditions
   - **Klasifikasi**: None, Mild, Moderate, Severe
   - **Penting untuk**: Sensitive skin products, soothing ingredients

#### 7. **Uneven Skin Tone**
   - **Deteksi**: Color uniformity, discoloration
   - **Score**: 0-100 (evenness)
   - **Penting untuk**: Tone-correcting products

#### 8. **Sun Damage / Age Spots**
   - **Deteksi**: UV damage, sunspots, freckles
   - **Klasifikasi**: None, Mild, Moderate, Severe
   - **Penting untuk**: SPF recommendations, anti-aging

#### 9. **Blackheads / Whiteheads**
   - **Deteksi**: Comedones (open/closed)
   - **Klasifikasi**: Count, Severity
   - **Penting untuk**: Cleansing recommendations

#### 10. **Skin Firmness / Elasticity** (Advanced)
   - **Deteksi**: Sagging, loss of elasticity
   - **Klasifikasi**: Firm, Moderate, Loose
   - **Penting untuk**: Anti-aging, firming products

---

## 🗂️ Struktur Dataset Ideal

### Multi-Label Classification Format:

```csv
image_path,skin_type,acne_type,acne_scars,acne_scar_severity,dark_spots,dark_spots_severity,wrinkles,wrinkle_severity,pores,pore_size,texture,texture_score,hydration,hydration_score,redness,redness_level,uneven_tone,tone_score,sun_damage,overall_score
face_001.jpg,oily,blackheads,1,1,0,0,0,0,1,2,rough,65,2,70,0,0,3,75,0,82
face_002.jpg,dry,none,0,0,1,2,1,1,0,1,smooth,85,3,90,1,1,4,85,0,88
face_003.jpg,combination,cystic,1,2,1,1,0,0,1,2,bumpy,60,2,75,1,1,3,70,1,72
```

### Label Format:
- **skin_type**: oily, dry, combination, sensitive, normal
- **acne_type**: none, blackheads, whiteheads, papules, pustules, cystic, nodular
- **acne_scars**: 0 (no), 1 (yes)
- **acne_scar_severity**: 0 (none), 1 (mild), 2 (moderate), 3 (severe)
- **dark_spots**: 0 (no), 1 (yes)
- **dark_spots_severity**: 0-3
- **wrinkles**: 0 (no), 1 (yes)
- **wrinkle_severity**: 0-3
- **pores**: 0 (not visible), 1 (visible)
- **pore_size**: 0 (small), 1 (medium), 2 (large)
- **texture**: smooth, rough, bumpy, uneven
- **texture_score**: 0-100
- **hydration**: 0 (severely dehydrated), 1 (dehydrated), 2 (moderate), 3 (well-hydrated)
- **hydration_score**: 0-100
- **redness**: 0 (no), 1 (yes)
- **redness_level**: 0-3
- **uneven_tone**: 1-5 (1=very uneven, 5=very even)
- **tone_score**: 0-100
- **sun_damage**: 0 (none), 1 (mild), 2 (moderate), 3 (severe)
- **overall_score**: 0-100

---

## 📦 Dataset Gratis yang Cocok

### 🎯 Priority 1: Kaggle Datasets (Recommended)

#### 1. **Skin Type Classification Dataset**
- **Platform**: Kaggle
- **Search Terms**: "skin type classification", "facial skin type dataset"
- **URL**: https://www.kaggle.com/datasets
- **Features**: Skin types (oily, dry, combination, etc.)
- **Size**: Varies (typically 1,000-5,000 images)
- **Free**: ✅ Yes
- **Format**: Images + CSV labels
- **How to Access**:
  1. Create free Kaggle account
  2. Search "skin type classification"
  3. Download datasets that match your needs

#### 2. **Acne Detection Dataset**
- **Platform**: Kaggle
- **Search Terms**: "acne detection", "acne classification dataset"
- **URL**: https://www.kaggle.com/datasets
- **Features**: Acne types, severity
- **Free**: ✅ Yes
- **Note**: Combine multiple acne datasets for comprehensive coverage

#### 3. **HAM10000 - Skin Lesion Dataset**
- **Platform**: Kaggle
- **URL**: https://www.kaggle.com/datasets/kmader/skin-cancer-mnist-ham10000
- **Features**: 7 classes of skin lesions, high-quality images
- **Size**: 10,015 images
- **Free**: ✅ Yes
- **License**: CC0 (Public Domain)
- **Pros**: Large dataset, well-labeled, medical quality
- **Cons**: More medical-focused, but useful for texture/color analysis
- **How to Download**:
  ```bash
  # Using Kaggle API
  kaggle datasets download -d kmader/skin-cancer-mnist-ham10000
  ```

#### 4. **Face Quality Dataset** (Adaptable)
- **Platform**: Kaggle
- **Search**: "face quality dataset", "facial analysis dataset"
- **Features**: Can be adapted for skin analysis
- **Free**: ✅ Yes

### 🎯 Priority 2: Academic/Research Datasets

#### 5. **ISIC Archive**
- **URL**: https://www.isic-archive.com/
- **Features**: Dermatology images, various skin conditions
- **Size**: Very large collection (100,000+ images)
- **Free**: ✅ Yes (requires free registration)
- **License**: Research/Educational use
- **Pros**: High quality, medical-grade images
- **Cons**: More medical/research focused
- **How to Access**:
  1. Register free account at isic-archive.com
  2. Browse and download datasets
  3. Filter for facial/face images

#### 6. **DermNet NZ**
- **URL**: https://dermnetnz.org/image-library/
- **Features**: Educational skin condition images
- **Free**: ✅ Yes (for educational/non-commercial use)
- **License**: Check terms for commercial use
- **Pros**: Good quality, educational labels
- **Cons**: May need permission for commercial use
- **Note**: Better for reference/learning than direct training

### 🎯 Priority 3: General Face Datasets (Can be Adapted)

#### 7. **CelebA Dataset**
- **Platform**: Multiple sources
- **URL**: http://mmlab.ie.cuhk.edu.hk/projects/CelebA.html
- **Features**: Large face dataset (200k+ images)
- **Free**: ✅ Yes
- **Note**: Can extract skin-related features, but needs labeling
- **Use Case**: Good for data augmentation

#### 8. **FFHQ (Flickr-Faces-HQ)**
- **URL**: https://github.com/NVlabs/ffhq-dataset
- **Features**: High-quality face images
- **Free**: ✅ Yes (research use)
- **Note**: Need to add skin analysis labels

---

## 🎯 Rekomendasi Dataset untuk Skinovate

### **Best Starting Point:**

1. **Start dengan HAM10000** (Kaggle)
   - ✅ Large dataset (10k+ images)
   - ✅ Well-labeled
   - ✅ Free & Public Domain
   - ⚠️ Filter untuk face images
   - ⚠️ Add custom labels untuk skin type, acne, etc.

2. **Combine dengan Kaggle Skin Type Datasets**
   - Search dan download 2-3 skin type datasets
   - Combine into one dataset
   - Add multi-label annotations

3. **Supplement dengan ISIC Archive**
   - Download additional high-quality images
   - Use for training/validation

### **Dataset Combination Strategy:**

```
Final Dataset Structure:
├── train/ (70%)
│   ├── oily/
│   ├── dry/
│   ├── combination/
│   └── ...
├── val/ (15%)
└── test/ (15%)

Multi-label CSV:
- image_path
- skin_type
- acne_type
- acne_scars
- dark_spots
- wrinkles
- pores
- texture
- hydration
- redness
- uneven_tone
- overall_score
```

---

## 📥 Cara Download Dataset

### Kaggle Datasets:
```bash
# 1. Install Kaggle API
pip install kaggle

# 2. Setup API credentials (download from Kaggle account settings)
# Place kaggle.json in ~/.kaggle/

# 3. Download dataset
kaggle datasets download -d kmader/skin-cancer-mnist-ham10000

# 4. Extract
unzip skin-cancer-mnist-ham10000.zip
```

### ISIC Archive:
1. Register at https://www.isic-archive.com/
2. Browse datasets
3. Download via web interface or API
4. Filter for relevant images

---

## 📊 Dataset Requirements

### Minimum Dataset Size:
- **Per Class**: 500-1,000 images minimum
- **Ideal**: 2,000-5,000 images per class
- **Total**: 10,000-50,000 images for good model

### Data Quality Requirements:
- ✅ Clear face images
- ✅ Good lighting
- ✅ Various angles (but front-facing preferred)
- ✅ Different skin tones
- ✅ Various ages
- ✅ Multiple conditions per image (multi-label)

---

## 🔄 Data Collection Strategy

### Phase 1: Public Datasets (Now)
1. Download HAM10000
2. Download 2-3 Kaggle skin type datasets
3. Download ISIC Archive images
4. Combine & clean datasets

### Phase 2: Manual Labeling (1-2 months)
1. Use labeling tools (LabelImg, CVAT, Labelbox)
2. Add multi-label annotations
3. Focus on: skin_type, acne, scars, dark_spots, wrinkles, etc.

### Phase 3: Data Augmentation (Ongoing)
1. Rotations, flips, brightness adjustments
2. Increase dataset size 2-3x
3. Improve model robustness

### Phase 4: Own Data Collection (Future)
1. In-app data collection feature
2. User contributions (with consent)
3. Expert labeling (dermatologists)

---

## 🛠️ Tools untuk Dataset Preparation

### Labeling Tools:
1. **LabelImg** (Free)
   - Simple bounding box labeling
   - https://github.com/heartexlabs/labelImg

2. **CVAT** (Free, Open Source)
   - Advanced annotation tool
   - Multi-label support
   - https://github.com/openvinotoolkit/cvat

3. **Labelbox** (Paid, but has free tier)
   - Professional labeling platform
   - https://labelbox.com/

### Data Processing:
- Python + OpenCV
- Pandas untuk CSV management
- TensorFlow/Keras untuk preprocessing

---

## 📝 Next Steps

1. ✅ Download HAM10000 dataset
2. ✅ Download 2-3 Kaggle skin type datasets
3. ⏳ Combine & organize datasets
4. ⏳ Manual labeling untuk multi-label annotations
5. ⏳ Data augmentation
6. ⏳ Train/Val/Test split
7. ⏳ Model training preparation

---

## 🔗 Quick Links

- **Kaggle Datasets**: https://www.kaggle.com/datasets
- **HAM10000**: https://www.kaggle.com/datasets/kmader/skin-cancer-mnist-ham10000
- **ISIC Archive**: https://www.isic-archive.com/
- **DermNet NZ**: https://dermnetnz.org/image-library/
- **Kaggle API Docs**: https://www.kaggle.com/docs/api

