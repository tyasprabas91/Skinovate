# Dataset Download & Organization Guide

Scripts dan templates untuk download, organize, dan label datasets untuk Skin Analysis App.

## 📁 File Structure

```
.
├── scripts/
│   ├── download_datasets.py      # Script untuk download datasets
│   ├── labeling_helper.py        # Validator untuk labeling CSV
│   └── requirements_datasets.txt # Python dependencies
├── templates/
│   ├── labeling_template.csv     # CSV template untuk labeling
│   └── labeling_guide.md         # Panduan lengkap untuk labeling
└── datasets/                     # Dataset directory (created by script)
    ├── raw/                      # Raw downloaded datasets
    ├── processed/                # Processed/organized images
    └── annotations/              # Labeling CSV files
```

## 🚀 Quick Start

### 1. Install Dependencies

```bash
pip install -r scripts/requirements_datasets.txt
```

### 2. Setup Kaggle API (untuk download datasets)

1. Create account di https://www.kaggle.com/
2. Go to Account settings → API → Create New Token
3. Download `kaggle.json`
4. Place di `~/.kaggle/kaggle.json` (Linux/Mac) atau `C:\Users\<username>\.kaggle\kaggle.json` (Windows)

### 3. Download Datasets

```bash
python scripts/download_datasets.py
```

Script akan:
- Download HAM10000 dataset dari Kaggle
- Organize images
- Create labeling template CSV

### 4. Manual Labeling

1. Open `datasets/annotations/labeling_template.csv`
2. Fill semua columns sesuai `templates/labeling_guide.md`
3. Save progress regularly

### 5. Validate Labeling

```bash
python scripts/labeling_helper.py datasets/annotations/labeling_template.csv
```

### 6. Create Train/Val/Test Split

```python
from scripts.download_datasets import DatasetDownloader

downloader = DatasetDownloader()
downloader.create_train_val_test_split(
    "datasets/annotations/labeling_template.csv",
    train_ratio=0.7,
    val_ratio=0.15,
    test_ratio=0.15
)
```

## 📋 Labeling Template Format

CSV dengan columns:
- `image_path` - Path ke image file
- `skin_type` - oily, dry, combination, sensitive, normal
- `acne_type` - none, blackheads, whiteheads, papules, pustules, cystic, nodular
- `acne_scars` - 0 (no), 1 (yes)
- `acne_scar_severity` - 0-3
- `dark_spots` - 0 (no), 1 (yes)
- `dark_spots_severity` - 0-3
- `wrinkles` - 0 (no), 1 (yes)
- `wrinkle_severity` - 0-3
- `pores` - 0 (not visible), 1 (visible)
- `pore_size` - 0 (small), 1 (medium), 2 (large)
- `texture` - smooth, rough, bumpy, uneven
- `texture_score` - 0-100
- `hydration` - 0-3
- `hydration_score` - 0-100
- `redness` - 0 (no), 1 (yes)
- `redness_level` - 0-3
- `uneven_tone` - 1-5
- `tone_score` - 0-100
- `sun_damage` - 0-3
- `overall_score` - 0-100
- `notes` - Optional notes

Lihat `templates/labeling_guide.md` untuk panduan lengkap.

## 🛠️ Scripts Documentation

### download_datasets.py

Main script untuk download dan organize datasets.

**Functions:**
- `download_kaggle_dataset(dataset_name)` - Download dataset dari Kaggle
- `download_ham10000()` - Download HAM10000 dataset
- `organize_images_by_class(source_dir, label_csv)` - Organize images by class
- `create_labeling_template(images_dir, output_csv)` - Create CSV template
- `create_train_val_test_split(annotations_csv, ...)` - Create train/val/test split

**Usage:**
```python
from scripts.download_datasets import DatasetDownloader

downloader = DatasetDownloader()

# Download HAM10000
downloader.download_ham10000()

# Create labeling template
downloader.create_labeling_template("datasets/processed/images", "labeling_template.csv")
```

### labeling_helper.py

Validator untuk check quality dan consistency labeling CSV.

**Usage:**
```bash
python scripts/labeling_helper.py datasets/annotations/labeling_template.csv
```

**Output:**
- Statistics tentang dataset
- Error reports
- Warning reports
- Validation status

## 📊 Dataset Requirements

- **Minimum**: 1,000 labeled images
- **Ideal**: 5,000-10,000 labeled images
- **Distribution**: Balanced across classes
- **Quality**: Consistent labeling, validated

## 🔗 Dataset Sources

1. **HAM10000** - https://www.kaggle.com/datasets/kmader/skin-cancer-mnist-ham10000
2. **Kaggle Skin Type Datasets** - Search "skin type classification"
3. **ISIC Archive** - https://www.isic-archive.com/

## 📝 Next Steps

1. ✅ Download datasets
2. ✅ Create labeling template
3. ⏳ Manual labeling (1-2 months)
4. ⏳ Validate labels
5. ⏳ Create train/val/test split
6. ⏳ Model training preparation

## ❓ FAQ

**Q: Berapa banyak images yang perlu di-label?**
A: Minimum 1,000, ideal 5,000-10,000 untuk good model.

**Q: Bagaimana cara label efficiently?**
A: Use spreadsheet (Excel/Google Sheets), label in batches, take breaks.

**Q: Apa jika tidak yakin dengan label?**
A: Skip atau mark as uncertain, better accurate than guessing.

**Q: Bisa pakai multiple labelers?**
A: Yes, tapi perlu consistency check dan inter-annotator agreement.

**Q: Berapa lama proses labeling?**
A: 1-2 months untuk 5,000 images (depending on labeler experience).

