"""
Script untuk download dan organize datasets untuk Skin Analysis App
Mendukung download dari Kaggle, ISIC Archive, dan organize dataset structure
"""

import os
import json
import zipfile
import shutil
import pandas as pd
from pathlib import Path
import requests
from typing import List, Dict, Optional

# Kaggle API setup (install: pip install kaggle)
try:
    import kaggle
    KAGGLE_AVAILABLE = True
except ImportError:
    KAGGLE_AVAILABLE = False
    print("⚠️  Kaggle not installed. Install with: pip install kaggle")


class DatasetDownloader:
    """Class untuk download dan organize datasets"""
    
    def __init__(self, output_dir: str = "datasets"):
        self.output_dir = Path(output_dir)
        self.output_dir.mkdir(exist_ok=True)
        
        # Dataset structure
        self.raw_dir = self.output_dir / "raw"
        self.processed_dir = self.output_dir / "processed"
        self.annotations_dir = self.output_dir / "annotations"
        
        # Create directories
        self.raw_dir.mkdir(exist_ok=True)
        self.processed_dir.mkdir(exist_ok=True)
        self.annotations_dir.mkdir(exist_ok=True)
    
    def download_kaggle_dataset(self, dataset_name: str, unzip: bool = True) -> Optional[str]:
        """
        Download dataset dari Kaggle
        
        Args:
            dataset_name: Format 'username/dataset-name' atau dataset slug
            unzip: Extract zip file setelah download
            
        Returns:
            Path ke downloaded dataset
        """
        if not KAGGLE_AVAILABLE:
            print("❌ Kaggle API not available. Please install: pip install kaggle")
            print("   And setup credentials: https://www.kaggle.com/docs/api")
            return None
        
        print(f"📥 Downloading Kaggle dataset: {dataset_name}")
        
        try:
            # Download dataset
            dataset_dir = self.raw_dir / dataset_name.replace("/", "_")
            dataset_dir.mkdir(exist_ok=True)
            
            kaggle.api.dataset_download_files(
                dataset_name,
                path=str(dataset_dir),
                unzip=unzip
            )
            
            print(f"✅ Downloaded to: {dataset_dir}")
            return str(dataset_dir)
            
        except Exception as e:
            print(f"❌ Error downloading {dataset_name}: {str(e)}")
            return None
    
    def download_ham10000(self) -> Optional[str]:
        """Download HAM10000 dataset (recommended)"""
        return self.download_kaggle_dataset("kmader/skin-cancer-mnist-ham10000")
    
    def organize_images_by_class(self, source_dir: str, label_csv: Optional[str] = None):
        """
        Organize images by class/label
        
        Args:
            source_dir: Directory containing images
            label_csv: Optional CSV file with image labels
        """
        source_path = Path(source_dir)
        if not source_path.exists():
            print(f"❌ Source directory not found: {source_dir}")
            return
        
        print(f"📁 Organizing images from: {source_dir}")
        
        # If CSV provided, use it for organization
        if label_csv and Path(label_csv).exists():
            df = pd.read_csv(label_csv)
            print(f"📊 Found {len(df)} labeled images")
            
            # Organize by class
            for _, row in df.iterrows():
                image_path = source_path / row.get('image', row.get('image_path', ''))
                if not image_path.exists():
                    continue
                
                # Get class/label (adjust column name as needed)
                class_name = row.get('class', row.get('label', row.get('dx', 'unknown')))
                class_dir = self.processed_dir / str(class_name)
                class_dir.mkdir(exist_ok=True)
                
                # Copy image
                shutil.copy2(image_path, class_dir / image_path.name)
        
        else:
            # Simple organization: move all images to processed/images
            images_dir = self.processed_dir / "images"
            images_dir.mkdir(exist_ok=True)
            
            # Find all image files
            image_extensions = ['.jpg', '.jpeg', '.png', '.bmp']
            image_count = 0
            
            for ext in image_extensions:
                for img_file in source_path.rglob(f"*{ext}"):
                    shutil.copy2(img_file, images_dir / img_file.name)
                    image_count += 1
            
            print(f"✅ Organized {image_count} images to {images_dir}")
    
    def create_labeling_template(self, images_dir: str, output_csv: str = "labeling_template.csv"):
        """
        Create CSV template untuk manual labeling
        
        Args:
            images_dir: Directory containing images to label
            output_csv: Output CSV file path
        """
        images_path = Path(images_dir)
        if not images_path.exists():
            print(f"❌ Images directory not found: {images_dir}")
            return
        
        # Find all images
        image_extensions = ['.jpg', '.jpeg', '.png', '.bmp']
        image_files = []
        
        for ext in image_extensions:
            image_files.extend(list(images_path.rglob(f"*{ext}")))
        
        if not image_files:
            print(f"❌ No images found in: {images_dir}")
            return
        
        print(f"📝 Creating labeling template for {len(image_files)} images")
        
        # Create template DataFrame
        template_data = []
        for img_file in image_files:
            relative_path = img_file.relative_to(images_path)
            template_data.append({
                'image_path': str(relative_path),
                'skin_type': '',  # oily, dry, combination, sensitive, normal
                'acne_type': '',  # none, blackheads, whiteheads, papules, pustules, cystic, nodular
                'acne_scars': '',  # 0 (no), 1 (yes)
                'acne_scar_severity': '',  # 0 (none), 1 (mild), 2 (moderate), 3 (severe)
                'dark_spots': '',  # 0 (no), 1 (yes)
                'dark_spots_severity': '',  # 0-3
                'wrinkles': '',  # 0 (no), 1 (yes)
                'wrinkle_severity': '',  # 0-3
                'pores': '',  # 0 (not visible), 1 (visible)
                'pore_size': '',  # 0 (small), 1 (medium), 2 (large)
                'texture': '',  # smooth, rough, bumpy, uneven
                'texture_score': '',  # 0-100
                'hydration': '',  # 0 (severely dehydrated), 1 (dehydrated), 2 (moderate), 3 (well-hydrated)
                'hydration_score': '',  # 0-100
                'redness': '',  # 0 (no), 1 (yes)
                'redness_level': '',  # 0-3
                'uneven_tone': '',  # 1-5 (1=very uneven, 5=very even)
                'tone_score': '',  # 0-100
                'sun_damage': '',  # 0 (none), 1 (mild), 2 (moderate), 3 (severe)
                'overall_score': '',  # 0-100
                'notes': ''  # Additional notes
            })
        
        df = pd.DataFrame(template_data)
        output_path = self.annotations_dir / output_csv
        df.to_csv(output_path, index=False)
        
        print(f"✅ Created labeling template: {output_path}")
        print(f"📊 Template contains {len(df)} images")
        print(f"\n📋 Columns to fill:")
        print("   - skin_type: oily, dry, combination, sensitive, normal")
        print("   - acne_type: none, blackheads, whiteheads, papules, pustules, cystic, nodular")
        print("   - acne_scars: 0 or 1")
        print("   - dark_spots: 0 or 1")
        print("   - wrinkles: 0 or 1")
        print("   - pores: 0 or 1")
        print("   - texture: smooth, rough, bumpy, uneven")
        print("   - hydration: 0-3")
        print("   - redness: 0 or 1")
        print("   - uneven_tone: 1-5")
        print("   - sun_damage: 0-3")
        print("   - overall_score: 0-100")
    
    def create_train_val_test_split(self, annotations_csv: str, train_ratio: float = 0.7, 
                                   val_ratio: float = 0.15, test_ratio: float = 0.15):
        """
        Create train/validation/test split dari annotations
        
        Args:
            annotations_csv: Path ke CSV file dengan annotations
            train_ratio: Ratio untuk training set
            val_ratio: Ratio untuk validation set
            test_ratio: Ratio untuk test set
        """
        annotations_path = Path(annotations_csv)
        if not annotations_path.exists():
            print(f"❌ Annotations file not found: {annotations_csv}")
            return
        
        df = pd.read_csv(annotations_path)
        
        # Shuffle
        df = df.sample(frac=1, random_state=42).reset_index(drop=True)
        
        # Calculate split indices
        total = len(df)
        train_end = int(total * train_ratio)
        val_end = train_end + int(total * val_ratio)
        
        # Split
        train_df = df[:train_end]
        val_df = df[train_end:val_end]
        test_df = df[val_end:]
        
        # Save splits
        train_path = self.annotations_dir / "train.csv"
        val_path = self.annotations_dir / "val.csv"
        test_path = self.annotations_dir / "test.csv"
        
        train_df.to_csv(train_path, index=False)
        val_df.to_csv(val_path, index=False)
        test_df.to_csv(test_path, index=False)
        
        print(f"✅ Created train/val/test splits:")
        print(f"   Training: {len(train_df)} images ({len(train_df)/total*100:.1f}%) -> {train_path}")
        print(f"   Validation: {len(val_df)} images ({len(val_df)/total*100:.1f}%) -> {val_path}")
        print(f"   Test: {len(test_df)} images ({len(test_df)/total*100:.1f}%) -> {test_path}")


def main():
    """Main function untuk download datasets"""
    print("🚀 Skin Analysis Dataset Downloader")
    print("=" * 50)
    
    downloader = DatasetDownloader()
    
    # Download HAM10000 dataset (recommended)
    print("\n1️⃣  Downloading HAM10000 dataset...")
    ham10000_path = downloader.download_ham10000()
    
    if ham10000_path:
        print(f"\n📁 Organizing HAM10000 dataset...")
        # HAM10000 biasanya memiliki metadata CSV
        metadata_csv = Path(ham10000_path) / "HAM10000_metadata.csv"
        if metadata_csv.exists():
            downloader.organize_images_by_class(ham10000_path, str(metadata_csv))
        else:
            downloader.organize_images_by_class(ham10000_path)
    
    # Create labeling template
    print("\n2️⃣  Creating labeling template...")
    images_dir = downloader.processed_dir / "images"
    if images_dir.exists():
        downloader.create_labeling_template(str(images_dir), "labeling_template.csv")
    
    print("\n✅ Dataset download and organization complete!")
    print(f"\n📂 Dataset structure:")
    print(f"   Raw datasets: {downloader.raw_dir}")
    print(f"   Processed images: {downloader.processed_dir}")
    print(f"   Annotations: {downloader.annotations_dir}")
    
    print("\n📋 Next steps:")
    print("   1. Review and fill labeling_template.csv")
    print("   2. Run create_train_val_test_split() after labeling")
    print("   3. Start model training with labeled data")


if __name__ == "__main__":
    # Example usage
    downloader = DatasetDownloader()
    
    # Download HAM10000
    print("Downloading HAM10000...")
    ham_path = downloader.download_ham10000()
    
    # Organize images
    if ham_path:
        downloader.organize_images_by_class(ham_path)
    
    # Create labeling template
    images_dir = downloader.processed_dir / "images"
    if images_dir.exists():
        downloader.create_labeling_template(str(images_dir))

