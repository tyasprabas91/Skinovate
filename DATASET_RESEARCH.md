# Dataset Research untuk Skin Analysis App

## Fitur-fitur yang Diperlukan untuk TensorFlow Lite Model

Berdasarkan analisis aplikasi Skinovate, berikut adalah fitur-fitur yang relevan:

### ✅ Sudah Ada di App:
1. **Skin Type** (Oily, Dry, Combination, Sensitive, Normal)
2. **Acne Percentage** (0-100%)
3. **Acne Type** (Blackheads, Whiteheads, Cystic, etc.)
4. **Acne Scars** (Yes/No, Severity)
5. **Dryness Percentage** (0-100%)
6. **Overall Skin Score** (0-100)

### ➕ Fitur Tambahan yang Direkomendasikan:
1. **Dark Spots / Hyperpigmentation**
   - Detection & percentage
   - Common untuk semua skin types

2. **Wrinkles / Fine Lines**
   - Age-related skin concerns
   - Severity level

3. **Pore Size / Visibility**
   - Large pores indicator
   - Texture analysis

4. **Skin Texture**
   - Smooth, Rough, Bumpy
   - Texture quality score

5. **Hydration Level**
   - Dehydrated skin detection
   - Moisture level

6. **Redness / Inflammation**
   - Sensitivity indicator
   - Rosacea-like conditions

7. **Uneven Skin Tone**
   - Color uniformity
   - Tone evenness score

8. **Sun Damage**
   - UV damage indicators
   - Age spots

---

## Dataset Gratis yang Relevan

### 🎯 Dataset Utama (Recommended)

#### 1. **Kaggle - Skin Type Dataset**
- **URL**: https://www.kaggle.com/datasets
- **Search**: "skin type classification", "face skin dataset"
- **Features**: Skin types, basic classifications
- **Size**: Varies
- **Free**: ✅ Yes
- **Format**: Images with labels

#### 2. **HAM10000 Dataset** (Medical, but useful)
- **URL**: https://www.kaggle.com/datasets/kmader/skin-cancer-mnist-ham10000
- **Features**: Skin lesions, 7 classes
- **Size**: 10,015 images
- **Free**: ✅ Yes
- **Note**: More medical-focused, but good for texture analysis

#### 3. **ISIC Archive**
- **URL**: https://www.isic-archive.com/
- **Features**: Dermatology images, various skin conditions
- **Size**: Large collection
- **Free**: ✅ Yes (with registration)
- **Note**: Medical/research focused

#### 4. **DermNet NZ**
- **URL**: https://dermnetnz.org/
- **Features**: Skin conditions, educational images
- **Free**: ✅ Yes (for educational use)
- **Note**: Check license for commercial use

### 📊 Dataset untuk Fitur Spesifik

#### Untuk Acne Detection:
- **Acne Dataset** - Kaggle
- Search: "acne detection", "acne classification"
- Features: Acne types, severity levels

#### Untuk Wrinkles/Fine Lines:
- **Age Estimation Datasets**
- Search: "facial age dataset", "wrinkle detection"
- Features: Age-related skin changes

#### Untuk Dark Spots/Hyperpigmentation:
- **Skin Lesion Datasets**
- Combine with: HAM10000, ISIC Archive
- Features: Pigmentation variations

#### Untuk Texture Analysis:
- **Face Texture Datasets**
- Search: "face texture dataset", "skin texture"
- Features: Skin surface quality

---

## Rekomendasi Dataset untuk Skinovate

### Priority 1: Multi-label Classification Dataset
**Ideal Dataset Structure:**
```
images/
├── image_001.jpg
├── image_002.jpg
└── ...

labels.csv:
image_path, skin_type, acne_level, acne_scars, dark_spots, wrinkles, pores, texture, hydration, redness, tone_evenness, overall_score
image_001.jpg, oily, 2, 1, 0, 0, 2, smooth, 3, 0, 4, 85
image_002.jpg, dry, 0, 0, 1, 2, 1, rough, 2, 1, 3, 70
```

### Priority 2: Combine Multiple Datasets
Karena tidak ada dataset "perfect" yang mencakup semua fitur, strategi terbaik:

1. **Kaggle Datasets** (Skin Type + Acne)
   - Combine 2-3 datasets
   - Manual labeling untuk fitur tambahan

2. **Create Custom Dataset**
   - Start dengan public datasets
   - Augment dengan own data collection
   - Label incrementally

---

## Dataset Links (Gratis)

### Kaggle Datasets (Recommended):
1. Skin Type Classification
   - Search: "skin type dataset"
   - Multiple options available

2. Acne Detection
   - Search: "acne classification dataset"
   
3. Face Analysis
   - Search: "facial skin analysis"
   - "face quality dataset"

4. General Face Datasets (can be adapted)
   - CelebA (for face structure)
   - FFHQ (Face quality)

### Academic/Research Datasets:
1. **ISIC Archive**: https://www.isic-archive.com/
2. **DermNet NZ**: https://dermnetnz.org/
3. **Med-Node**: Medical image datasets

---

## Data Preparation Strategy

### Step 1: Collect Base Datasets
- Download 2-3 Kaggle datasets
- Filter untuk face skin images
- Clean & organize

### Step 2: Multi-label Annotation
- Use labeling tools (LabelImg, CVAT)
- Create multi-label annotations
- Focus on: skin_type, acne, scars, dark_spots, wrinkles, etc.

### Step 3: Data Augmentation
- Rotations, flips, brightness adjustments
- Increase dataset size 2-3x

### Step 4: Train/Val/Test Split
- 70% Train
- 15% Validation  
- 15% Test

---

## Next Steps

1. ✅ Research & identify best free datasets
2. ⏳ Download & explore datasets
3. ⏳ Clean & preprocess data
4. ⏳ Multi-label annotation
5. ⏳ Model training preparation

